package theokanning.rover.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;

import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.listener.RobotChatListener;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.chat.model.Message;
import theokanning.rover.robot.RobotConnectionListener;
import theokanning.rover.ui.fragment.WaitingFragment;
import theokanning.rover.ui.fragment.robot.ChatMessageDebugListener;
import theokanning.rover.ui.fragment.robot.ConnectedFragment;
import theokanning.rover.usb.UsbScanner;

/**
 * Controls all call activity for the robot. Only receives calls.
 */
public class RobotActivity extends BaseActivity implements RobotConnectionListener, RobotChatListener {

    private static final String TAG = "RobotActivity";

    private static final int MINIMUM_COMMAND_PERIOD_MS = 30;

    @Inject
    UsbScanner usbScanner;

    @Inject
    RobotChatClient robotChatClient;

    private long lastMessageTime;

    private ChatMessageDebugListener chatMessageDebugListener;

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
                showWaitingFragment();
            } else {
                Intent intent = new Intent(RobotActivity.this, ModeSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void sendDirectionsToRobot(String command) {
        if (!connectedToRobot()) {
            Log.d(TAG, "Can't send command, not connected to robot");
        }

        long time = System.currentTimeMillis();
        if (time - lastMessageTime > MINIMUM_COMMAND_PERIOD_MS) {
            lastMessageTime = time;
            usbScanner.write(command);
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

    private void sendChatMessageToDriver(Message message) {
        robotChatClient.sendMessage(message);
    }

    private boolean connectedToRobot() {
        return usbScanner.isConnected();
    }

    public void registerChatMessageDebugListener(ChatMessageDebugListener listener){
        chatMessageDebugListener = listener;
    }

    public void unregisterChatMessageDebugListener(){
        chatMessageDebugListener = null;
    }

    private void sendMessageToDebugListener(Message message){
        if(chatMessageDebugListener != null){
            chatMessageDebugListener.showMessage(message);
        }
    }

    @Override
    public void onConnect() {
        sendChatMessageToDriver(new Message(Message.Tag.DISPLAY, "Connected to robot"));
        runOnUiThread(() -> showConnectedFragment());
    }

    @Override
    public void onDisconnect() {
        sendChatMessageToDriver(new Message(Message.Tag.DISPLAY, "Disconnected from robot"));
    }

    @Override
    public void onMessageReceived(String message) {
        Log.d(TAG, "Received message: " + message);
    }

    @Override
    public void onCallReceived() {
        Toast.makeText(RobotActivity.this, "Call received", Toast.LENGTH_SHORT).show();
        usbScanner.connect(this);
        showScanningFragment();
    }

    @Override
    public void onSessionEnded() {
        showWaitingFragment();
    }

    @Override
    public void onChatMessageReceived(Message message) {
        Log.e(TAG, "Message received: " + message);
        sendMessageToDebugListener(message);
        switch (message.getTag()) {
            case ROBOT:
                sendDirectionsToRobot(message.getContents());
                break;
            case DISPLAY:
                break;
            case TEST:
                Log.e(TAG, message.getContents());
            default:
        }
    }
}
