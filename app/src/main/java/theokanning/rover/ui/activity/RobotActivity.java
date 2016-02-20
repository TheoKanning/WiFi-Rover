package theokanning.rover.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.ui.fragment.WaitingFragment;
import theokanning.rover.ui.fragment.robot.ConnectedFragment;
import theokanning.rover.usb.UsbScanner;

/**
 * Controls all call activity for the robot. Only receives calls.
 */
public class RobotActivity extends BaseActivity implements QBRTCClientSessionCallbacks, UsbScanner.UsbScannerListener {

    private static final String TAG = "RobotActivity";
    private static final int ROBOT_COMMAND_MAX = 200;

    @Inject
    UsbScanner usbScanner;

    @Inject
    RobotChatClient robotChatClient;

    private QBPrivateChat privateChat;
    private QBRTCSession currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);
        ((RoverApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginToChatService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usbScanner.close();
        usbScanner.unregisterListener();
        QBRTCClient.getInstance(this).removeSessionsCallbacksListener(this);
    }

    private void loginToChatService() {
        showLoggingInFragment();
        robotChatClient.login(this).subscribe((success) -> {
            if (success) {
                initVideoChatClient();
                initPrivateChatClient();
                usbScanner.registerListener(RobotActivity.this);
                showWaitingFragment();
            } else {
                Intent intent = new Intent(RobotActivity.this, ModeSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void initVideoChatClient() {
        QBRTCClient.getInstance(this).addSessionCallbacksListener(this);
    }

    private void initPrivateChatClient() {
        QBPrivateChatManagerListener privateChatManagerListener = new QBPrivateChatManagerListener() {
            @Override
            public void chatCreated(final QBPrivateChat incomingPrivateChat, final boolean createdLocally) {
                if (!createdLocally) {
                    privateChat = incomingPrivateChat;
                    privateChat.addMessageListener(privateChatMessageListener);
                }
            }
        };
        QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();
        privateChatManager.addPrivateChatManagerListener(privateChatManagerListener);
    }

    /**
     * Sends a chat message
     */
    private void sendChatMessageToDriver(String message) {
        if (privateChat != null) {
            try {
                privateChat.sendMessage(message);
            } catch (XMPPException | SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a directional command to the robot after converting it to RL values
     *
     * @param direction direction robot should move
     */
    private void sendDirectionsToRobot(SteeringListener.Direction direction) {
        //todo change to sending percent value -100 to 100
        if (connectedToRobot()) {
            int left = 0;
            int right = 0;
            switch (direction) {
                case UP:
                    left = ROBOT_COMMAND_MAX;
                    right = ROBOT_COMMAND_MAX;
                    break;
                case DOWN:
                    left = -1 * ROBOT_COMMAND_MAX;
                    right = -1 * ROBOT_COMMAND_MAX;
                    break;
                case LEFT:
                    right = ROBOT_COMMAND_MAX / 2;
                    left = -1 * ROBOT_COMMAND_MAX / 2;
                    break;
                case RIGHT:
                    right = -1 * ROBOT_COMMAND_MAX / 2;
                    left = ROBOT_COMMAND_MAX / 2;
                    break;
            }
            //todo refactor start and end characters into usb message class
            String bluetoothCommand = "(" + left + "," + right + ")";
            usbScanner.write("(R" + right + ")(L" + left + ")");
        } else {
            Log.d(TAG, "Can't send command, not connected to robot");
        }
    }

    /**
     * Shows waiting screen when logging in to chat service
     */
    private void showLoggingInFragment() {
        WaitingFragment fragment = WaitingFragment.newInstance("Logging in to chat service...");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showWaitingFragment() {
        WaitingFragment fragment = WaitingFragment.newInstance("Waiting for connection...");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showScanningFragment() {
        WaitingFragment fragment = WaitingFragment.newInstance("Connecting to robot...");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showConnectedFragment() {
        ConnectedFragment fragment = ConnectedFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onReceiveNewSession(final QBRTCSession qbrtcSession) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RobotActivity.this, "Call received", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Call received");

                Map<String, String> userInfo = new HashMap<>(); //todo this might be unnecessary
                userInfo.put("Key", "Robot");

                // Accept incoming call
                qbrtcSession.acceptCall(qbrtcSession.getUserInfo());
                currentSession = qbrtcSession;
                usbScanner.startScan();
                showScanningFragment();
            }
        });
    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        //Should not happen
    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        //Should not happen
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        //Should not happen
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer) {
        currentSession = null;
        showWaitingFragment();
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

    QBMessageListener<QBPrivateChat> privateChatMessageListener = new QBMessageListener<QBPrivateChat>() {
        @Override
        public void processMessage(QBPrivateChat privateChat, final QBChatMessage chatMessage) {
            Log.e(TAG, "Message received: " + chatMessage.getBody());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendDirectionsToRobot(SteeringListener.Direction.valueOf(chatMessage.getBody()));
                }
            });
        }

        @Override
        public void processError(QBPrivateChat privateChat, QBChatException error, QBChatMessage originMessage) {
            Log.e(TAG, error.getMessage());
        }
    };

    private boolean connectedToRobot() {
        return usbScanner.isConnected();
    }

    @Override
    public void onConnect() {
        sendChatMessageToDriver("Connected to robot");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showConnectedFragment();
            }
        });
    }

    @Override
    public void onDisconnect() {
        sendChatMessageToDriver("Disconnected from robot");
    }

    @Override
    public void onMessageReceived(String message) {
        Log.d(TAG, "Received message: " + message);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentSession != null) {
            currentSession.hangUp(null);
        }
    }
}
