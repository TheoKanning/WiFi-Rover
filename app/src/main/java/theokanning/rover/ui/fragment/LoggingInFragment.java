package theokanning.rover.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import theokanning.rover.R;

/**
 * Shows a message to let the user know that the app is logging in
 */
public class LoggingInFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logging_in, container, false);
    }

    @Override
    public int getTitleResourceId() {
        return 0;
    }
}
