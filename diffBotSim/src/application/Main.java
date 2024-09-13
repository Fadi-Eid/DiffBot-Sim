package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		RobotGraphics robot = new RobotGraphics();
		
		robot.setRobotLength(2.0);
		robot.setWheelsRadius(0.05, 0.05);
		robot.setWorkspaceDimensions(10, 10);
		robot.setRobotPose(5, 5);
		robot.setWheelsSeparation(0.2);
		robot.setRobotOrientation(Math.PI);
		
		robot.setWheelsSpeed(0.9, 0.9);
		
		robot.startAnimation();
		
		Scene scene = new Scene(robot,400,400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
