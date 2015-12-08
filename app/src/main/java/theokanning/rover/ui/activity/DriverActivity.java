package theokanning.rover.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import theokanning.rover.R;
import theokanning.rover.ui.fragment.driver.ConnectFragment;
import theokanning.rover.user.User;

/**
 * Activity where user controls remote device and watches video stream. Starts by showing
 * instructions to connect to remote device, then connects and shows video stream.
 */
public class DriverActivity extends BaseActivity implements QBRTCClientSessionCallbacks {
    private static final String TAG = "DriverActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        setFragment(new ConnectFragment(), true);
    }

    /**
     * Starts a connection to the robot user. Called by ConnectFragment.
     */
    public void connect() {
        QBRTCClient.getInstance(this).prepareToProcessCalls();
        QBRTCClient.getInstance(this).addSessionCallbacksListener(this);
        //TODO implement QBRTCSessionConnectionCallbacks

        List<Integer> ids = new ArrayList<>();
        ids.add(User.ROBOT_ID);

        //Init session
        QBRTCSession session = QBRTCClient.getInstance(this).createNewSessionWithOpponents(ids,
                QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("key", "robot");

        //Start call
        session.startCall(userInfo);
    }

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        //Should not happen
    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        Toast.makeText(this, "Robot did not answer", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Robot did not answer");
    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        Log.e(TAG, "Call rejected, should not happen");
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        Toast.makeText(this, "Call started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Call started");
        //TODO start control fragment here
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {

    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {

    }
}
