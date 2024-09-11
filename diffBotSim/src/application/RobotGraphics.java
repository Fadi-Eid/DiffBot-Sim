package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class RobotGraphics extends Pane {
	// Three shapes are supported
	public enum Shape {
		REAR_WHEEL, CENTERED_SQUARED, CENTERED_CIRCLE
	}
	// Note: Units are SI (Radiant, Meter, Second)
	private Shape		shape = Shape.REAR_WHEEL;
	private double 		wheelsSeparation;
	private double 		rightWheelRadius, leftWheelRadius;
	private double[] 	workspaceDimensions = new double[2];
	private double 		xPose, yPose;
	private double 		xVel, yVel;
	private double 		orientation = 0.0;
	private double 		refreshRate = 50.0; // 1/DT (Hz)
	private double 		rightWheelSpeed = 0.0, leftWheelSpeed = 0.0;
	private double 		robotLength;
	
	private Timeline animation;
	private EventHandler<ActionEvent> eventHandler = e -> { this.animationLoop(); };
	
	public RobotGraphics() {
		// start the animation loop
		animation = new Timeline(new KeyFrame(Duration.millis(1/this.refreshRate * 1000), eventHandler));
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.stop();
	}
	
	// Start animation after setting the params (TODO: Safety check if the params are set)
	public void startAnimation() {
		animation.play();
	}
	
	
	private void animationLoop() {
		// compute the changes here
		this.paint();
	}
	
	
	private void paint() {
		if(this.shape == Shape.CENTERED_CIRCLE) {
			this.paintCenteredCircle();
		}
		else if(this.shape == Shape.CENTERED_SQUARED) {
			this.paintCenteredSquared();
		}
		else if(this.shape == Shape.REAR_WHEEL) {
			this.paintRearWheel();
		}
	}
	
	private void paintCenteredCircle() {
		
	}
	private void paintCenteredSquared() {
		
	}
	private void paintRearWheel() {
		
	}
	void setRefreshRate(double rate) {
		this.refreshRate = rate;
		animation = new Timeline(new KeyFrame(Duration.millis(1/this.refreshRate * 1000), eventHandler));
	}
	void setRobotShape(Shape shape) {
		this.shape = shape;
	}
	void setRobotLength(double length) {
		this.robotLength = length;
	}
	void setWheelsSeparation(double wheelsSeparation) {
		this.wheelsSeparation = wheelsSeparation;
	}
	void setWheelsRadius(double rightWheelRadius, double leftWheelRadius) {
		this.rightWheelRadius = rightWheelRadius;
		this.leftWheelRadius = leftWheelRadius;
	}
	void setWorkspaceDimensions(double workspaceWidth, double workspaceHeight) {
		this.workspaceDimensions[0] = workspaceWidth;
		this.workspaceDimensions[1] = workspaceHeight;
	}
	void setRobotPose(double x, double y) {
		this.xPose = x;
		this.yPose = y;
	}
	void setRobotOrientation(double theta) {
		this.orientation = theta;
	}
	void setWheelsSpeed(double rightWheelSpeed, double leftWheelSpeed) {
		this.rightWheelSpeed = rightWheelSpeed;
		this.leftWheelSpeed = leftWheelSpeed;
	}
	double getRightWheelRadius() { return this.rightWheelRadius; }
	double getLeftWheelRadius()  { return this.leftWheelRadius;  }
	double getRobotOrientation() { return this.orientation;		 }
	double getRightWheelSpeed()  { return this.rightWheelSpeed;  }
	double getLeftWheelSpeed()   { return this.leftWheelSpeed;   }
	double getWheelsSeparation() { return this.wheelsSeparation; }
	
	@Override
	public void setWidth(double width) {
		super.setWidth(width);
		this.paint();
	}
	@Override
	public void setHeight(double height) {
		super.setHeight(height);
		this.paint();
	}
}
