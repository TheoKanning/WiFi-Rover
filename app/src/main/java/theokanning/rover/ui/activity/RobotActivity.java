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
import theokanning.rover.ui.fragment.WaitingFragment;

/**
 * Controls all call activity for the robot
 */
public class RobotActivity extends BaseActivity implements QBRTCClientSessionCallbacks {

    private static final String TAG = "RobotActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initQbrtcClient();
        showWaitingFragment();
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

    /**
     * Shows a waiting fragment to tell the user that the app is waiting for a connection
     */
    private void showWaitingFragment() {
        WaitingFragment fragment = new WaitingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WaitingFragment.WAITING_TEXT_EXTRA, "Waiting for connection...");
        fragment.setArguments(bundle);
        setFragment(fragment, true);
    }

    @Override
    public void onReceiveNewSession(final QBRTCSession qbrtcSession) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RobotActivity.this, "Call received", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Call received");

                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("Key", "Robot");

                // Accept incoming call
                qbrtcSession.acceptCall(qbrtcSession.getUserInfo());
            }
        });
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
