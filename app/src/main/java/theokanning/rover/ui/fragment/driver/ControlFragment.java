package theokanning.rover.ui.fragment.driver;


import android.os.Bundle;
import android.view.LayoutInflater;
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
import theokanning.rover.R;
import theokanning.rover.RoverApplication;
import theokanning.rover.chat.client.DriverChatClient;
import theokanning.rover.chat.model.Message;
import theokanning.rover.ui.fragment.BaseFragment;
import theokanning.rover.ui.model.SteeringCommand;
import theokanning.rover.ui.view.Dpad;

/**
 * Fragment that displays streaming video from robot and allows user to send commands
 */
public class ControlFragment extends BaseFragment implements QBRTCClientVideoTracksCallbacks, Dpad.DpadListener {

    @Inject
    DriverChatClient driverChatClient;

    @Bind(R.id.videoView)
    public RTCGLVideoView videoView;

    @Bind(R.id.dpad)
    Dpad dpad;

    public static ControlFragment newInstance(){
        return new ControlFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        ((RoverApplication) getActivity().getApplication()).getComponent().inject(this);
        ButterKnife.bind(this, view);
        driverChatClient.registerQbVideoCallbacksListener(this);
        driverChatClient.setMicrophoneEnabled(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dpad.registerListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        dpad.unregisterListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        driverChatClient.unregisterQbVideoCallbacksListener(this);
    }

    @Override
    public void onLocalVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack) {
        //no need to do anything if we are only receiving a stream
    }

    @Override
    public void onRemoteVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack, Integer userId) {
        qbrtcVideoTrack.addRenderer(new VideoRenderer(videoView.obtainVideoRenderer(RTCGLVideoView.RendererSurface.MAIN)));
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
    public void onDpadPressed(SteeringCommand command) {
        Message message = new Message(Message.Tag.ROBOT, command.toRobotReadableString());
        driverChatClient.sendMessage(message);
    }
}
