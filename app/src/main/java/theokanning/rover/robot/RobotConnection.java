package theokanning.rover.robot;

/**
 * All methods available for interacting with the robot
 *
 * @author Theo Kanning
 */
public interface RobotConnection {
    void connect(RobotConnectionListener robotConnectionListener);
    void disconnect();
    void sendMessage(String message);
}
