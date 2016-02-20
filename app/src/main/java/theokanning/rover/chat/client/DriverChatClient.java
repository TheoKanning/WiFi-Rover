package theokanning.rover.chat.client;

import android.content.Context;

import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;

import rx.Observable;
import theokanning.rover.chat.callback.DriverChatCallbackListener;

/**
 * All common chat methods plus those used by the driver
 *
 * @author Theo Kanning
 */
public interface DriverChatClient extends ChatClient {
    Observable<Boolean> loginAsDriver(Context context);
    void startCall();
    void registerDriverChatCallbackListener(DriverChatCallbackListener listener);
    void unregisterDriverChatCallbackListener();

    void sendMessageToRobot(String message);
    //todo remove qb reference!
    void registerQbVideoCallbacksListener(QBRTCClientVideoTracksCallbacks callbacks);
    void unregisterQbVideoCallbacksListener(QBRTCClientVideoTracksCallbacks callbacks);
}
