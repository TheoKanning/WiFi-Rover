package theokanning.rover.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import theokanning.rover.chat.DriverChatClient;
import theokanning.rover.chat.RobotChatClient;
import theokanning.rover.chat.quickblox.QuickbloxDriverChatClient;
import theokanning.rover.chat.quickblox.QuickbloxRobotChatClient;

@Module
public class ChatModule {

    private Context context;

    public ChatModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    DriverChatClient provideDriverChatClient(){
        return new QuickbloxDriverChatClient(context);
    }

    @Singleton
    @Provides
    RobotChatClient provideRobotChatClient(){
        return new QuickbloxRobotChatClient();
    }
}
