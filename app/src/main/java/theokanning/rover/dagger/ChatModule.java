package theokanning.rover.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.chat.quickblox.QuickBloxDriverChatClient;
import theokanning.rover.chat.quickblox.QuickBloxRobotChatClient;

@Module
public class ChatModule {

    private Context context;

    public ChatModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    DriverChatClient provideDriverChatClient(){
        return new QuickBloxDriverChatClient();
    }

    @Singleton
    @Provides
    RobotChatClient provideRobotChatClient(){
        return new QuickBloxRobotChatClient();
    }
}
