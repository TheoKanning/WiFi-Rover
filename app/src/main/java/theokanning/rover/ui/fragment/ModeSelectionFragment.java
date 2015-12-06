package theokanning.rover.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import theokanning.rover.R;
import theokanning.rover.ui.activity.DriverActivity;

/**
 * Fragment where user selects remote or user mode
 */
public class ModeSelectionFragment extends BaseFragment {

    @OnClick(R.id.driver)
    public void goToDriverActivity(){
        Intent intent = new Intent(getContext(), DriverActivity.class);
        startActivity(intent);
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
    }

    @Override
    public int getTitleResourceId() {
        return R.string.mode_selection_title;
    }
}
