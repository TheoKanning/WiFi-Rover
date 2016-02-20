package theokanning.rover.activity;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import theokanning.rover.R;
import theokanning.rover.ui.activity.DriverActivity;
import theokanning.rover.ui.activity.ModeSelectionActivity;
import theokanning.rover.ui.activity.RobotActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ModeSelectionActivityTest {

    @Rule
    public ActivityTestRule<ModeSelectionActivity> activityRule = new ActivityTestRule<>(
            ModeSelectionActivity.class,
            true,
            true);

    @Test
    public void testStartingActivities() {
        activityRule.getActivity();

        Instrumentation.ActivityMonitor driverMonitor = InstrumentationRegistry.getInstrumentation()
                .addMonitor(DriverActivity.class.getName(), null, true);
        Instrumentation.ActivityMonitor robotMonitor = InstrumentationRegistry.getInstrumentation()
                .addMonitor(RobotActivity.class.getName(), null, true);

        onView(withId(R.id.driver)).perform(click());
        assertEquals(1, driverMonitor.getHits());

        onView(withId(R.id.robot)).perform(click());
        assertEquals(1, robotMonitor.getHits());
    }
}
