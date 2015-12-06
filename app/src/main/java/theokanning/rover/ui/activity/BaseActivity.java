package theokanning.rover.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import theokanning.rover.R;
import theokanning.rover.ui.fragment.BaseFragment;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void setFragment(BaseFragment fragment, boolean animate) {
        // First, get the current fragment. If there isn't a current fragment, then add the new
        // fragment.  If there is, then potentially replace the current fragment with the new one.
        // However, if the user tries to navigate to the exact same fragment, that will look silly,
        // so only do so if the new fragment has a different canonical name than the current one.
        if (fragment != null) {
            if (getCurrentFragment() == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            } else if (!getCurrentFragmentName().equals(fragment.getClass().getCanonicalName())) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.addToBackStack(fragment.getClass().getSimpleName());
                if (animate) {
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                }
                ft.commit();
            }
        }

        getSupportActionBar().setTitle(fragment.getTitleResourceId());
    }

    public BaseFragment getCurrentFragment() {
        return (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private String getCurrentFragmentName() {
        return getCurrentFragment().getClass().getCanonicalName();
    }

    public void showMainActivity(boolean animate) {
        Log.d(TAG, "showMainScreen()");

        // clear the backstack so we this becomes our top-most activity once the user is logged in
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
