package theokanning.rover.chat.quickblox;

import android.content.Context;

import rx.Observable;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.user.User;

/**
 * {@link RobotChatClient} implementation using the Quickblox api
 *
 * @author Theo Kanning
 */
public class QuickBloxRobotChatClient extends QuickBloxChatClient implements RobotChatClient {

    public QuickBloxRobotChatClient(Context context) {
        super(context);
    }

    @Override
    public Observable<Boolean> login() {
        User user = User.ROBOT;
        return loginAsUser(user, context);
    }
}
