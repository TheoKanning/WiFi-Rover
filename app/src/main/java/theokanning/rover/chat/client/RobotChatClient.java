package theokanning.rover.chat.client;

import theokanning.rover.chat.callback.RobotChatCallbackListener;

/**
 * All common chat methods plus those used by the robot
 *
 * @author Theo Kanning
 */
public interface RobotChatClient extends ChatClient {
    void registerRobotChatCallbackListener(RobotChatCallbackListener listener);
    void unregisterRobotChatCallbackListener();
}
