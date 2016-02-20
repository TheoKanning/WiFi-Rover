package theokanning.rover.activity;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;

import rx.Observable;
import theokanning.rover.TestApplication;
import theokanning.rover.TestComponent;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.ui.activity.DriverActivity;
import theokanning.rover.ui.activity.ModeSelectionActivity;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DriverActivityTest {

    @Inject
    DriverChatClient mockDriverChatClient;

    private Context context;

    @Rule
    public ActivityTestRule<DriverActivity> activityTestRule =
            new ActivityTestRule<>(DriverActivity.class, true, false);

    @Before
    public void setup(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        TestApplication app = (TestApplication) instrumentation.getTargetContext().getApplicationContext();
        context = app;
        app.setMockMode(true);
        TestComponent component = (TestComponent) app.getComponent();
        component.inject(this);
    }

    @Test
    public void loginFailure(){
        Mockito.when(mockDriverChatClient.login(context)).thenReturn(Observable.just(false));

        Instrumentation.ActivityMonitor monitor = InstrumentationRegistry.getInstrumentation()
                .addMonitor(ModeSelectionActivity.class.getName(), null, true);

        activityTestRule.launchActivity(new Intent());

        assertEquals(1, monitor.getHits());
    }

    @After
    public void tearDown(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        TestApplication app = (TestApplication) instrumentation.getTargetContext().getApplicationContext();
        app.setMockMode(false);
    }
}
