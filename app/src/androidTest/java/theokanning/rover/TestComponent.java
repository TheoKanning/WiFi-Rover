package theokanning.rover;

import javax.inject.Singleton;

import dagger.Component;
import theokanning.rover.chat.QuickBloxDriverChatClientTest;
import theokanning.rover.dagger.ChatModule;
import theokanning.rover.dagger.RoverComponent;
import theokanning.rover.dagger.UsbModule;

/**
 * Component for injecting regular modules into test class
 *
 * @author Theo Kanning
 */
@Singleton
@Component(modules = {ChatModule.class, UsbModule.class})
public interface TestComponent extends RoverComponent {
    void inject(TestApplication testApplication);
    void inject(QuickBloxDriverChatClientTest test);
}
