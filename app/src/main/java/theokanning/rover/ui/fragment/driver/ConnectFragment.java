package theokanning.rover.ui.fragment.driver;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import theokanning.rover.R;
import theokanning.rover.ui.fragment.BaseFragment;

/**
 * Shows instructions on how to connect to robot
 */
public class ConnectFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }


    @Override
    public int getTitleResourceId() {
        return 0;
    }
}
