package theokanning.rover.chat.client;

import theokanning.rover.chat.callback.ChatCallbackListener;

/**
 * All chat methods used by both Driver and Robot
 *
 * @author Theo Kanning
 */
public interface ChatClient {
    //void endCall();
    //void changeAudioSettings(); //todo create object for parameters
    void sendChatMessage(String message);
    void registerChatCallbackListener(ChatCallbackListener listener);
    void unregisterChatCallbackListener();
}
