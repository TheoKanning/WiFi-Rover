package theokanning.rover.ui.model;

/**
 * Steering command comprised of a throttle and a differential
 *
 * @author Theo Kanning
 */
public class SteeringCommand {

    public static final int ROBOT_COMMAND_MAX = 175;
    private static final String ROBOT_START_CHARACTER = "(";
    private static final String  ROBOT_STOP_CHARACTER = ")";
    private static final String  ROBOT_RIGHT_CHARACTER = "R";
    private static final String  ROBOT_LEFT_CHARACTER = "L";

    private float throttle;
    private float differential;

    public SteeringCommand(float throttle, float differential) {
        this.throttle = throttle;
        this.differential = differential;
    }

    public float getThrottle() {
        return throttle;
    }

    public float getDifferential() {
        return differential;
    }

    /**
     * Convert values to a string that can be read directly by the robot
     */
    public String toRobotReadableString() {
        return getRightMotorCommandString() + getLeftMotorCommandString();
    }

    private String getRightMotorCommandString() {
        int right = getRightMotorCommand();
        return ROBOT_START_CHARACTER + ROBOT_RIGHT_CHARACTER + String.valueOf(right) + ROBOT_STOP_CHARACTER;
    }

    private String getLeftMotorCommandString() {
        int left = getLeftMotorCommand();
        return ROBOT_START_CHARACTER + ROBOT_LEFT_CHARACTER + String.valueOf(left) + ROBOT_STOP_CHARACTER;
    }

    public int getRightMotorCommand() {
        return (int) ( ROBOT_COMMAND_MAX * (throttle + differential));
    }

    public int getLeftMotorCommand() {
        return (int) ( ROBOT_COMMAND_MAX * (throttle - differential));
    }
}
