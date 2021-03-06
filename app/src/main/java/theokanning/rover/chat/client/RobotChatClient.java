package theokanning.rover.chat.client;

import theokanning.rover.chat.listener.RobotChatListener;

/**
 * All common chat methods plus those used by the robot
 *
 * @author Theo Kanning
 */
public interface RobotChatClient extends ChatClient {
    void registerRobotChatCallbackListener(RobotChatListener listener);
    void unregisterRobotChatCallbackListener();
}
