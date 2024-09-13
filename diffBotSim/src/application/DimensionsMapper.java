package application;

// This class transforms the real dimensions in SI to screen dimensions in pixels
abstract class DimensionsMapper {
	public static double metersToPixelsX(RobotGraphics robot, double value) {
		double workspaceWidth = robot.getWorkspaceWidth();
		double screenWidth = robot.getWidth();
		
		return value * screenWidth / workspaceWidth;
	}
	public static double metersToPixelsY(RobotGraphics robot, double value) {
		double workspaceHeight = robot.getWorkspaceHeight();
		double screenHeight = robot.getHeight();
		
		return value * screenHeight / workspaceHeight;
	}
}
