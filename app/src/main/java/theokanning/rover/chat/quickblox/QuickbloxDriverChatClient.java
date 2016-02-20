package theokanning.rover.chat.quickblox;

import rx.Observable;
import theokanning.rover.chat.DriverChatClient;

/**
 * {@link DriverChatClient} implementation using the quickblox api
 *
 * @author Theo Kanning
 */
public class QuickbloxDriverChatClient implements DriverChatClient{
    @Override
    public Observable<Boolean> logIn() {
        return Observable.just(true);
    }
}
