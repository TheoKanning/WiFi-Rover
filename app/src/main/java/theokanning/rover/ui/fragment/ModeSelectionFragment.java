package theokanning.rover.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import theokanning.rover.R;
import theokanning.rover.ui.activity.ModeSelectionInterface;

/**
 * Fragment where user selects remote or user mode
 */
public class ModeSelectionFragment extends BaseFragment {

    private static final String TAG = "ModeSelectionFragment";

    @OnClick(R.id.driver)
    public void goToDriverActivity() {
        modeSelectionInterface.startDriverActivity();
    }

    @OnClick(R.id.robot)
    public void goToRobotActivity() {
        modeSelectionInterface.startRobotActivity();
    }

    private ModeSelectionInterface modeSelectionInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mode_selection, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initializeModeSelectionInterface();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
