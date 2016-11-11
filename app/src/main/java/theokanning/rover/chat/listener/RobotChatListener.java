package theokanning.rover.chat.listener;
import theokanning.rover.chat.client.RobotChatClient;
/**
 * Callbacks that are specific to {@link RobotChatClient}
 *
 * @author Theo Kanning
 */
public interface RobotChatListener extends ChatListener {
    void onCallReceived(); //todo create answerable class?
}
