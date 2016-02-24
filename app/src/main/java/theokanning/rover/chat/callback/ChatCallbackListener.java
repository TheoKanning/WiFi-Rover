package theokanning.rover.chat.callback;

import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.chat.model.Message;

/**
 * All callback methods used by {@link DriverChatClient} and {@link RobotChatClient}
 *
 * @author Theo Kanning
 */
public interface ChatCallbackListener {
    void onSessionEnded();
    void onChatMessageReceived(Message message);
}
