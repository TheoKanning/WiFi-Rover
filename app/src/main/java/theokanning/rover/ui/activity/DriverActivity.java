package theokanning.rover.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.videochat.webrtc.QBRTCSession;

import javax.inject.Inject;

import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.callback.DriverChatCallbackListener;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.ui.fragment.WaitingFragment;
import theokanning.rover.ui.fragment.driver.ConnectFragment;
import theokanning.rover.ui.fragment.driver.ControlFragment;

/**
 * Activity where user controls remote device and watches video stream. Starts by showing
 * instructions to connect to remote device, then connects and shows video stream.
 */
public class DriverActivity extends BaseActivity implements SteeringListener, DriverChatCallbackListener {
    private static final String TAG = "DriverActivity";

    @Inject
    DriverChatClient driverChatClient;

    private QBRTCSession currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RoverApplication) getApplication()).getComponent().inject(this);
        setContentView(R.layout.activity_driver);
        driverChatClient.registerChatCallbackListener(this);
        driverChatClient.registerDriverChatCallbackListener(this);
        loginToChatService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        driverChatClient.unregisterDriverChatCallbackListener();
        driverChatClient.unregisterChatCallbackListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        driverChatClient.endCall();
    }

    private void loginToChatService() {
        showLoggingInFragment();
        driverChatClient.loginAsDriver(this).subscribe((success) -> {
            if (success) {
                showConnectFragment();
            } else {
                Intent intent = new Intent(DriverActivity.this, ModeSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void sendChatMessage(String message) {
        driverChatClient.sendMessageToRobot(message);
    }

    private void showLoggingInFragment() {
        WaitingFragment fragment = WaitingFragment.newInstance("Logging in to chat service...");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showConnectFragment() {
        setFragment(new ConnectFragment(), true);
    }

    private void showWaitingFragment() {
        WaitingFragment fragment = WaitingFragment.newInstance("Attempting to connect to robot...");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showControlFragment() {
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
        driverChatClient.startCall();
    }

    @Override
    public void sendCommand(Direction direction) {
        sendChatMessage(direction.toString());
    }

    @Override
    public void onCallAnswered() {
        Toast.makeText(this, "Starting video chat", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Connected successfully");
        showControlFragment();
    }

    @Override
    public void onCallNotAnswered() {
        Toast.makeText(this, "Robot did not answer", Toast.LENGTH_SHORT).show();
        showConnectFragment();
    }

    @Override
    public void onSessionEnded() {
        showConnectFragment();
    }

    @Override
    public void onChatMessageReceived(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}