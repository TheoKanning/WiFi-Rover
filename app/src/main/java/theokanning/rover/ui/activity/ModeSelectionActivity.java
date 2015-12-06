package theokanning.rover.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import theokanning.rover.R;
import theokanning.rover.ui.fragment.ModeSelectionFragment;

public class ModeSelectionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setFragment(new ModeSelectionFragment(), false);
    }


}
