package theokanning.rover.ui.activity;

import android.os.Bundle;

import theokanning.rover.R;
import theokanning.rover.ui.fragment.driver.ConnectFragment;

/**
 * Activity where user controls remote device and watches video stream. Starts by showing
 * instructions to connect to remote device, then connects and shows video stream.
 */
public class DriverActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        setFragment(new ConnectFragment(), true);
    }

    /**
     * Starts a connection to the robot user and starts driving if successful. Called by
     * ConnectFragment.
     */
    public void connect(){

    }
}
