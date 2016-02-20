package theokanning.rover.chat.quickblox;

import android.content.Context;

import rx.Observable;
import theokanning.rover.chat.RobotChatClient;
import theokanning.rover.user.User;

/**
 * {@link RobotChatClient} implementation using the Quickblox api
 *
 * @author Theo Kanning
 */
public class QuickBloxRobotChatClient extends QuickBloxChatClient implements RobotChatClient{
    private Context context;

    public QuickBloxRobotChatClient(Context context) {
        this.context = context;
    }

    @Override
    public Observable<Boolean> login() {
        User user = User.ROBOT;
        return loginAsUser(user, context);
    }
}
