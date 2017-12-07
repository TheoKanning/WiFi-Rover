package theokanning.rover.chat.quickblox;

import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;

/**
 * Driver-specific extension of {@link QuickBloxChatClient}
 *
 * @author Theo Kanning
 */
public class QuickBloxDriverChatClient extends QuickBloxChatClient {

    public QuickBloxDriverChatClient(QBRTCClient qbrtcClient) {
        super(qbrtcClient);
    }

    @Override
    protected QBUser getUser() {
        return User.DRIVER.getQbUser();
    }

    @Override
    protected QBUser getOpponent() {
        return User.ROBOT.getQbUser();
    }
}
