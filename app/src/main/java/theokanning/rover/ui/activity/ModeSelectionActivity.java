package theokanning.rover.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import javax.inject.Inject;

import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.ui.fragment.ModeSelectionFragment;

public class ModeSelectionActivity extends BaseActivity implements ModeSelectionInterface {

    @Inject
    DriverChatClient driverChatClient;

    private static final String TAG = "ModeSelectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);
        ((RoverApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showModeSelection();
    }

    private void showModeSelection() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new ModeSelectionFragment());
        ft.commit();
    }

    @Override
    public void startDriverActivity() {
        startActivity(new Intent(this, DriverActivity.class));
    }

    @Override
    public void startRobotActivity() {
        startActivity(new Intent(this, RobotActivity.class));
    }
}
