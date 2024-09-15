package application;
	
import javax.naming.CannotProceedException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		RobotGraphics robot = new RobotGraphics();
		
		robot.setRobotShape(RobotGraphics.Shape.REAR_WHEEL);
		robot.setRobotLength(0.8);
		robot.setWheelsSeparation(0.6);
		robot.setWheelsRadius(0.16, 0.16);
		robot.setWorkspaceDimensions(6, 6);
		robot.setRobotPose(3, 3);
		robot.setRobotOrientation(Math.PI / 4);
		robot.setMaxWheelsSpeed(15.0, 15.0);
		
		robot.setWheelsSpeed(4.0, 2.0);
		
		try {
			robot.startAnimation();
		} catch (CannotProceedException e) {
			e.printStackTrace();
		}
		
		Scene scene = new Scene(robot,400,400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
