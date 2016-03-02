package theokanning.rover.chat.quickblox;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
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
import theokanning.rover.chat.listener.ChatListener;
import theokanning.rover.chat.listener.DriverChatListener;
import theokanning.rover.chat.listener.RobotChatListener;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.chat.model.Message;

/**
 * Quickblox implementation of {@link RobotChatClient} and {@link DriverChatClient}
 *
 * @author Theo Kanning
 */
public abstract class QuickBloxChatClient implements RobotChatClient, DriverChatClient {

    private static final String TAG = "QuickBloxChatClient";

    private Context context;

    private QBPrivateChat privateChat;
    private QBRTCSession currentSession;

    private ChatListener chatListener;
    private RobotChatListener robotChatCallbackListener;
    private DriverChatListener driverChatCallbackListener;

    QBMessageListener<QBPrivateChat> privateChatMessageListener = new QBMessageListener<QBPrivateChat>() {
        @Override
        public void processMessage(QBPrivateChat privateChat, final QBChatMessage chatMessage) {
            String body = chatMessage.getBody();
            Gson gson = new Gson();
            Message message = gson.fromJson(body, Message.class);
            chatListener.onChatMessageReceived(message);
        }

        @Override
        public void processError(QBPrivateChat privateChat, QBChatException error, QBChatMessage originMessage) {
            Log.e(TAG, error.getMessage());
        }
    };

    QBRTCClientSessionCallbacks sessionCallbacks = new QBRTCClientSessionCallbacks() {
        //todo create a new class implements callbacks, then extend that class and only override those that are needed
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
            chatListener.onSessionEnded();
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

    private void enableReceivingPrivateChats() {
        QBPrivateChatManagerListener privateChatManagerListener = (incomingPrivateChat, createdLocally) -> {
            if (!createdLocally) {
                privateChat = incomingPrivateChat;
                privateChat.addMessageListener(privateChatMessageListener);
            }
        };
        QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();
        privateChatManager.addPrivateChatManagerListener(privateChatManagerListener);
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

    private void startPrivateChat() {
        Integer opponentId = getOpponent().getId();
        privateChat = QBChatService.getInstance()
                .getPrivateChatManager()
                .createChat(opponentId, privateChatMessageListener);
    }

    @Override
    public void sendMessage(Message message) {
        if (privateChat == null) {
            startPrivateChat();
        }
        trySendingMessage(message);
    }

    private void trySendingMessage(Message message) {
        Gson gson = new Gson();
        String messageString = gson.toJson(message);
        try {
            QBChatMessage chatMessage = new QBChatMessage();
            chatMessage.setBody(messageString);
            privateChat.sendMessage(chatMessage);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            //do nothing
        }
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

    @Override
    public void registerChatCallbackListener(ChatListener chatListener) {
        this.chatListener = chatListener;
    }

    @Override
    public void unregisterChatCallbackListener() {
        chatListener = null;
    }

    @Override
    public void registerRobotChatCallbackListener(RobotChatListener listener) {
        this.robotChatCallbackListener = listener;
    }

    @Override
    public void unregisterRobotChatCallbackListener() {
        this.robotChatCallbackListener = null;
    }

    @Override
    public void registerDriverChatCallbackListener(DriverChatListener listener) {
        this.driverChatCallbackListener = listener;
    }

    @Override
    public void unregisterDriverChatCallbackListener() {
        this.driverChatCallbackListener = null;
    }

    protected abstract QBUser getUser();

    protected abstract QBUser getOpponent();
}
