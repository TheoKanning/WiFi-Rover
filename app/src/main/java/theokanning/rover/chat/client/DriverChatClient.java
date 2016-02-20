package theokanning.rover.chat.client;

import android.content.Context;

import rx.Observable;
import theokanning.rover.chat.callback.DriverChatCallbackListener;

/**
 * All common chat methods plus those used by the driver
 *
 * @author Theo Kanning
 */
public interface DriverChatClient extends ChatClient {
    Observable<Boolean> loginAsDriver(Context context);
    //void startCall();
    void registerDriverChatCallbackListener(DriverChatCallbackListener listener);
    void unregisterDriverChatCallbackListener();
}
