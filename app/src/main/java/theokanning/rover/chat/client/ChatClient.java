package theokanning.rover.chat.client;

import android.content.Context;

import rx.Observable;
import theokanning.rover.chat.listener.ChatListener;
import theokanning.rover.chat.model.Message;

/**
 * All chat methods used by both Driver and Robot
 *
 * @author Theo Kanning
 */
public interface ChatClient {
    void registerChatCallbackListener(ChatListener listener);
    void unregisterChatCallbackListener();
    Observable<Boolean> login(Context context);
    void sendMessage(Message message);
    void endCall();
    void setMicrophoneEnabled(boolean enabled);
    //void changeAudioSettings(); //todo create object for parameters
}
