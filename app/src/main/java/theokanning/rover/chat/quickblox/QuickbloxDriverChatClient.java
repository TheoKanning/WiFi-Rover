package theokanning.rover.chat.quickblox;

import android.content.Context;

import rx.Observable;
import rx.Subscriber;
import theokanning.rover.chat.DriverChatClient;
import theokanning.rover.user.User;

/**
 * {@link DriverChatClient} implementation using the quickblox api
 *
 * @author Theo Kanning
 */
public class QuickbloxDriverChatClient implements DriverChatClient {

    private static final String TAG = "QbDriverChatClient";

    private Context context;

    public QuickbloxDriverChatClient(Context context) {
        this.context = context;
    }

    @Override
    public Observable<Boolean> login() {
        final User user = User.DRIVER;

        Observable<Boolean> observable = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                QuickBloxLoginTask task = new QuickBloxLoginTask(user, context,
                        new LoginTaskSubscriberAdapter(subscriber));
                task.execute();
            }
        });

        return observable;
    }

    /**
     * Adapts {@link QuickBloxLoginTask.LoginTaskCallback} events to a subscriber
     */
    private static class LoginTaskSubscriberAdapter implements QuickBloxLoginTask.LoginTaskCallback{
        private Subscriber<? super Boolean> subscriber;

        public LoginTaskSubscriberAdapter(Subscriber<? super Boolean> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onSuccess() {
            subscriber.onNext(true);
            subscriber.onCompleted();
        }

        @Override
        public void onFailure() {
            subscriber.onNext(false);
            subscriber.onCompleted();
        }
    }


}
