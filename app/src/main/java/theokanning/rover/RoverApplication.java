package theokanning.rover;


import android.app.Application;

import com.quickblox.core.QBSettings;

import theokanning.rover.dagger.ChatModule;
import theokanning.rover.dagger.DaggerRoverComponent;
import theokanning.rover.dagger.RobotConnectionModule;
import theokanning.rover.dagger.RoverComponent;

public class RoverApplication extends Application {

    private RoverComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerRoverComponent.builder()
                .robotConnectionModule(new RobotConnectionModule(this))
                .chatModule(new ChatModule(this))
                .build();
        component.inject(this);

        initializeQuickBlox();
    }

    public RoverComponent getComponent() {
        return component;
    }

    private void initializeQuickBlox(){
        QBSettings.getInstance().fastConfigInit(getString(R.string.quickblox_app_id),
                getString(R.string.quickblox_auth_key),
                getString(R.string.quickblox_auth_secret));
    }
}
