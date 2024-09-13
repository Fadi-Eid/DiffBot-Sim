package application;
//Ref: https://www.youtube.com/watch?v=RZlZcDxQ8P4

abstract class DFKEquation {
	
	public static double computeXVel(RobotGraphics robot) {
		double rR = robot.getRightWheelRadius();
		double rL = robot.getLeftWheelRadius();
		double theta = robot.getRobotOrientation();
		double phiR = robot.getRightWheelSpeed();
		double phiL = robot.getLeftWheelSpeed();
		
		double xVel = (rR/2) * Math.cos(theta) * phiR;
		xVel += (rL/2) * Math.cos(theta) * phiL;
		
		return xVel;
	}
	public static double computeYVel(RobotGraphics robot) {
		double rR = robot.getRightWheelRadius();
		double rL = robot.getLeftWheelRadius();
		double theta = robot.getRobotOrientation();
		double phiR = robot.getRightWheelSpeed();
		double phiL = robot.getLeftWheelSpeed();
		
		double xVel = (rR/2) * Math.sin(theta) * phiR;
		xVel += (rL/2) * Math.sin(theta) * phiL;
		
		return xVel;
	}
	public static double computeAngularVel(RobotGraphics robot) {
		double rR = robot.getRightWheelRadius();
		double rL = robot.getLeftWheelRadius();
		double phiR = robot.getRightWheelSpeed();
		double phiL = robot.getLeftWheelSpeed();
		double wheelsSep = robot.getWheelsSeparation();
		
		return (rR/wheelsSep)*phiR - (rL/wheelsSep)*phiL;
	}
}
