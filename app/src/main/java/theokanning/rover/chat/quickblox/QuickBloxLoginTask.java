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
 * Background task for logging in to QuickBlox service
 *
 * @author Theo Kanning
 */
public class QuickBloxLoginTask extends AsyncTask<Void, Void, Boolean> {

    //todo wrap try catch blocks in separate methods

    private static final String TAG = "QuickBloxLoginTask";

    public interface LoginTaskCallback {
        void onSuccess();

        void onFailure();
    }

    private LoginTaskCallback callback;

    private Context context;

    private QBUser user;

    public QuickBloxLoginTask(QBUser user, Context context, LoginTaskCallback callback) {
        this.user = user;
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            QBAuth.createSession(user);
        } catch (QBResponseException e) {
            e.printStackTrace();
            return false;
        }

        if (!QBChatService.isInitialized()) {
            QBChatService.init(context);
        }

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

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
         if(aBoolean){
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }
}
