package theokanning.rover.chat.quickblox;

import rx.Subscriber;

/**
 * Adapts {@link QuickBloxLoginTask.LoginTaskCallback} events to a subscriber
 *
 * todo is adapter the right word?
 * @author Theo Kanning
 */
public class LoginTaskSubscriberAdapter implements QuickBloxLoginTask.LoginTaskCallback {
    private final QuickBloxChatClient chatClient;
    private Subscriber<? super Boolean> subscriber;

    public LoginTaskSubscriberAdapter(Subscriber<? super Boolean> subscriber, QuickBloxChatClient chatClient) {
        this.chatClient = chatClient;
        this.subscriber = subscriber;
    }

    @Override
    public void onSuccess() {
        subscriber.onNext(true);
        subscriber.onCompleted();
        chatClient.initVideoChatClient();
    }

    @Override
    public void onFailure() {
        subscriber.onNext(false);
        subscriber.onCompleted();
    }
}