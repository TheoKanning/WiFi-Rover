package theokanning.rover.ui.fragment.robot;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import theokanning.rover.R;

/**
 * Fragment displayed on the robot while driving
 *
 * @author Theo Kanning
 */
public class ConnectedFragment extends Fragment {

    public ConnectedFragment() {
        // Required empty public constructor
    }


    public static ConnectedFragment newInstance() {
        ConnectedFragment fragment = new ConnectedFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connected, container, false);
    }

}
