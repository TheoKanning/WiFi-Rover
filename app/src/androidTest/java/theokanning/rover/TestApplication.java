package theokanning.rover;

import theokanning.rover.dagger.RoverComponent;
import theokanning.rover.dagger.RobotConnectionModule;

/**
 * Test application that enables injecting mock dependencies if desired
 *
 * @author Theo Kanning
 */
public class TestApplication extends RoverApplication {

    TestComponent testComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        buildTestComponent(false);
    }

    private void buildTestComponent(boolean mock){
        testComponent = DaggerTestComponent.builder()
                .mockChatModule(new MockChatModule(this, mock))
                .usbModule(new RobotConnectionModule(this))
                .build();
    }

    public void setMockMode(boolean mockMode){
        buildTestComponent(mockMode);
    }

    @Override
    public RoverComponent getComponent(){
        return testComponent;
    }
}
