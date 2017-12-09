package theokanning.rover.chat.quickblox;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

/**
 * Class that logs in to QuickBlox. Should be called on background thread
 *
 * @author Theo Kanning
 */
class QuickBloxLoginService {

    private static final String TAG = "QuickBloxLoginService";

    boolean login(QBUser user, Context context) {

        try {
            QBAuth.createSession(user).perform();
        } catch (QBResponseException e) {
            e.printStackTrace();
            return false;
        }

        // if something is wrong here, this used to check if QbChat service was initialized

        QBChatService chatService = QBChatService.getInstance();

        if (chatService.isLoggedIn()) {
            try {
                Log.d(TAG, "Logging out");
                chatService.logout();
            } catch (SmackException.NotConnectedException e) {
                Log.d(TAG, "Failed to log out");
                e.printStackTrace();
                return false;
            }
        }

        try {
            chatService.login(user);
        } catch (XMPPException | IOException | SmackException e) {
            e.printStackTrace();
            return false;
        }

        Log.d(TAG, "Logged in successfully");
        return true;
    }
}
