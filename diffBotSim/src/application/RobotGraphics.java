package application;

import javax.naming.CannotProceedException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class RobotGraphics extends Pane {
	// Three shapes are supported
	public enum Shape {
		REAR_WHEEL, CENTERED_SQUARED, CENTERED_CIRCLE
	}
	// Note: Units are SI (Radiant, Meter, Second)
	private Shape		shape = Shape.CENTERED_SQUARED;
	private double 		wheelsSeparation;
	private double 		rightWheelRadius, leftWheelRadius;
	private double 		workspaceWidth, workspaceHeight;
	private double 		xPose, yPose;
	private double 		xVel, yVel, angularVel;
	private double 		orientation = 0.0;
	private double 		refreshRate = 50.0; // 1/DT (Hz)
	private double 		rightWheelSpeed = 0.0, leftWheelSpeed = 0.0;
	private double		maxRightWheelSpeed = 5.0, maxLeftWheelSpeed = 5.0;
	private double 		robotLength;
	
	// Note: Units are Pixels and Degrees
	private double		xPose_, yPose_;
	private double		orientation_;
	
	// flags that must be set to start the animation
	boolean isRobotLengthSet, isXPoseSet, isYPoseSet, isWorkspaceWidthSet, isWorkspaceHeightSet,
			isWheelSeparationSet, isRightWheelRadiusSet, isLeftWheelRadiusSet;
	
	private Timeline animation;
	private EventHandler<ActionEvent> eventHandler = e -> { this.animationLoop(); };
	
	public RobotGraphics() {
		// start the animation loop
		animation = new Timeline(new KeyFrame(Duration.millis(1/this.refreshRate * 1000), eventHandler));
		animation.setCycleCount(Timeline.INDEFINITE);
	}
	
	public void startAnimation() throws CannotProceedException {
		if(isRobotLengthSet&&isXPoseSet&&isYPoseSet&&isWorkspaceWidthSet&&isWorkspaceHeightSet)
			if(isWheelSeparationSet&&isRightWheelRadiusSet&&isLeftWheelRadiusSet) {
				animation.play();
				return;
			}
		throw new CannotProceedException("Not all required parameters are set. Make sure the following"
				+ "parameters are correctly set:"
				+ "* Robot Length\n"
				+ "* Robot x & y Pose\n"
				+ "* Workspace width\n"
				+ "* Workspace height\n"
				+ "* Wheels separation\n"
				+ "* Right wheel radius\n"
				+ "* Left wheel radius");
	}
	
	private void animationLoop() {
		this.xVel = DFKEquation.computeXVel(this);	// m/s
		this.yVel = DFKEquation.computeYVel(this);	// m/s
		this.angularVel = DFKEquation.computeAngularVel(this);	// rad/s
		this.xPose += xVel/refreshRate;
		this.yPose += yVel/refreshRate;
		this.orientation += this.angularVel/refreshRate;
		
		// remap meters to pixels
		this.xPose_ = DimensionsMapper.metersToPixelsX(this, this.xPose);
		this.yPose_ = DimensionsMapper.metersToPixelsY(this, this.yPose);
		// remap radians to degrees
		this.orientation_ = this.orientation * 180.0 / Math.PI;
		
		this.getChildren().clear();
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
		Circle robotBody = new Circle();
		Rectangle rightWheel = new Rectangle();
		Rectangle leftWheel = new Rectangle();
	}
	private void paintCenteredSquared() {
		Rectangle robotBody = new Rectangle();
		Rectangle rightWheel = new Rectangle();
		Rectangle leftWheel = new Rectangle();
		
		double robotLength_ = DimensionsMapper.metersToPixelsX(this, this.robotLength);
		double wheelsSeparation_ = DimensionsMapper.metersToPixelsY(this, this.wheelsSeparation);
		
		// Position the robot body
		robotBody.setWidth(robotLength_);
		robotBody.setHeight(wheelsSeparation_);
		robotBody.setX(this.xPose_ - robotLength_/2);
		robotBody.setY(this.getHeight() - this.yPose_ - wheelsSeparation_/2);
		robotBody.setRotate(-this.orientation_);
		robotBody.setFill(Color.RED);
		
		// Position the robot left wheel
		double leftWheelRadius_ = DimensionsMapper.metersToPixelsX(this, this.leftWheelRadius);
		double leftWheelX_ = this.xPose_ - (wheelsSeparation_/2) * Math.cos(Math.PI/2 - this.orientation) - leftWheelRadius_;
		double leftWheelY_ = this.getHeight() - this.yPose_ - (wheelsSeparation_/2) * Math.sin(Math.PI/2 - this.orientation) - leftWheelRadius_/2.6;
		leftWheel.setX(leftWheelX_);
		leftWheel.setY(leftWheelY_);
		leftWheel.setHeight(leftWheelRadius_/1.3);
		leftWheel.setWidth(leftWheelRadius_*2);
		leftWheel.setRotate(-this.orientation_);
		leftWheel.setFill(Color.BLACK);
		
		// Position the robot left wheel
		double rightWheelRadius_ = DimensionsMapper.metersToPixelsX(this, this.rightWheelRadius);
		double rightWheelX_ = this.xPose_ + (wheelsSeparation_/2) * Math.cos(Math.PI/2 - this.orientation) - rightWheelRadius_;
		double rightWheelY_ = this.getHeight() - this.yPose_ + (wheelsSeparation_/2) * Math.sin(Math.PI/2 - this.orientation) - rightWheelRadius_/2.6;
		rightWheel.setX(rightWheelX_);
		rightWheel.setY(rightWheelY_);
		rightWheel.setHeight(rightWheelRadius_/1.3);
		rightWheel.setWidth(rightWheelRadius_*2);
		rightWheel.setRotate(-this.orientation_);
		rightWheel.setFill(Color.BLACK);
		
		this.getChildren().addAll(robotBody, leftWheel, rightWheel);
		
		
	}
	private void paintRearWheel() {
		Rectangle robotBody = new Rectangle();
		Rectangle rightWheel = new Rectangle();
		Rectangle leftWheel = new Rectangle();
	}
	void setRefreshRate(double rate) {
		if(rate < 1.0) {
			throw new IllegalArgumentException("Animation update rate must be > 1");
		}
		this.refreshRate = rate;
		animation = new Timeline(new KeyFrame(Duration.millis(1/this.refreshRate * 1000), eventHandler));
	}
	void setRobotShape(Shape shape) {
		this.shape = shape;
		this.paint();
	}
	void setRobotLength(double length) {
		if(length < 0.01) {
			throw new IllegalArgumentException("Length must be > 0.01 m");
		}
		this.robotLength = length;
		this.isRobotLengthSet = true;
	}
	void setWheelsSeparation(double wheelsSeparation) {
		if(wheelsSeparation < 0.01) {
			throw new IllegalArgumentException("Wheel separation must be > 0.01 m");
		}
		this.wheelsSeparation = wheelsSeparation;
		this.isWheelSeparationSet = true;
	}
	void setWheelsRadius(double leftWheelRadius, double	rightWheelRadius) {
		if(rightWheelRadius < 0.01 || leftWheelRadius < 0.01) {
			throw new IllegalArgumentException("Wheel radius must be > 0.01 m");
		}
		this.rightWheelRadius = rightWheelRadius;
		this.leftWheelRadius = leftWheelRadius;
		this.isRightWheelRadiusSet = true;
		this.isLeftWheelRadiusSet = true;
	}
	void setWorkspaceDimensions(double workspaceWidth, double workspaceHeight) {
		if(workspaceWidth < 0.1 || workspaceHeight < 0.1) {
			throw new IllegalArgumentException("Workspace dimensions are too small");
		}
		this.workspaceWidth = workspaceWidth;
		this.workspaceHeight = workspaceHeight;
		this.isWorkspaceHeightSet = true;
		this.isWorkspaceWidthSet = true;
	}
	void setRobotPose(double x, double y) {
		this.xPose = x;
		this.yPose = y;
		this.isXPoseSet = true;
		this.isYPoseSet = true;
	}
	void setRobotOrientation(double theta) {
		this.orientation = theta;
	}
	void setWheelsSpeed(double leftWheelSpeed, double rightWheelSpeed) {
		if(Math.abs(rightWheelSpeed) > this.maxRightWheelSpeed || Math.abs(leftWheelSpeed) > this.maxLeftWheelSpeed) {
			throw new IllegalArgumentException("Wheel speed limit violated. Wheel speed not changed");
		}
		this.rightWheelSpeed = rightWheelSpeed;
		this.leftWheelSpeed = leftWheelSpeed;
	}
	void setMaxWheelsSpeed(double maxLeft, double maxRight) {
		this.maxLeftWheelSpeed = maxLeft;
		this.maxRightWheelSpeed = maxRight;
	}
	
	double getRightWheelRadius() { return this.rightWheelRadius; }
	double getLeftWheelRadius()  { return this.leftWheelRadius;  }
	double getRobotOrientation() { return this.orientation;		 }
	double getRobotXPose()		 { return this.xPose;			 }
	double getRightWheelSpeed()  { return this.rightWheelSpeed;  }
	double getLeftWheelSpeed()   { return this.leftWheelSpeed;   }
	double getWheelsSeparation() { return this.wheelsSeparation; }
	double getWorkspaceWidth()	 { return this.workspaceWidth;	 }
	double getWorkspaceHeight()	 { return this.workspaceHeight;	 }
	
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
