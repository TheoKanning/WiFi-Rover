package theokanning.rover.chat.client;

import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;

import theokanning.rover.chat.listener.DriverChatListener;

/**
 * All common chat methods plus those used by the driver
 *
 * @author Theo Kanning
 */
public interface DriverChatClient extends ChatClient {
    void startCall();
    void registerDriverChatCallbackListener(DriverChatListener listener);
    void unregisterDriverChatCallbackListener();
    //todo remove qb reference!
    void registerQbVideoCallbacksListener(QBRTCClientVideoTracksCallbacks callbacks);
    void unregisterQbVideoCallbacksListener(QBRTCClientVideoTracksCallbacks callbacks);
}
