package application;

public class JoystickRobotLogic {
	public JoystickRobotLogic(RobotGraphics robot, Joystick joystick) {
		double joystickX = joystick.getHandleX();
		double joystickY = joystick.getHandleY();
		double maxRightVel = robot.getMaxRightWheelSpeed();
		double maxLeftVel = robot.getMaxLeftWheelSpeed();
		double rightWheelVel = 0.0;
		double leftWheelVel = 0.0;
		
		// Implement the logic that links the joystick position with the motor velocities here
        leftWheelVel = (joystickY * maxLeftVel) + (joystickX * maxLeftVel);
        rightWheelVel = (joystickY * maxRightVel) - (joystickX * maxRightVel);

        double maxWheelVel = Math.max(Math.abs(leftWheelVel), Math.abs(rightWheelVel));
        if (maxWheelVel > maxLeftVel || maxWheelVel > maxRightVel) {
            leftWheelVel = (leftWheelVel / maxWheelVel) * maxLeftVel;
            rightWheelVel = (rightWheelVel / maxWheelVel) * maxRightVel;
        }		
		robot.setWheelsSpeed(leftWheelVel, rightWheelVel);
	}
}
