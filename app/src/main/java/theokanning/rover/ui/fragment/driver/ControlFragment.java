package theokanning.rover.ui.fragment.driver;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;
import com.quickblox.videochat.webrtc.view.RTCGLVideoView;

import org.webrtc.VideoRenderer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import theokanning.rover.R;
import theokanning.rover.ui.activity.DriverActivity;
import theokanning.rover.ui.activity.SteeringListener;
import theokanning.rover.ui.fragment.BaseFragment;

/**
 * Fragment that displays streaming video from robot and allows user to send commands
 */
public class ControlFragment extends BaseFragment implements QBRTCClientVideoTracksCallbacks {


    @Bind(R.id.videoView)
    public RTCGLVideoView videoView;

    private SteeringListener steeringListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_control, container, false);
        ButterKnife.bind(this,view);

        ((DriverActivity) getActivity()).addVideoTrackCallbacksListener(this);
        steeringListener = (SteeringListener) getActivity();
        return view;
    }

    @Override
    public void onLocalVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack) {
        //no need to do anything if we are only receiving a stream
    }

    /**
     * Add streamed video track to the video view
     * @param qbrtcSession current chat session
     * @param qbrtcVideoTrack video data to be shown on screen
     * @param userId id of user who sent video
     */
    @Override
    public void onRemoteVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack, Integer userId) {
        qbrtcVideoTrack.addRenderer(new VideoRenderer(videoView.obtainVideoRenderer(RTCGLVideoView.RendererSurface.MAIN)));
    }

    @OnTouch(R.id.up)
    public void up(){
        sendDirection(SteeringListener.Direction.UP);
    }

    @OnClick(R.id.down)
    public void down(){
        sendDirection(SteeringListener.Direction.DOWN);
    }

    @OnClick(R.id.left)
    public void left(){
        sendDirection(SteeringListener.Direction.LEFT);
    }

    @OnClick(R.id.right)
    public void right(){
        sendDirection(SteeringListener.Direction.RIGHT);
    }

    /**
     * Sends a direction command to the SteeringListener
     *
     * @param direction direction that user has pressed, up/down etc...
     */
    private void sendDirection(SteeringListener.Direction direction){
        steeringListener.sendCommand(direction);
    }
}
