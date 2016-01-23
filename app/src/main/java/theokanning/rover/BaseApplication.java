package theokanning.rover;


import android.app.Application;

import com.quickblox.core.QBSettings;

import theokanning.rover.dagger.BaseComponent;
import theokanning.rover.dagger.BaseModule;
import theokanning.rover.dagger.DaggerBaseComponent;

public class BaseApplication extends Application {

    private BaseComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerBaseComponent.builder()
                .baseModule(new BaseModule(this))
                .build();
        component.inject(this);

        initializeQuickBlox();
    }

    public BaseComponent getComponent() {
        return component;
    }

    /**
     * Initializes quickblox components using stored api credentials
     */
    private void initializeQuickBlox(){
        QBSettings.getInstance().fastConfigInit(getString(R.string.quickblox_app_id),
                getString(R.string.quickblox_auth_key),
                getString(R.string.quickblox_auth_secret));
    }
}
