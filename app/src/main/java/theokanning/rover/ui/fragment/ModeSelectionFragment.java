package theokanning.rover.ui.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import theokanning.rover.R;
import theokanning.rover.ui.activity.ModeSelectionInterface;
import theokanning.rover.user.User;

/**
 * Fragment where user selects remote or user mode
 */
public class ModeSelectionFragment extends BaseFragment {

    private static final String TAG = "ModeSelectionFragment";

    @OnClick(R.id.driver)
    public void goToDriverActivity() {
        modeSelectionInterface.logInAndStartActivity(User.DRIVER);
    }

    @OnClick(R.id.robot)
    public void goToRobotActivity() {
        modeSelectionInterface.logInAndStartActivity(User.ROBOT);
    }


    private ModeSelectionInterface modeSelectionInterface;

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
        initializeModeSelectionInterface();
    }

    private void initializeModeSelectionInterface() {
        try {
            modeSelectionInterface = (ModeSelectionInterface) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "Activity does not implement ModeSelectionInterface!");
            throw e;
        }
    }
}
