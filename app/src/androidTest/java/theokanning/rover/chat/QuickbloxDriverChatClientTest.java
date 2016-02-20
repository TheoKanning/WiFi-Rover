package theokanning.rover.chat;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import rx.Observable;
import rx.observers.TestSubscriber;
import theokanning.rover.TestApplication;
import theokanning.rover.TestComponent;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class QuickbloxDriverChatClientTest {

    @Inject
    DriverChatClient driverChatClient;

    @Before
    public void setup() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        TestApplication app = (TestApplication) instrumentation.getTargetContext().getApplicationContext();
        TestComponent component = app.getTestComponent();
        component.inject(this);
    }

    @Test
    public void testLogin(){
        final Observable<Boolean> observable = driverChatClient.logIn();
        TestSubscriber<Boolean> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);

        if(!subscriber.getOnErrorEvents().isEmpty())
            fail("Unexpected Error:" + subscriber.getOnErrorEvents().get(0).getMessage());

        subscriber.assertCompleted();
        subscriber.assertValueCount(1);
        Boolean response = subscriber.getOnNextEvents().get(0);
        assertTrue(response);
    }
}
