package theokanning.rover.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;

import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.chat.listener.RobotChatListener;
import theokanning.rover.chat.model.Message;
import theokanning.rover.robot.RobotConnection;
import theokanning.rover.robot.RobotConnectionListener;
import theokanning.rover.ui.fragment.WaitingFragment;
import theokanning.rover.ui.fragment.robot.ChatMessageDebugListener;
import theokanning.rover.ui.fragment.robot.ConnectedFragment;

/**
 * Controls all call activity for the robot. Only receives calls.
 */
public class RobotActivity extends BaseActivity implements RobotConnectionListener, RobotChatListener {

    private static final String TAG = "RobotActivity";

    private static final int MESSAGE_QUEUE_PERIOD = 80;

    @Inject
    RobotChatClient robotChatClient;

    @Inject
    RobotConnection robotConnection;

    private final Object lock = new Object();

    private ChatMessageDebugListener chatMessageDebugListener;

    private Handler messageQueueHandler = new Handler();

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
        robotConnection.disconnect();
        robotChatClient.unregisterRobotChatCallbackListener();
        robotChatClient.unregisterChatCallbackListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        robotChatClient.endCall();
    }

    private void loginToChatService() {
        showWaitingFragment("Logging in to chat service...");
        robotChatClient.login(this).subscribe((success) -> {
            if (success) {
                showWaitingFragment("Waiting for connection...");
            } else {
                Intent intent = new Intent(RobotActivity.this, ModeSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void sendDirectionsToRobot(String command) {
        robotConnection.sendMessage(command);
    }

    private void showWaitingFragment(String message) {
        WaitingFragment fragment = WaitingFragment.newInstance(message);
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
        messageQueueHandler.post(() -> {
            robotChatClient.sendMessage(message);
            synchronized (lock) {
                try {
                    lock.wait(MESSAGE_QUEUE_PERIOD);
                } catch (InterruptedException e) {
                    //do nothing
                }
            }
        });
    }

    public void registerChatMessageDebugListener(ChatMessageDebugListener listener) {
        chatMessageDebugListener = listener;
    }

    public void unregisterChatMessageDebugListener() {
        chatMessageDebugListener = null;
    }

    private void sendMessageToDebugListener(Message message) {
        if (chatMessageDebugListener != null) {
            chatMessageDebugListener.showMessage(message);
        }
    }

    @Override
    public void onConnect() {
        sendChatMessageToDriver(new Message(Message.Tag.DISPLAY, "Connected to robot"));
        runOnUiThread(this::showConnectedFragment);
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
        robotConnection.connect(this);
        showWaitingFragment("Connecting to robot...");
    }

    @Override
    public void onSessionEnded() {
        showWaitingFragment("Waiting for connection...");
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
