package theokanning.rover;

import javax.inject.Singleton;

import dagger.Component;
import theokanning.rover.chat.QuickbloxDriverChatClientTest;
import theokanning.rover.dagger.RoverComponent;
import theokanning.rover.dagger.UsbModule;

/**
 * Component for injecting mock modules into test class
 *
 * @author Theo Kanning
 */
@Singleton
@Component(modules = {TestModule.class, UsbModule.class})
public interface TestComponent extends RoverComponent {
    void inject(TestApplication testApplication);
    void inject(QuickbloxDriverChatClientTest test);
}
