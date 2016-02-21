package theokanning.rover.chat.quickblox;

import com.quickblox.users.model.QBUser;

import theokanning.rover.user.User;

/**
 * Driver-specific extension of {@link QuickBloxChatClient}
 *
 * @author Theo Kanning
 */
public class QuickBloxDriverChatClient extends QuickBloxChatClient{
    @Override
    protected QBUser getUser() {
        return User.DRIVER.getQbUser();
    }

    @Override
    protected QBUser getOpponent() {
        return User.ROBOT.getQbUser();
    }
}
