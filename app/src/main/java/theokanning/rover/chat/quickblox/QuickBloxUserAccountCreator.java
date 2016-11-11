package theokanning.rover.chat.quickblox;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.List;

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
        QBUsers.signUp(newUser, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                Log.d(TAG, newUser.getLogin() + " signed up successfully");
                Toast.makeText(context, newUser.getLogin() + " signed up successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(List<String> errors) {
                Toast.makeText(context, newUser.getLogin() + " not signed up", Toast.LENGTH_SHORT).show();
                for (String error : errors) {
                    Log.e(TAG, error);
                }
            }
        });
    }
}
