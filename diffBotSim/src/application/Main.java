package application;
	
import javax.naming.CannotProceedException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws CannotProceedException {
		
		RobotGraphics robot = new RobotGraphics();
		Joystick joystick = new Joystick(250, 250);
		
		// Units are SI (meters, radians, seconds)
		robot.setRobotShape(RobotGraphics.Shape.CENTERED_SQUARED);
		robot.setRobotLength(2.2);
		robot.setWheelsSeparation(1.6);
		robot.setWheelsRadius(0.4, 0.4);
		robot.setWorkspaceDimensions(32, 16);
		robot.setRobotPose(8, 1.9);
		robot.setRobotOrientation(Math.PI / 2);
		robot.setMaxWheelsSpeed(9.0, 9.0);
		robot.connectJoystick(joystick);
		robot.startAnimation();
		
		Scene scene = new Scene(robot,800,400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Differential Robot Simulator");
		primaryStage.show();
		
        Stage joystickStage = new Stage();
        Scene joystickScene = new Scene(joystick);
        joystickStage.setScene(joystickScene);
        joystickStage.setResizable(false);
        joystickStage.setTitle("Controller");
        joystickStage.show();
        
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
