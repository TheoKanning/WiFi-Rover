package theokanning.rover.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;

import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.chat.listener.DriverChatListener;
import theokanning.rover.chat.model.Message;
import theokanning.rover.ui.fragment.WaitingFragment;
import theokanning.rover.ui.fragment.driver.ConnectFragment;
import theokanning.rover.ui.fragment.driver.ControlFragment;

/**
 * Activity where user controls remote device and watches video stream. Starts by showing
 * instructions to connect to remote device, then connects and shows video stream.
 */
public class DriverActivity extends BaseActivity implements DriverChatListener {
    private static final String TAG = "DriverActivity";

    //todo understand fragment back stack

    @Inject
    DriverChatClient driverChatClient;

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
        driverChatClient.endCall();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack(ConnectFragment.class.getSimpleName(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void loginToChatService() {
        showLoggingInFragment();
        driverChatClient.login(this).subscribe((success) -> {
            if (success) {
                showConnectFragment();
            } else {
                Intent intent = new Intent(DriverActivity.this, ModeSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void showLoggingInFragment() {
        WaitingFragment fragment = WaitingFragment.newInstance("Logging in to chat service...");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showConnectFragment() {
        ConnectFragment fragment = ConnectFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showWaitingFragment() {
        WaitingFragment fragment = WaitingFragment.newInstance("Attempting to connect to robot...");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(ConnectFragment.class.getSimpleName())
                .commit();
    }

    private void showControlFragment() {
        ControlFragment fragment = ControlFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
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
    public void onChatMessageReceived(Message message) {
        Toast.makeText(this, message.getContents(), Toast.LENGTH_SHORT).show();
    }
}