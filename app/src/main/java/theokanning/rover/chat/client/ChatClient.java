package theokanning.rover.chat.client;

import theokanning.rover.chat.callback.ChatCallbackListener;

/**
 * All chat methods used by both Driver and Robot
 *
 * @author Theo Kanning
 */
public interface ChatClient {
    void registerChatCallbackListener(ChatCallbackListener listener);
    void unregisterChatCallbackListener();

    void endCall();
    void setMicrophoneEnabled(boolean enabled);
    //void changeAudioSettings(); //todo create object for parameters
}
