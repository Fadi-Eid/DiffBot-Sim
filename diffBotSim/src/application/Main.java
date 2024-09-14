package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		RobotGraphics robot = new RobotGraphics();
		
		robot.setRobotLength(0.6);
		robot.setWheelsSeparation(0.3);
		robot.setWheelsRadius(0.15, 0.15);
		robot.setWorkspaceDimensions(6, 6);
		robot.setRobotPose(3, 3);
		robot.setRobotOrientation(Math.PI / 2);
		
		robot.setWheelsSpeed(+10.0, -10.00);
		
		robot.startAnimation();
		
		Scene scene = new Scene(robot,400,400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
