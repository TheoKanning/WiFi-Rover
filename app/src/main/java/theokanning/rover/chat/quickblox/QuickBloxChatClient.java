package theokanning.rover.chat.quickblox;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBVideoChatWebRTCSignalingManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.chat.listener.ChatListener;
import theokanning.rover.chat.listener.DriverChatListener;
import theokanning.rover.chat.listener.RobotChatListener;
import theokanning.rover.chat.model.Message;

/**
 * Quickblox implementation of {@link RobotChatClient} and {@link DriverChatClient}
 *
 * @author Theo Kanning
 */
public abstract class QuickBloxChatClient implements RobotChatClient, DriverChatClient {

    private static final String TAG = "QuickBloxChatClient";

    private QBRTCClient qbrtcClient;

    private QBRTCSession currentSession;
    private QBChatDialog chatDialog;

    private ChatListener chatListener;
    private RobotChatListener robotChatCallbackListener;
    private DriverChatListener driverChatCallbackListener;

    QuickBloxChatClient(QBRTCClient qbrtcClient) {
        this.qbrtcClient = qbrtcClient;
    }

    QBChatDialogMessageListener chatMessageListener = new QBChatDialogMessageListener() {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            String body = qbChatMessage.getBody();
            Gson gson = new Gson();
            Message message = gson.fromJson(body, Message.class);
            chatListener.onChatMessageReceived(message);
        }

        @Override
        public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
            Log.e(TAG, "Could not process message", e);
        }
    };

    QBRTCClientSessionCallbacks sessionCallbacks = new QBRTCClientSessionCallbacks() {
        //todo create a new class implements callbacks, then extend that class and only override those that are needed
        @Override
        public void onReceiveNewSession(QBRTCSession qbrtcSession) {
            //todo see if this still needs the UI thread after updating QB dependency
//            ((Activity) context).runOnUiThread(() -> {
                qbrtcSession.acceptCall(new HashMap<>());
                currentSession = qbrtcSession;
//            });
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
        public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
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
        final QBUser user = getUser();

        return Observable.create(subscriber -> {
            QuickBloxLoginService task = new QuickBloxLoginService();
            boolean result = task.login(user, context);
            if (result) {
                enableChatServices();
            }
            subscriber.onNext(result);
            subscriber.onCompleted();
        });
    }

    public void enableChatServices() {
        //todo make it more clear where this is being called
        qbrtcClient.prepareToProcessCalls();
        qbrtcClient.addSessionCallbacksListener(sessionCallbacks);
        QBRTCConfig.setAnswerTimeInterval(10);
        enableReceivingVideoCalls();
        enableReceivingPrivateChats();
    }

    private void enableReceivingVideoCalls() {
        QBVideoChatWebRTCSignalingManager signalingManager = QBChatService.getInstance()
                .getVideoChatWebRTCSignalingManager();

        signalingManager.addSignalingManagerListener((qbSignaling, createdLocally) -> {
            if (!createdLocally) {
                qbrtcClient.addSignaling(qbSignaling);
            }
        });
    }

    private void enableReceivingPrivateChats() {
        QBIncomingMessagesManager privateChatManager = QBChatService.getInstance().getIncomingMessagesManager();
        privateChatManager.addDialogMessageListener(chatMessageListener);
    }

    @Override
    public void startCall() {
        List<Integer> ids = new ArrayList<>();
        ids.add(User.ROBOT.getId());

        currentSession = qbrtcClient.createNewSessionWithOpponents(ids,
                QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);

        Log.d(TAG, "Starting call");
        currentSession.startCall(currentSession.getUserInfo());
    }

    private void startPrivateChat() {
        Integer opponentId = getOpponent().getId();
        QBUser opponent;
        try {
            opponent = QBUsers.getUser(opponentId).perform();
        } catch (QBResponseException e) {
            Log.e(TAG, "Could not load user", e);
            return;
        }

        QBChatDialog dialog = DialogUtils.buildDialog(opponent);
        QBRestChatService.createChatDialog(dialog)
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        QuickBloxChatClient.this.chatDialog = qbChatDialog;
                        qbChatDialog.addMessageListener(chatMessageListener);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e(TAG, "Shit, couldn't start a chat", e);
                    }
                });
    }

    @Override
    public void sendMessage(Message message) {
        if(chatDialog == null) {
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
            chatDialog.sendMessage(chatMessage);
        } catch (SmackException.NotConnectedException e) {
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
