package application;
	
import javax.naming.CannotProceedException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		RobotGraphics robot = new RobotGraphics();
		Joystick joystick = new Joystick(250, 250);
		
		// Units are SI (meters, radians, seconds)
		robot.setRobotShape(RobotGraphics.Shape.CENTERED_SQUARED);
		robot.setRobotLength(1.2);
		robot.setWheelsSeparation(0.6);
		robot.setWheelsRadius(0.3, 0.3);
		robot.setWorkspaceDimensions(32, 16);
		robot.setRobotPose(8, 0.9);
		robot.setRobotOrientation(Math.PI / 2);
		robot.setMaxWheelsSpeed(9.0, 9.0);
		robot.connectJoystick(joystick);
		
		try {
			robot.startAnimation();
		} catch (CannotProceedException e) {
			e.printStackTrace();
		}
		
		Scene scene = new Scene(robot,800,400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
        Stage joystickStage = new Stage();
        Scene joystickScene = new Scene(joystick);
        joystickStage.setScene(joystickScene);
        joystickStage.setResizable(false);
        joystickStage.show();
        
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
