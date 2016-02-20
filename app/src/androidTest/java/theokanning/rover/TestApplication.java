package theokanning.rover;

import theokanning.rover.dagger.UsbModule;

/**
 * Test application that enables injecting mock dependencies is desired
 *
 * @author Theo Kanning
 */
public class TestApplication extends RoverApplication {

    TestComponent testComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        testComponent = DaggerTestComponent.builder()
                .testModule(new TestModule())
                .usbModule(new UsbModule(this))
                .build();
    }

    public TestComponent getTestComponent(){
        return testComponent;
    }
}
