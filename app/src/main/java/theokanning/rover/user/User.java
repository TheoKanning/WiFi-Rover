package theokanning.rover.user;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Contains factory methods for getting driver and robot user credentials
 * Users are returned as new objects because they are mutable
 */
public class User {


    private static final String TAG = "User";
    private static final String DRIVER_USERNAME = "driver";
    private static final String DRIVER_PASSWORD = "qh8ZhC5z";

    private static final String ROBOT_USERNAME = "robot";
    private static final String ROBOT_PASSWORD = "hxJVfsSd";

    public static QBUser getDriver(){
        return new QBUser(DRIVER_USERNAME, DRIVER_PASSWORD);
    }

    public static QBUser getRobot(){
        return new QBUser(ROBOT_USERNAME, ROBOT_PASSWORD);
    }

    /**
     * Adds user to QuickBlox server, only needs to be called once per user
     * Shows a toast with request result and prints all error messages if any are received
     * @param newUser new user to be added to server
     * @param context context in which to show toast message
     */
    public static void addUserToQuickBlox(final QBUser newUser, final Context context){
        QBUsers.signUp(newUser, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                Log.d(TAG, newUser.getLogin() + " signed up successfully");
                Toast.makeText(context, newUser.getLogin() + " signed up successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(List<String> errors) {
                Toast.makeText(context, newUser.getLogin() + " not signed up", Toast.LENGTH_SHORT).show();
                for(String error : errors){
                    Log.e(TAG, error);
                }
            }
        });
    }
}
