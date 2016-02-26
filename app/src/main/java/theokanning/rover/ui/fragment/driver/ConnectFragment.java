package theokanning.rover.ui.fragment.driver;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import theokanning.rover.R;
import theokanning.rover.ui.activity.DriverActivity;
import theokanning.rover.ui.fragment.BaseFragment;

/**
 * Shows instructions on how to connect to robot
 */
public class ConnectFragment extends BaseFragment {

    @OnClick(R.id.connect)
    public void connect(){
        ((DriverActivity) getActivity()).connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
    }
}
