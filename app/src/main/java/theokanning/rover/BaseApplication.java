package theokanning.rover;


import android.app.Application;

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
    }

    public BaseComponent getComponent() {
        return component;
    }
}
