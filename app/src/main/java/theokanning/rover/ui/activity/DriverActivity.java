package theokanning.rover.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.chat.model.QBChatMessage;
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

import javax.inject.Inject;

import rx.Subscriber;
import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.DriverChatClient;
import theokanning.rover.ui.fragment.WaitingFragment;
import theokanning.rover.ui.fragment.driver.ConnectFragment;
import theokanning.rover.ui.fragment.driver.ControlFragment;
import theokanning.rover.user.User;

/**
 * Activity where user controls remote device and watches video stream. Starts by showing
 * instructions to connect to remote device, then connects and shows video stream.
 */
public class DriverActivity extends BaseActivity implements QBRTCClientSessionCallbacks, SteeringListener {
    private static final String TAG = "DriverActivity";

    @Inject
    DriverChatClient driverChatClient;

    private QBRTCSession currentSession;
    private QBPrivateChat privateChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RoverApplication) getApplication()).getComponent().inject(this);
        setContentView(R.layout.activity_driver);

        loginToChatService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        QBRTCClient.getInstance(this).removeSessionsCallbacksListener(this);
    }

    private void loginToChatService(){
        showLoggingInFragment();
        driverChatClient.login().subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean success) {
                if(success){
                    initQbrtcClient();
                    showConnectFragment();
                } else {
                    Intent intent = new Intent(DriverActivity.this, ModeSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    private void initQbrtcClient() {

        QBChatService.getInstance().getVideoChatWebRTCSignalingManager()
                .addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
                    @Override
                    public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                        if (!createdLocally) {
                            QBRTCClient.getInstance(DriverActivity.this).addSignaling((QBWebRTCSignaling) qbSignaling);
                        }
                    }
                });

        QBRTCConfig.setAnswerTimeInterval(10); //Wait for 10 seconds before giving up call
        QBRTCConfig.setDebugEnabled(false);

        QBRTCClient.getInstance(this).addSessionCallbacksListener(this);
        QBRTCClient.getInstance(this).prepareToProcessCalls();
    }

    /**
     * Gets the chat session and sends a text message
     *
     * @param message string to send over chat client
     */
    private void sendChatMessage(String message) {
        if (privateChat == null) {
            //todo this should be in its own method
            Integer opponentId = User.ROBOT.getId();
            privateChat = QBChatService.getInstance()
                    .getPrivateChatManager()
                    .createChat(opponentId, privateChatMessageListener);
        }

        try {
            //todo wrap in method
            QBChatMessage chatMessage = new QBChatMessage();
            chatMessage.setBody(message);
            privateChat.sendMessage(chatMessage);
        } catch (XMPPException e) {

        } catch (SmackException.NotConnectedException e) {

        }
    }
    /**
     * Shows waiting screen when logging in to chat service
     */
    private void showLoggingInFragment() {
        WaitingFragment fragment = WaitingFragment.newInstance( "Logging in to chat service...");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * Shows connect fragment that gives user option to initiate call
     */
    private void showConnectFragment() {
        setFragment(new ConnectFragment(), true);
    }

    /**
     * Shows waiting screen when starting call
     */
    private void showWaitingFragment() {
        WaitingFragment fragment = WaitingFragment.newInstance( "Attempting to connect to robot...");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showControlFragment(){
        ControlFragment fragment = new ControlFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * Starts a connection to the robot user. Called by ConnectFragment.
     */
    public void connect() {
        showWaitingFragment();

        List<Integer> ids = new ArrayList<>();
        ids.add(User.ROBOT.getId());

        currentSession = QBRTCClient.getInstance(this).createNewSessionWithOpponents(ids,
                QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);


        Map<String, String> userInfo = new HashMap<>(); //todo might be unnecessary
        userInfo.put("key", "robot");

        //Start call
        Log.d(TAG, "Starting call");
        currentSession.startCall(currentSession.getUserInfo());
    }

    public void addVideoTrackCallbacksListener(QBRTCClientVideoTracksCallbacks videoTracksCallbacks) {
        if (currentSession != null) {
            currentSession.addVideoTrackCallbacksListener(videoTracksCallbacks);
        }
    }

    public void removeVideoTrackCallbacksListener(QBRTCClientVideoTracksCallbacks callbacks){
        if(currentSession != null){
            currentSession.removeVideoTrackCallbacksListener(callbacks);
        }
    }

    public void setMicrophoneEnabled(boolean enabled){
        if(currentSession != null){
            currentSession.getMediaStreamManager().setAudioEnabled(enabled);
        }
    }

    public void setStreamAudioEnabled(boolean enabled){
        if(currentSession != null){
            currentSession.getMediaStreamManager().switchAudioOutput();
            //switches to headphones to mute sound...
        }
    }

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        //Should not happen
    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        Toast.makeText(this, "Robot did not answer", Toast.LENGTH_SHORT).show();
        showConnectFragment();
    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        Log.e(TAG, "Call rejected, should not happen");
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        if (qbrtcSession != currentSession) {
            Log.e(TAG, "Call accepted for incorrect session");
            return;
        }
        Toast.makeText(this, "Starting video chat", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Connected successfully");
        showControlFragment();
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer) {
        currentSession = null;
        showConnectFragment();
    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {
        Log.e(TAG, "Session closed");
        showConnectFragment();
    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {

    }

    QBMessageListener<QBPrivateChat> privateChatMessageListener = new QBMessageListener<QBPrivateChat>() {
        @Override
        public void processMessage(QBPrivateChat privateChat, final QBChatMessage chatMessage) {
            Toast.makeText(DriverActivity.this, chatMessage.getBody(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void processError(QBPrivateChat privateChat, QBChatException error, QBChatMessage originMessage) {
            Log.e(TAG, error.getMessage());
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (currentSession != null) {
            currentSession.hangUp(null);
        }
    }

    @Override
    public void sendCommand(Direction direction) {
        sendChatMessage(direction.toString());
    }
}