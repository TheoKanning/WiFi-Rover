package theokanning.rover.chat.quickblox;

import com.quickblox.users.model.QBUser;

import theokanning.rover.user.User;

/**
 * Robot-specific extension of {@link QuickBloxChatClient}
 *
 * @author Theo Kanning
 */
public class QuickBloxRobotChatClient extends QuickBloxChatClient{
    @Override
    protected QBUser getUser() {
        return User.ROBOT.getQbUser();
    }

    @Override
    protected QBUser getOpponent() {
        return User.DRIVER.getQbUser();
    }
}
