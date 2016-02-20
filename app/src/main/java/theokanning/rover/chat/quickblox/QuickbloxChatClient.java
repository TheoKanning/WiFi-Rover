package theokanning.rover.chat.quickblox;

import android.content.Context;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBVideoChatWebRTCSignalingManager;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

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
public class QuickBloxChatClient implements RobotChatClient, DriverChatClient {

    private static final String TAG = "QuickBloxChatClient";

    private Context context;

    private QBPrivateChat privateChat;

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

    private Observable<Boolean> loginAsUser(final User user, final Context context) {
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

    public void initVideoChatClient() {
        QBRTCClient.getInstance(context).prepareToProcessCalls();
        QBRTCClient.getInstance(context).addSessionCallbacksListener(sessionCallbacks);
        QBRTCConfig.setAnswerTimeInterval(10);
        enableReceivingVideoCalls();
    }

    private void enableReceivingVideoCalls() {
        QBVideoChatWebRTCSignalingManager signalingManager = QBChatService.getInstance()
                .getVideoChatWebRTCSignalingManager();

        signalingManager.addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if (!createdLocally) {
                    QBRTCClient.getInstance(context).addSignaling((QBWebRTCSignaling) qbSignaling);
                }
            }
        });
    }

    @Override
    public Observable<Boolean> loginAsRobot(Context context) {
        this.context = context;
        User user = User.ROBOT;
        return loginAsUser(user, context);
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
    public void sendMessageToDriver(String message) {
        //todo consider making this abstract and forcing subclasses to implement getUserId/getOpponentId()?
        if (privateChat == null) {
            startPrivateChatWithDriver();
        }
        sendMessage(message);
    }

    @Override
    public Observable<Boolean> loginAsDriver(Context context) {
        this.context = context;
        User user = User.DRIVER;
        return loginAsUser(user, context);
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
    public void sendMessageToRobot(String message) {
        if (privateChat == null) {
            startPrivateChatWithRobot();
        }
        sendMessage(message);
    }

    private void startPrivateChatWithDriver() {
        Integer opponentId = User.DRIVER.getId();
        startPrivateChat(opponentId);
    }

    private void startPrivateChatWithRobot() {
        Integer opponentId = User.ROBOT.getId();
        startPrivateChat(opponentId);
    }

    private void startPrivateChat(Integer opponentId) {
        privateChat = QBChatService.getInstance()
                .getPrivateChatManager()
                .createChat(opponentId, privateChatMessageListener);
    }

    private void sendMessage(String message) {
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


}
