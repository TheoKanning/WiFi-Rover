package theokanning.rover.ui.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.HashMap;
import java.util.Map;

import theokanning.rover.R;
import theokanning.rover.bluetooth.BluetoothConnection;
import theokanning.rover.bluetooth.BluetoothScanner;
import theokanning.rover.ui.fragment.WaitingFragment;

/**
 * Controls all call activity for the robot
 */
public class RobotActivity extends BaseActivity implements QBRTCClientSessionCallbacks, BluetoothConnection.BluetoothConnectionListener {

    private static final String TAG = "RobotActivity";
    private static final int ROBOT_COMMAND_MAX = 255;

    private QBPrivateChat privateChat;

    private BluetoothScanner bluetoothScanner;
    private BluetoothConnection bluetoothConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initQbrtcClient();
        initChatClient();
        showWaitingFragment();
    }

    /**
     * Tells the client that this activity is prepared to process calls and sets this activity
     * as a listener for client callback
     */
    private void initQbrtcClient() {
        QBRTCClient.getInstance(this).prepareToProcessCalls();
        QBRTCClient.getInstance(this).addSessionCallbacksListener(this);

        QBChatService.getInstance().getVideoChatWebRTCSignalingManager()
                .addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
                    @Override
                    public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                        if (!createdLocally) {
                            QBRTCClient.getInstance(RobotActivity.this).addSignaling((QBWebRTCSignaling) qbSignaling);
                        }
                    }
                });

        QBRTCConfig.setDebugEnabled(false);
    }

    private void initChatClient() {
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
     * Shows a waiting fragment to tell the user that the app is waiting for a connection
     */
    private void showWaitingFragment() {
        WaitingFragment fragment = new WaitingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WaitingFragment.WAITING_TEXT_EXTRA, "Waiting for connection...");
        fragment.setArguments(bundle);
        setFragment(fragment, true);
    }

    /**
     * Show a waiting fragment to tell the user that the app is scanning for the robot over bluetooth
     */
    private void showScanningFragment() {
        WaitingFragment fragment = new WaitingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WaitingFragment.WAITING_TEXT_EXTRA, "Scanning for robot...");
        fragment.setArguments(bundle);
        setFragment(fragment, true);
    }

    private void scanForRobot() {
        Log.d(TAG, "Starting scan for robot");
        showScanningFragment();
        bluetoothScanner = new BluetoothScanner(this);
        bluetoothScanner.startScan(new BluetoothScanner.OnBluetoothDeviceDiscoveredListener() {
            @Override
            public void OnBluetoothDeviceDiscovered(BluetoothDevice device) {
                Log.d(TAG, "Bluetooth device found: " + device.getName());
                if (device.getName().equals("Dexter")) {
                    bluetoothConnection = new BluetoothConnection(device, RobotActivity.this);
                    sendChatMessage("Found robot");
                }
            }
        });
    }

    /**
     * Sends a chat message
     */
    private void sendChatMessage(String message) {
        if (privateChat != null) {
            try {
                privateChat.sendMessage(message);
            } catch (XMPPException | SmackException.NotConnectedException e) {

            }
        }
    }

    /**
     * Sends a directional command to the robot after converting it to RL values
     *
     * @param direction direction robot should move
     */
    private void sendDirections(SteeringListener.Direction direction) {
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
            String bluetoothCommand = "L" + left + "\rR" + right + "\r";
            bluetoothConnection.write(bluetoothCommand);
        } else {
            Log.d(TAG, "Can't send command, not connected to robot");
        }
    }

    @Override
    public void onReceiveNewSession(final QBRTCSession qbrtcSession) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RobotActivity.this, "Call received", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Call received");

                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("Key", "Robot");

                // Accept incoming call
                qbrtcSession.acceptCall(qbrtcSession.getUserInfo());
                scanForRobot();
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
                    sendDirections(SteeringListener.Direction.valueOf(chatMessage.getBody()));
                }
            });
        }

        @Override
        public void processError(QBPrivateChat privateChat, QBChatException error, QBChatMessage originMessage) {
            Log.e(TAG, error.getMessage());
        }
    };

    private boolean connectedToRobot(){
        return bluetoothConnection != null && bluetoothConnection.isConnected();
    }

    @Override
    public void onMessageReceived(String message) {

    }

    @Override
    public void onConnect() {
        sendChatMessage("Connected to robot");
    }

    @Override
    public void onDisconnect() {
        sendChatMessage("Disconnected from robot");
    }
}
