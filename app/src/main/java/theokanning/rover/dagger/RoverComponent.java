package theokanning.rover.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import theokanning.rover.ui.activity.DriverActivity;
import theokanning.rover.ui.activity.ModeSelectionActivity;
import theokanning.rover.ui.activity.RobotActivity;

@Singleton
@Component(modules = {
        UsbModule.class,
        ChatModule.class})
public interface RoverComponent {
    void inject(Application application);

    void inject(DriverActivity robotActivity);

    void inject(RobotActivity robotActivity);

    void inject(ModeSelectionActivity modeSelectionActivity); //todo remove after moving login process
}

