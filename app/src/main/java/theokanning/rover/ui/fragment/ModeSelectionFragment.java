package theokanning.rover.ui.fragment;


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
import com.quickblox.users.model.QBUser;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import theokanning.rover.R;
import theokanning.rover.ui.activity.DriverActivity;
import theokanning.rover.user.User;

/**
 * Fragment where user selects remote or user mode
 */
public class ModeSelectionFragment extends BaseFragment {

    private static final String TAG = "ModeSelectionFragment";

    /**
     * Starts driver session and logs in to chat service. If both are successful, starts
     * DriverActivity
     */
    @OnClick(R.id.driver)
    public void goToDriverActivity() {

        final QBUser driver = User.getDriver();

        QBAuth.createSession(driver, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                Log.d(TAG, "Driver session created successfully");

                driver.setId(result.getUserId());

                QBChatService.init(getContext());
                QBChatService chatService = QBChatService.getInstance();

                chatService.login(driver, new QBEntityCallbackImpl<QBUser>() {

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Driver successfully logged in to chat, starting DriverActivity");
                        Intent intent = new Intent(getContext(), DriverActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(List errors) {

                        Toast.makeText(getContext(), "Error when logging in", Toast.LENGTH_SHORT).show();
                        for (Object error : errors) {
                            Log.d(TAG, error.toString());
                        }
                    }
                });
            }

            @Override
            public void onError(List<String> errors) {
                Toast.makeText(getContext(), "Could not start session", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.robot)
    public void goToRobotActivity() {
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                Toast.makeText(getContext(), "QB Session created successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(List<String> errors) {
                for (String error : errors) {
                    Log.e(TAG, error);
                }
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
}
