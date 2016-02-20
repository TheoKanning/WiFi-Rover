package theokanning.rover.ui.fragment.driver;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;
import com.quickblox.videochat.webrtc.view.RTCGLVideoView;

import org.webrtc.VideoRenderer;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.ui.activity.SteeringListener;
import theokanning.rover.ui.activity.SteeringListener.Direction;
import theokanning.rover.ui.fragment.BaseFragment;

/**
 * Fragment that displays streaming video from robot and allows user to send commands
 */
public class ControlFragment extends BaseFragment implements QBRTCClientVideoTracksCallbacks {

    @Inject
    DriverChatClient driverChatClient;

    @Bind(R.id.videoView)
    public RTCGLVideoView videoView;

    @Bind(R.id.audio_toggle)
    ToggleButton audioToggle;

    private SteeringListener steeringListener;

    private Direction currentDirection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        ((RoverApplication) getActivity().getApplication()).getComponent().inject(this);
        ButterKnife.bind(this, view);

        driverChatClient.registerQbVideoCallbacksListener(this);
        steeringListener = (SteeringListener) getActivity(); //todo put in onattach or do directly
        return view;
    }

    @Override
    public void onLocalVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack) {
        //no need to do anything if we are only receiving a stream
    }

    /**
     * Add streamed video track to the video view
     *
     * @param qbrtcSession    current chat session
     * @param qbrtcVideoTrack video data to be shown on screen
     * @param userId          id of user who sent video
     */
    @Override
    public void onRemoteVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack, Integer userId) {
        qbrtcVideoTrack.addRenderer(new VideoRenderer(videoView.obtainVideoRenderer(RTCGLVideoView.RendererSurface.MAIN)));
    }

    private Handler handler = new Handler();

    Runnable directionsRunnable = new Runnable() {
        @Override
        public void run() {
            sendDirection(currentDirection);
            handler.postDelayed(directionsRunnable, 100);
        }
    };

    @OnTouch(R.id.up)
    public boolean up(View v, MotionEvent event) {
        updateDirectionPress(Direction.UP, event.getAction());
        return true;
    }

    @OnTouch(R.id.down)
    public boolean down(View v, MotionEvent event) {
        updateDirectionPress(Direction.DOWN, event.getAction());
        return true;
    }

    @OnTouch(R.id.left)
    public boolean left(View v, MotionEvent event) {
        updateDirectionPress(Direction.LEFT, event.getAction());
        return true;
    }

    @OnTouch(R.id.right)
    public boolean right(View v, MotionEvent event) {
        updateDirectionPress(Direction.RIGHT, event.getAction());
        return true;
    }

    /**
     * Updates direction commands based on button press changes
     *
     * @param direction which direction button
     * @param action    action returned by press MotionEvent
     */
    private void updateDirectionPress(Direction direction, int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            currentDirection = direction;
        } else if (action == MotionEvent.ACTION_UP) {
            currentDirection = null;
        }
    }

    /**
     * Sends a direction command to the SteeringListener
     *
     * @param direction all directional buttons pressed
     */
    private void sendDirection(Direction direction) {
        if (direction != null) {
            steeringListener.sendCommand(direction);
        }
    }

    @OnClick(R.id.audio_toggle)
    public void toggleAudio(View view){
        boolean enabled = ((ToggleButton) view).isChecked();
        //todo add mute feature
    }

    @OnClick(R.id.mic_toggle)
    public void toggleMicrophone(View view){
        boolean enabled = ((ToggleButton) view).isChecked();
        driverChatClient.setMicrophoneEnabled(enabled);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(directionsRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(directionsRunnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        driverChatClient.unregisterQbVideoCallbacksListener(this);
    }
}
