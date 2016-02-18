package theokanning.rover.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.List;

import theokanning.rover.R;
import theokanning.rover.ui.fragment.ModeSelectionFragment;
import theokanning.rover.ui.fragment.WaitingFragment;
import theokanning.rover.user.User;

public class ModeSelectionActivity extends BaseActivity implements ModeSelectionInterface{

    private static final String TAG = "ModeSelectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showModeSelection();
    }

    /**
     * Shows mode selection fragment without adding to back stack so that user can't return to WaitingFragment
     */
    private void showModeSelection(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new ModeSelectionFragment());
        ft.commit();
    }

    /**
     * Shows a waiting screen when logging in
     */
    private void showLoggingIn(){
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
        final QBUser qbUser = user.getQbUser();

        showLoggingIn();

        //todo rearrange nested listeners
        QBAuth.createSession(qbUser, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                Log.d(TAG, user + " session created successfully");

                if (!QBChatService.isInitialized()) {
                    QBChatService.init(ModeSelectionActivity.this);
                }
                QBChatService chatService = QBChatService.getInstance();

                if (chatService.isLoggedIn()) {
                    try {
                        Log.d(TAG, "Logging out");
                        chatService.logout();
                    } catch (SmackException.NotConnectedException e) {
                        Log.d(TAG, "Failed to log out");
                        return;
                    }
                }

                chatService.login(qbUser, new QBEntityCallbackImpl<QBUser>() {

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, user + " successfully logged in to chat, starting " + user + " activity");

                        Intent intent;
                        switch (user) {
                            case DRIVER:
                                intent = new Intent(ModeSelectionActivity.this, DriverActivity.class);
                                break;
                            case ROBOT:
                            default:
                                intent = new Intent(ModeSelectionActivity.this, RobotActivity.class);
                                break;
                        }

                        startActivity(intent);
                    }

                    @Override
                    public void onError(List errors) {

                        showModeSelection();

                        Toast.makeText(ModeSelectionActivity.this, "Error when logging in " + user, Toast.LENGTH_SHORT).show();
                        for (Object error : errors) {
                            Log.d(TAG, error.toString());
                        }
                    }
                });
            }

            @Override
            public void onError(List<String> errors) {

                showModeSelection();

                Toast.makeText(ModeSelectionActivity.this, "Could not start " + user + " session", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Adds user to QuickBlox server, only needs to be called once per user
     * Shows a toast with request result and prints all error messages if any are received
     * @param newUser new user to be added to server
     * @param context context in which to show toast message
     */
    public static void addUserToQuickBlox(final QBUser newUser, final Context context){
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
