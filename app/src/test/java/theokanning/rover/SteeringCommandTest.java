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
    public void forwardConversionIsCorrect(){
        SteeringCommand forward = new SteeringCommand(1, 0);
        assertEquals(MAXIMUM, forward.getRightMotorCommand(), 1);
        assertEquals(MAXIMUM, forward.getLeftMotorCommand(), 1);
    }

    @Test
    public void forwardRightConversionIsCorrect(){
        SteeringCommand forwardRight = new SteeringCommand(1, .5f);
        assertEquals(MAXIMUM*1.5, forwardRight.getRightMotorCommand(), 1);
        assertEquals(MAXIMUM*.5, forwardRight.getLeftMotorCommand(), 1);
    }
}