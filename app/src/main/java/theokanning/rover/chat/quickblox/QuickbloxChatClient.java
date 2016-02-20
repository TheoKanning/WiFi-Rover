package theokanning.rover.chat.quickblox;

import android.content.Context;

import com.quickblox.videochat.webrtc.QBRTCClient;

import rx.Observable;
import rx.Subscriber;
import theokanning.rover.user.User;

/**
 * Contains implementations of all methods common to {@link QuickBloxDriverChatClient} and {@link QuickBloxRobotChatClient}
 *
 * @author Theo Kanning
 */
public class QuickBloxChatClient {

    protected Context context;

    public QuickBloxChatClient(Context context) {
        this.context = context;
    }

    protected Observable<Boolean> loginAsUser(final User user,final Context context){
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

    public void initVideoChatClient(){
        QBRTCClient.getInstance(context).prepareToProcessCalls();
    }

    /**
     * Adapts {@link QuickBloxLoginTask.LoginTaskCallback} events to a subscriber
     */
    private class LoginTaskSubscriberAdapter implements QuickBloxLoginTask.LoginTaskCallback{
        private Subscriber<? super Boolean> subscriber;

        public LoginTaskSubscriberAdapter(Subscriber<? super Boolean> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onSuccess() {
            subscriber.onNext(true);
            subscriber.onCompleted();
            QuickBloxChatClient.this.initVideoChatClient();
        }

        @Override
        public void onFailure() {
            subscriber.onNext(false);
            subscriber.onCompleted();
        }
    }
}
