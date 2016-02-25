package theokanning.rover;

import org.junit.Test;

import theokanning.rover.ui.model.SteeringCommand;

import static org.junit.Assert.assertEquals;

public class SteeringCommandTest {

    private static final int MAXIMUM = SteeringCommand.ROBOT_COMMAND_MAX;

    @Test
    public void stationaryConversionIsCorrect() {
        SteeringCommand stationary = new SteeringCommand(0, 0);
        assertEquals(0, stationary.getRightMotorCommand(), 1);
        assertEquals(0, stationary.getLeftMotorCommand(), 1);
    }

    @Test
    public void rightConversionIsCorrect(){
        SteeringCommand right = new SteeringCommand(0, 1);
        assertEquals(-MAXIMUM, right.getRightMotorCommand(), 1);
        assertEquals(MAXIMUM, right.getLeftMotorCommand(), 1);
    }
    
    @Test
    public void forwardConversionIsCorrect(){
        SteeringCommand forward = new SteeringCommand((float)Math.PI/2, 1);
        assertEquals(MAXIMUM, forward.getRightMotorCommand(), 1);
        assertEquals(MAXIMUM, forward.getLeftMotorCommand(), 1);
    }

    @Test
    public void forwardRightConversionIsCorrect(){
        SteeringCommand forwardRight = new SteeringCommand((float)Math.PI/4, 1);
        assertEquals(0, forwardRight.getRightMotorCommand(), 1);
        assertEquals(MAXIMUM*Math.sin(Math.PI/4), forwardRight.getLeftMotorCommand(), 1);
    }
}