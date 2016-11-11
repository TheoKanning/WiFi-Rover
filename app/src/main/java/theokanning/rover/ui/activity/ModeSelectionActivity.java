package theokanning.rover.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import theokanning.rover.R;
import theokanning.rover.ui.fragment.ModeSelectionFragment;

public class ModeSelectionActivity extends BaseActivity implements ModeSelectionInterface {

    private static final String TAG = "ModeSelectionActivity";

    private static final int PERMISSION_REQUEST_ID = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hasPermissions()) {
            showModeSelection();
        } else {
            requestPermissions();
        }
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

    private boolean hasPermissions(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(){
        String[] permissions = new String[]{
                Manifest.permission.CAMERA
        };
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_ID){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showModeSelection();
            }
        }
    }
}
