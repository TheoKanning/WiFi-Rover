package theokanning.rover.chat.listener;
import theokanning.rover.chat.client.DriverChatClient;

/**
 * Callbacks that are specific to {@link DriverChatClient}
 *
 * @author Theo Kanning
 */
public interface DriverChatListener extends ChatListener {
    void onCallAnswered(); //todo create answerable class?
    void onCallNotAnswered();
}
