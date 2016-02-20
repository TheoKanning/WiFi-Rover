package theokanning.rover.chat.client;

import android.content.Context;

import rx.Observable;
import theokanning.rover.chat.callback.RobotChatCallbackListener;

/**
 * All common chat methods plus those used by the robot
 *
 * @author Theo Kanning
 */
public interface RobotChatClient extends ChatClient {
    Observable<Boolean> loginAsRobot(Context context);
    void registerRobotChatCallbackListener(RobotChatCallbackListener listener);
    void unregisterRobotChatCallbackListener();
}
