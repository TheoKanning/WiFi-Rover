package theokanning.rover.chat.quickblox;

import android.content.Context;

import rx.Observable;
import theokanning.rover.chat.DriverChatClient;
import theokanning.rover.user.User;

/**
 * {@link DriverChatClient} implementation using the quickblox api
 *
 * @author Theo Kanning
 */
public class QuickBloxDriverChatClient extends QuickBloxChatClient implements DriverChatClient {

    private static final String TAG = "QbDriverChatClient";

    private Context context;

    public QuickBloxDriverChatClient(Context context) {
        this.context = context;
    }

    @Override
    public Observable<Boolean> login() {
        User user = User.DRIVER;
        return loginAsUser(user, context);
    }
}
