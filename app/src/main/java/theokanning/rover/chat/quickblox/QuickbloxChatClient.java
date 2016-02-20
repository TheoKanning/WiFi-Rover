package theokanning.rover.chat.quickblox;

import android.content.Context;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBVideoChatWebRTCSignalingManager;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;

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
public class QuickBloxChatClient implements RobotChatClient, DriverChatClient{

    private Context context;

    private ChatCallbackListener chatCallbackListener;
    private RobotChatCallbackListener robotChatCallbackListener;
    private DriverChatCallbackListener driverChatCallbackListener;

    @Override
    public void sendChatMessage(String message) {

    }

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
                        new LoginTaskSubscriberAdapter(subscriber));
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

    /**
     * Adapts {@link QuickBloxLoginTask.LoginTaskCallback} events to a subscriber
     */
    private class LoginTaskSubscriberAdapter implements QuickBloxLoginTask.LoginTaskCallback {
        private Subscriber<? super Boolean> subscriber;

        public LoginTaskSubscriberAdapter(Subscriber<? super Boolean> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onSuccess() {
            subscriber.onNext(true);
            subscriber.onCompleted();
            QuickBloxChatClient.this.initVideoChatClient();
        }

        @Override
        public void onFailure() {
            subscriber.onNext(false);
            subscriber.onCompleted();
        }
    }
}
