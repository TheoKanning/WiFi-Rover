package theokanning.rover.chat.quickblox;

import android.content.Context;

import rx.Observable;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.user.User;

/**
 * {@link DriverChatClient} implementation using the quickblox api
 *
 * @author Theo Kanning
 */
public class QuickBloxDriverChatClient extends QuickBloxChatClient implements DriverChatClient {

    private static final String TAG = "QbDriverChatClient";

    @Override
    public Observable<Boolean> login(Context context) {
        setContext(context);
        User user = User.DRIVER;
        return loginAsUser(user, context);
    }
}
