package theokanning.rover.chat.callback;
import theokanning.rover.chat.client.DriverChatClient;

/**
 * Callbacks that are specific to {@link DriverChatClient}
 *
 * @author Theo Kanning
 */
public interface DriverChatCallbacks {
    void onCallAnswered(); //todo create answerable class?
    void onCallNotAnswered();
}
