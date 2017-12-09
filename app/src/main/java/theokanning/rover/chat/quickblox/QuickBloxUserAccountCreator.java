package theokanning.rover.chat.quickblox;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

/**
 * Gives method for creating new QuickBlox accounts, only needs to be used once per account
 *
 * @author Theo Kanning
 */
public class QuickBloxUserAccountCreator {

    private static final String TAG = "QbUserAccountCreator";

    /**
     * Adds user to QuickBlox server, only needs to be called once per user
     * Shows a toast with request result and prints all error messages if any are received
     *
     * @param newUser new user to be added to server
     * @param context context in which to show toast message
     */
    public static void addUserToQuickBlox(final QBUser newUser, final Context context) {
        QBUsers.signUp(newUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                Log.d(TAG, newUser.getLogin() + " signed up successfully");
                Toast.makeText(context, newUser.getLogin() + " signed up successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(QBResponseException exception) {
                Toast.makeText(context, newUser.getLogin() + " not signed up", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Could not add user", exception);
            }
        });
    }
}
