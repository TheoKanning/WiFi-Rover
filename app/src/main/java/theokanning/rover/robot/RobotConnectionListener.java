package theokanning.rover.robot;

/**
 * A listener for all important events in a connection to a robot
 *
 * @author Theo Kanning
 */
public interface RobotConnectionListener {
    void onConnect();
    void onDisconnect();
    void onMessageReceived(String message);
}
