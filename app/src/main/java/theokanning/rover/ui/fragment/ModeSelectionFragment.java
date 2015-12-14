package theokanning.rover.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import theokanning.rover.R;
import theokanning.rover.ui.activity.DriverActivity;
import theokanning.rover.ui.activity.RobotActivity;
import theokanning.rover.user.User;

/**
 * Fragment where user selects remote or user mode
 */
public class ModeSelectionFragment extends BaseFragment {

    private static final String TAG = "ModeSelectionFragment";

    @OnClick(R.id.driver)
    public void goToDriverActivity() {
        logInAndEnterActivity(User.DRIVER);
    }

    @OnClick(R.id.robot)
    public void goToRobotActivity() {
        logInAndEnterActivity(User.ROBOT);
    }

    /**
     * Navigates to the appropriate activity based on the user enum received. First creates a QB
     * session, then logs in as the appropriate user
     *
     * @param user the user to be logged in, determines which activity is started
     */
    private void logInAndEnterActivity(final User user) {
        final QBUser qbUser = user.getQbUser();

        QBAuth.createSession(qbUser, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                Log.d(TAG, user + " session created successfully");

                if (!QBChatService.isInitialized()) {
                    QBChatService.init(getContext());
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
                                intent = new Intent(getContext(), DriverActivity.class);
                                break;
                            case ROBOT:
                            default:
                                intent = new Intent(getContext(), RobotActivity.class);
                                break;
                        }

                        startActivity(intent);
                    }

                    @Override
                    public void onError(List errors) {

                        Toast.makeText(getContext(), "Error when logging in " + user, Toast.LENGTH_SHORT).show();
                        for (Object error : errors) {
                            Log.d(TAG, error.toString());
                        }
                    }
                });
            }

            @Override
            public void onError(List<String> errors) {
                Toast.makeText(getContext(), "Could not start " + user + " session", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mode_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.mode_selection_title;
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
