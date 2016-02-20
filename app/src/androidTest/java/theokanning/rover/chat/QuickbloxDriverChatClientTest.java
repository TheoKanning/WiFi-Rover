package theokanning.rover.chat;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import theokanning.rover.TestApplication;
import theokanning.rover.TestComponent;
import theokanning.rover.chat.client.DriverChatClient;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class QuickBloxDriverChatClientTest {

    @Inject
    DriverChatClient driverChatClient;

    private Context context;

    @Before
    public void setup() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        TestApplication app = (TestApplication) instrumentation.getTargetContext().getApplicationContext();
        context = app;
        TestComponent component = (TestComponent) app.getComponent();
        component.inject(this);
    }

    @Test
    public void testLogin() {
        final Observable<Boolean> observable = driverChatClient.loginAsDriver(context);
        TestSubscriber<Boolean> subscriber = new TestSubscriber<>();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        try {
            //todo fix threading issue, currently does not wait for observable to emit before asserting
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            //do nothing
        }

        if (!subscriber.getOnErrorEvents().isEmpty())
            fail("Unexpected Error: " + subscriber.getOnErrorEvents().get(0).getMessage());

        subscriber.assertCompleted();
        subscriber.assertValueCount(1);
        Boolean response = subscriber.getOnNextEvents().get(0);
        assertTrue(response);
    }
}
