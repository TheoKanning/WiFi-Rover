package theokanning.rover.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import theokanning.rover.ui.activity.RobotActivity;

@Singleton
@Component(modules = UsbModule.class)
public interface RoverComponent {
    void inject(Application application);
    void inject(RobotActivity robotActivity);
}

