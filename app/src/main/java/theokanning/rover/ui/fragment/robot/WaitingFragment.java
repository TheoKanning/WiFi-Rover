package theokanning.rover.ui.fragment.robot;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import theokanning.rover.R;
import theokanning.rover.ui.fragment.BaseFragment;

/**
 * Fragment shown while waiting for call, does nothing
 */
public class WaitingFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_waiting, container, false);
    }


    @Override
    public int getTitleResourceId() {
        return 0;
    }
}
