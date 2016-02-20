package theokanning.rover;

import javax.inject.Singleton;

import dagger.Component;
import theokanning.rover.activity.DriverActivityTest;
import theokanning.rover.chat.QuickBloxDriverChatClientTest;
import theokanning.rover.dagger.RoverComponent;
import theokanning.rover.dagger.UsbModule;

/**
 * Component for injecting modules into test classes
 *
 * @author Theo Kanning
 */
@Singleton
@Component(modules = {MockChatModule.class, UsbModule.class})
public interface TestComponent extends RoverComponent {
    void inject(TestApplication testApplication);
    void inject(QuickBloxDriverChatClientTest quickBloxDriverChatClientTest);
    void inject(DriverActivityTest driverActivityTest);
}
