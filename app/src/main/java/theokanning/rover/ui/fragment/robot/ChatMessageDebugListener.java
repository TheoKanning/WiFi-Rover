package theokanning.rover.ui.fragment.robot;

import theokanning.rover.chat.model.Message;

/**
 * Interface that displays chat messages for debugging
 */
public interface ChatMessageDebugListener {
    void showMessage(Message message);
}
