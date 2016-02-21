package theokanning.rover.chat.quickblox;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.QBVideoChatWebRTCSignalingManager;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import theokanning.rover.chat.callback.ChatCallbackListener;
import theokanning.rover.chat.callback.DriverChatCallbackListener;
import theokanning.rover.chat.callback.RobotChatCallbackListener;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.user.User;

/**
 * Quickblox implementation of {@link RobotChatClient} and {@link RobotChatClient}
 *
 * @author Theo Kanning
 */
public abstract class QuickBloxChatClient implements RobotChatClient, DriverChatClient {

    private static final String TAG = "QuickBloxChatClient";

    private Context context;

    private QBPrivateChat privateChat;
    private QBRTCSession currentSession;

    private ChatCallbackListener chatCallbackListener;
    private RobotChatCallbackListener robotChatCallbackListener;
    private DriverChatCallbackListener driverChatCallbackListener;

    @Override
    public void registerChatCallbackListener(ChatCallbackListener chatCallbackListener) {
        this.chatCallbackListener = chatCallbackListener;
    }

    @Override
    public void unregisterChatCallbackListener() {
        chatCallbackListener = null;
    }

    @Override
    public void endCall() {
        if (currentSession != null) {
            currentSession.hangUp(null);
        }
    }

    @Override
    public void setMicrophoneEnabled(boolean enabled) {
        if (currentSession != null) {
            currentSession.getMediaStreamManager().setAudioEnabled(enabled);
        }
    }

    public void enableChatServices() {
        //todo make it more clear where this is being called
        QBRTCClient.getInstance(context).prepareToProcessCalls();
        QBRTCClient.getInstance(context).addSessionCallbacksListener(sessionCallbacks);
        QBRTCConfig.setAnswerTimeInterval(10);
        enableReceivingVideoCalls();
        enableReceivingPrivateChats();
    }

    private void enableReceivingVideoCalls() {
        QBVideoChatWebRTCSignalingManager signalingManager = QBChatService.getInstance()
                .getVideoChatWebRTCSignalingManager();

        signalingManager.addSignalingManagerListener((qbSignaling, createdLocally) -> {
            if (!createdLocally) {
                QBRTCClient.getInstance(context).addSignaling((QBWebRTCSignaling) qbSignaling);
            }
        });
    }

    @Override
    public Observable<Boolean> login(final Context context) {
        this.context = context;
        final QBUser user = getUser();
        Observable<Boolean> observable = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                QuickBloxLoginTask task = new QuickBloxLoginTask(user, context,
                        new LoginTaskSubscriberAdapter(subscriber, QuickBloxChatClient.this));
                task.execute();
            }
        });

        return observable;
    }

    @Override
    public void registerRobotChatCallbackListener(RobotChatCallbackListener listener) {
        this.robotChatCallbackListener = listener;
    }

    @Override
    public void unregisterRobotChatCallbackListener() {
        this.robotChatCallbackListener = null;
    }

    @Override
    public void sendMessage(String message) {
        if (privateChat == null) {
            startPrivateChat();
        }
        trySendingMessage(message);
    }

    @Override
    public void startCall() {
        List<Integer> ids = new ArrayList<>();
        ids.add(User.ROBOT.getId());

        currentSession = QBRTCClient.getInstance(context).createNewSessionWithOpponents(ids,
                QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);

        Log.d(TAG, "Starting call");
        currentSession.startCall(currentSession.getUserInfo());
    }

    @Override
    public void registerDriverChatCallbackListener(DriverChatCallbackListener listener) {
        this.driverChatCallbackListener = listener;
    }

    @Override
    public void unregisterDriverChatCallbackListener() {
        this.driverChatCallbackListener = null;
    }


    @Override
    public void registerQbVideoCallbacksListener(QBRTCClientVideoTracksCallbacks callbacks) {
        if (currentSession != null) {
            currentSession.addVideoTrackCallbacksListener(callbacks);
        }
    }

    @Override
    public void unregisterQbVideoCallbacksListener(QBRTCClientVideoTracksCallbacks callbacks) {
        if (currentSession != null) {
            currentSession.removeVideoTrackCallbacksListener(callbacks);
        }
    }

    private void startPrivateChat() {
        Integer opponentId = getOpponent().getId();
        privateChat = QBChatService.getInstance()
                .getPrivateChatManager()
                .createChat(opponentId, privateChatMessageListener);
    }

    private void enableReceivingPrivateChats(){
        QBPrivateChatManagerListener privateChatManagerListener = (incomingPrivateChat, createdLocally) -> {
            if (!createdLocally) {
                privateChat = incomingPrivateChat;
                privateChat.addMessageListener(privateChatMessageListener);
            }
        };
        QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();
        privateChatManager.addPrivateChatManagerListener(privateChatManagerListener);
    }

    private void trySendingMessage(String message) {
        try {
            QBChatMessage chatMessage = new QBChatMessage();
            chatMessage.setBody(message);
            privateChat.sendMessage(chatMessage);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            //do nothing
        }
    }

    QBRTCClientSessionCallbacks sessionCallbacks = new QBRTCClientSessionCallbacks() {
        @Override
        public void onReceiveNewSession(QBRTCSession qbrtcSession) {
            ((Activity) context).runOnUiThread(() -> {
                qbrtcSession.acceptCall(new HashMap<>());
                currentSession = qbrtcSession;
            });

            robotChatCallbackListener.onCallReceived();
        }

        @Override
        public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
            driverChatCallbackListener.onCallNotAnswered();
        }

        @Override
        public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

        }

        @Override
        public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
            driverChatCallbackListener.onCallAnswered();
        }

        @Override
        public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer) {
            chatCallbackListener.onSessionEnded();
        }

        @Override
        public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {

        }

        @Override
        public void onSessionClosed(QBRTCSession qbrtcSession) {

        }

        @Override
        public void onSessionStartClose(QBRTCSession qbrtcSession) {

        }
    };

    QBMessageListener<QBPrivateChat> privateChatMessageListener = new QBMessageListener<QBPrivateChat>() {
        @Override
        public void processMessage(QBPrivateChat privateChat, final QBChatMessage chatMessage) {
            chatCallbackListener.onChatMessageReceived(chatMessage.getBody());
        }

        @Override
        public void processError(QBPrivateChat privateChat, QBChatException error, QBChatMessage originMessage) {
            Log.e(TAG, error.getMessage());
        }
    };

    protected abstract QBUser getUser();

    protected abstract QBUser getOpponent();
}
