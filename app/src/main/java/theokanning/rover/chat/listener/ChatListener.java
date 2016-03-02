package theokanning.rover.chat.listener;

import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.chat.client.RobotChatClient;
import theokanning.rover.chat.model.Message;

/**
 * All callback methods used by {@link DriverChatClient} and {@link RobotChatClient}
 *
 * @author Theo Kanning
 */
public interface ChatListener {
    void onSessionEnded();
    void onChatMessageReceived(Message message);
}
