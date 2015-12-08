package theokanning.rover.ui.fragment.driver;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import theokanning.rover.R;
import theokanning.rover.ui.fragment.BaseFragment;

/**
 * Fragments that displays video stream and receives user steering input
 */
public class DriveFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drive, container, false);
    }


    @Override
    public int getTitleResourceId() {
        return 0; //Activity has no toolbar
    }
}
