package theokanning.rover.ui.activity;

import theokanning.rover.user.User;

/**
 * Interface for ModeSelectionFragment to initialize logging in via its activity
 */
public interface ModeSelectionInterface {
    void logInAndStartActivity(User user);
}