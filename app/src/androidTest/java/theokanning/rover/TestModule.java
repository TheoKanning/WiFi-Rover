package theokanning.rover;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import theokanning.rover.chat.DriverChatClient;
import theokanning.rover.chat.quickblox.QuickbloxDriverChatClient;

/**
 * Module for injecting mock objects
 *
 * @author Theo Kanning
 */
@Module
public class TestModule {

    private Context context;

    public TestModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    DriverChatClient provideDriverChatClient(){
        return new QuickbloxDriverChatClient(context); //todo replace with mock
    }
}
