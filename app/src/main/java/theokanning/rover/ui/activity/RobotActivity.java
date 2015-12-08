package theokanning.rover.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;

import java.util.HashMap;
import java.util.Map;

import theokanning.rover.R;
import theokanning.rover.ui.fragment.robot.WaitingFragment;

/**
 * Controls all call activity for the robot
 */
public class RobotActivity extends BaseActivity implements QBRTCClientSessionCallbacks {

    private static final String TAG = "RobotActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);

        initQbrtcClient();

        setFragment(new WaitingFragment(), true);
    }

    /**
     * Tells the client that this activity is prepared to process calls and sets this activity
     * as a listener for client callback
     */
    private void initQbrtcClient() {
        QBRTCClient.getInstance(this).prepareToProcessCalls();
        QBRTCClient.getInstance(this).addSessionCallbacksListener(this);

        QBChatService.getInstance().getVideoChatWebRTCSignalingManager()
                .addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
                    @Override
                    public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                        if (!createdLocally) {
                            QBRTCClient.getInstance(RobotActivity.this).addSignaling((QBWebRTCSignaling) qbSignaling);
                        }
                    }
                });
    }

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {

        Toast.makeText(this, "Call received", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Call received");

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("Key", "Robot");

        // Accept incoming call
        qbrtcSession.acceptCall(userInfo);
    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        //Should not happen
    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        //Should not happen
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        //Should not happen
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
