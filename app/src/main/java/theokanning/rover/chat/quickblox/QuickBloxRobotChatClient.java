package theokanning.rover.chat.quickblox;

import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;

/**
 * Robot-specific extension of {@link QuickBloxChatClient}
 *
 * @author Theo Kanning
 */
public class QuickBloxRobotChatClient extends QuickBloxChatClient {

    public QuickBloxRobotChatClient(QBRTCClient qbrtcClient) {
        super(qbrtcClient);
    }

    @Override
    protected QBUser getUser() {
        return User.ROBOT.getQbUser();
    }

    @Override
    protected QBUser getOpponent() {
        return User.DRIVER.getQbUser();
    }
}
