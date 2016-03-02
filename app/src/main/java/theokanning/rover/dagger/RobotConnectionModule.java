package theokanning.rover.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import theokanning.rover.robot.RobotConnection;
import theokanning.rover.usb.RobotUsbConnection;

@Module
public class RobotConnectionModule {

    private Context context;

    public RobotConnectionModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    RobotConnection provideScanner(){
        return new RobotUsbConnection(context);
    }
}
