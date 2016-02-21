package theokanning.rover.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;

import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.callback.RobotChatCallbackListener;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.ui.fragment.WaitingFragment;
import theokanning.rover.ui.fragment.robot.ConnectedFragment;
import theokanning.rover.usb.UsbScanner;

/**
 * Controls all call activity for the robot. Only receives calls.
 */
public class RobotActivity extends BaseActivity implements UsbScanner.UsbScannerListener, RobotChatCallbackListener {

    private static final String TAG = "RobotActivity";
    private static final int ROBOT_COMMAND_MAX = 200;

    @Inject
    UsbScanner usbScanner;

    @Inject
    RobotChatClient robotChatClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);
        ((RoverApplication) getApplication()).getComponent().inject(this);
        robotChatClient.registerRobotChatCallbackListener(this);
        robotChatClient.registerChatCallbackListener(this);
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
        robotChatClient.unregisterRobotChatCallbackListener();
        robotChatClient.unregisterChatCallbackListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        robotChatClient.endCall();
    }

    private void loginToChatService() {
        showLoggingInFragment();
        robotChatClient.login(this).subscribe((success) -> {
            if (success) {
                usbScanner.registerListener(RobotActivity.this);
                showWaitingFragment();
            } else {
                Intent intent = new Intent(RobotActivity.this, ModeSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
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

    private void sendChatMessageToDriver(String message) {
        robotChatClient.sendMessage(message);
    }

    private boolean connectedToRobot() {
        return usbScanner.isConnected();
    }

    @Override
    public void onConnect() {
        sendChatMessageToDriver("Connected to robot");
        runOnUiThread(() -> showConnectedFragment());
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
    public void onCallReceived() {
        Toast.makeText(RobotActivity.this, "Call received", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Call received");
        usbScanner.startScan();
        showScanningFragment();
    }

    @Override
    public void onSessionEnded() {
        showWaitingFragment();
    }

    @Override
    public void onChatMessageReceived(String message) {
        Log.e(TAG, "Message received: " + message);
        sendDirectionsToRobot(SteeringListener.Direction.valueOf(message));
    }
}
