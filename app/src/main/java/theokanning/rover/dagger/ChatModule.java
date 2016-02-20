package theokanning.rover.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import theokanning.rover.chat.DriverChatClient;
import theokanning.rover.chat.RobotChatClient;
import theokanning.rover.chat.quickblox.QuickbloxDriverChatClient;
import theokanning.rover.chat.quickblox.QuickbloxRobotChatClient;

@Module
public class ChatModule {
    @Singleton
    @Provides
    DriverChatClient provideDriverChatClient(){
        return new QuickbloxDriverChatClient();
    }

    @Singleton
    @Provides
    RobotChatClient provideRobotChatClient(){
        return new QuickbloxRobotChatClient();
    }
}
