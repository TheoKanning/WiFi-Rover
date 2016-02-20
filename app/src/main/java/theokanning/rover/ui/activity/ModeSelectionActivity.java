package theokanning.rover.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.List;

import javax.inject.Inject;

import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.DriverChatClient;
import theokanning.rover.ui.fragment.ModeSelectionFragment;
import theokanning.rover.ui.fragment.WaitingFragment;
import theokanning.rover.user.User;

public class ModeSelectionActivity extends BaseActivity implements ModeSelectionInterface {

    @Inject
    DriverChatClient driverChatClient;

    private static final String TAG = "ModeSelectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);
        ((RoverApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showModeSelection();
    }

    private void showModeSelection() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new ModeSelectionFragment());
        ft.commit();
    }

    private void showLoggingIn() {
        WaitingFragment fragment = WaitingFragment.newInstance("Logging in to chat service...");
        setFragment(fragment, true);
    }

    /**
     * Navigates to the appropriate activity based on the user enum received. First creates a QB
     * session, then logs in as the appropriate user. Shows WaitingFragment while attempting to
     * make a connection
     *
     * @param user the user to be logged in, determines which activity is started
     */
    @Override
    public void logInAndStartActivity(final User user) {
        Intent intent;

        switch (user) {
            case DRIVER:
                intent = new Intent(this, DriverActivity.class);
                break;
            case ROBOT:
            default:
                intent = new Intent(this, RobotActivity.class);
                break;
        }

        startActivity(intent);
    }

    private void logOutOfChatService(QBChatService chatService) {
        if (chatService.isLoggedIn()) {
            try {
                Log.d(TAG, "Logging out");
                chatService.logout();
            } catch (SmackException.NotConnectedException e) {
                Log.d(TAG, "Failed to log out");
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds user to QuickBlox server, only needs to be called once per user
     * Shows a toast with request result and prints all error messages if any are received
     *
     * @param newUser new user to be added to server
     * @param context context in which to show toast message
     */
    public static void addUserToQuickBlox(final QBUser newUser, final Context context) {
        QBUsers.signUp(newUser, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                Log.d(TAG, newUser.getLogin() + " signed up successfully");
                Toast.makeText(context, newUser.getLogin() + " signed up successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(List<String> errors) {
                Toast.makeText(context, newUser.getLogin() + " not signed up", Toast.LENGTH_SHORT).show();
                for (String error : errors) {
                    Log.e(TAG, error);
                }
            }
        });
    }
}
