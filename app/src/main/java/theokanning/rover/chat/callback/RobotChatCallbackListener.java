package theokanning.rover.chat.callback;
import theokanning.rover.chat.client.RobotChatClient;
/**
 * Callbacks that are specific to {@link RobotChatClient}
 *
 * @author Theo Kanning
 */
public interface RobotChatCallbackListener extends ChatCallbackListener{
    void onCallReceived(); //todo create answerable class?
}
