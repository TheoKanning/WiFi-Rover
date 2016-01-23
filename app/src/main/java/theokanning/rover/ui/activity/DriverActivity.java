package theokanning.rover.ui.activity;

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

import theokanning.rover.R;
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

    private QBRTCSession currentSession;
    private QBPrivateChat privateChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        initQbrtcClient();
        showConnectFragment();
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
            Integer opponentId = User.ROBOT.getId();
            privateChat = QBChatService.getInstance()
                    .getPrivateChatManager()
                    .createChat(opponentId, privateChatMessageListener);
        }

        try {
            QBChatMessage chatMessage = new QBChatMessage();
            chatMessage.setBody(message);
            privateChat.sendMessage(chatMessage);
        } catch (XMPPException e) {

        } catch (SmackException.NotConnectedException e) {

        }
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
        WaitingFragment fragment = new WaitingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WaitingFragment.WAITING_TEXT_EXTRA, "Attempting to connect to robot...");
        fragment.setArguments(bundle);
        setFragment(fragment, true);
    }

    /**
     * Starts a connection to the robot user. Called by ConnectFragment.
     */
    public void connect() {
        showWaitingFragment();

        List<Integer> ids = new ArrayList<>();
        ids.add(User.ROBOT.getId());

        //Init session
        currentSession = QBRTCClient.getInstance(this).createNewSessionWithOpponents(ids,
                QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);


        Map<String, String> userInfo = new HashMap<>();
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
        Toast.makeText(this, "Connected to robot", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Connected successfully");
        setFragment(new ControlFragment(), true);
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {
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
        sendChatMessage(direction.asText());
    }
}