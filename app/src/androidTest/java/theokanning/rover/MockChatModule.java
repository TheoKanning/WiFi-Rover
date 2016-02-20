package theokanning.rover;

import android.content.Context;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.chat.quickblox.QuickBloxDriverChatClient;
import theokanning.rover.chat.quickblox.QuickBloxRobotChatClient;

/**
 * Module for injecting mock objects into tests
 *
 * @author Theo Kanning
 */
@Module
public class MockChatModule {

    private Context context;

    private boolean mockMode;

    public MockChatModule(Context context, boolean mockMode) {
        this.context = context;
        this.mockMode = mockMode;
    }

    @Singleton
    @Provides
    DriverChatClient provideDriverChatClient() {
        if (mockMode) {
            return Mockito.mock(DriverChatClient.class);
        } else {
            return new QuickBloxDriverChatClient();
        }
    }

    @Singleton
    @Provides
    RobotChatClient provideRobotChatClient() {
        if (mockMode) {
            return Mockito.mock(RobotChatClient.class);
        } else {
            return new QuickBloxRobotChatClient();
        }
    }
}
