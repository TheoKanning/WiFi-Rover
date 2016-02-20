package theokanning.rover;

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
    @Singleton
    @Provides
    DriverChatClient provideDriverChatClient(){
        return new QuickbloxDriverChatClient(); //todo replace with mock
    }
}
