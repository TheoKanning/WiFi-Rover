package theokanning.rover.chat;

import rx.Observable;

/**
 * All chat methods used by both Driver and Robot
 *
 * @author Theo Kanning
 */
public interface ChatClient {
    Observable<Boolean> login();
    //void endCall();
    //void changeAudioSettings(); //todo create object for parameters
    //void sendChatMessage(String message);
    //void registerChatCallbacks(); //todo take chat callbacks object
}
