package application;

import javax.naming.CannotProceedException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class RobotGraphics extends Pane {
	private final double refreshRate = 60.0; // 1/DT (Hz)
	// Three shapes are supported
	public enum Shape {
		FRONT_WHEEL, CENTERED_SQUARED, CENTERED_CIRCLE, REAR_WHEEL
	}
	// Note: Units are SI (Radiant, Meter, Second)
	private Shape		shape = Shape.CENTERED_SQUARED;
	private double 		wheelsSeparation;
	private double 		rightWheelRadius, leftWheelRadius;
	private double 		workspaceWidth, workspaceHeight;
	private double 		xPose, yPose;
	private double 		xVel, yVel, angularVel;
	private double 		orientation = 0.0;
	private double 		rightWheelSpeed = 0.0, leftWheelSpeed = 0.0;
	private double		maxRightWheelSpeed = 5.0, maxLeftWheelSpeed = 5.0;
	private double 		robotLength, robotLength_, wheelSeparation_;
	// Note: Units are Pixels and Degrees
	private double		xPose_, yPose_;
	private double		orientation_;
	private double 		windowWidth_, windowHeight_;
	private boolean		showTrailer = true;
	
	// Some flags for optimization
	private boolean		isCollision = false;
	private boolean		wasRed = false;
	
	// array of the points to be traces behind the robot
	private double[] xPoints = new double[250];
	private double[] yPoints = new double[250];
	private int numberOfPoints = 0;
	
	// flags that must be set to start the animation
	boolean isRobotLengthSet, isXPoseSet, isYPoseSet, isWorkspaceWidthSet, isWorkspaceHeightSet,
			isWheelSeparationSet, isRightWheelRadiusSet, isLeftWheelRadiusSet;
	
	private Timeline animation;
	private EventHandler<ActionEvent> eventHandler = e -> { this.animationLoop(); };
	
	private Joystick joystick;
	
	public RobotGraphics() {
		// start the animation loop
		animation = new Timeline(new KeyFrame(Duration.millis(1/this.refreshRate * 1000), eventHandler));
		animation.setCycleCount(Timeline.INDEFINITE);
		this.setStyle("-fx-border-color: black; -fx-border-width: 2px;");
		for(int i=0; i<xPoints.length; i++) {
			xPoints[i] = 0.0;
			yPoints[i] = 0.0;
		}
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
		if(this.joystick != null) {
			new JoystickRobotLogic(this, this.joystick);
		}
		this.xVel = DFKEquation.computeXVel(this);	// m/s
		this.yVel = DFKEquation.computeYVel(this);	// m/s
		this.angularVel = DFKEquation.computeAngularVel(this);	// rad/s
		this.xPose += xVel/refreshRate;
		this.yPose += yVel/refreshRate;
		
		this.isCollision = false;
		
		if(this.xPose > this.workspaceWidth) {
			this.xPose = this.workspaceWidth;
			this.isCollision = true;
		}
		else if(this.xPose < 0) {
			this.xPose = 0.0;
			this.isCollision = true;
		}
		if(this.yPose > this.workspaceHeight) {
			this.yPose = this.workspaceHeight;
			this.isCollision = true;
		}
		else if(this.yPose < 0) {
			this.yPose = 0.0;
			this.isCollision = true;
		}
		
		this.orientation += this.angularVel/refreshRate;
		
		// Remap meters to pixels
		this.xPose_ = DimensionsMapper.metersToPixelsX(this, this.xPose);
		this.yPose_ = DimensionsMapper.metersToPixelsY(this, this.yPose);
		
		// Remap radians to degrees
		this.orientation_ = this.orientation * 180.0 / Math.PI;
		
		// Shift previous points to the right
        for (int i = xPoints.length - 1; i > 0; i--) {
        	xPoints[i] = xPoints[i - 1];
        	yPoints[i] = yPoints[i - 1];
        }
        // Insert new point
		this.xPoints[0] = this.xPose_;
		this.yPoints[0] = this.windowHeight_ - this.yPose_;
		
		if(numberOfPoints < this.xPoints.length) {
			numberOfPoints++;
		}
		
		this.getChildren().clear();
		this.paint();
	}
	
	
	private void paint() {
		// Draw border collision if needed
		if(this.isCollision) {
			if(!this.wasRed) {
				this.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
				this.wasRed = true;
			}
		}
		else if(this.wasRed) {
			this.setStyle("-fx-border-color: black; -fx-border-width: 2px;");
			this.wasRed = false;
		}
			
		// draw x/y lines for every meter
		for(int i = 1; i < (int)this.workspaceWidth; i++) {
			double section = windowWidth_/this.workspaceWidth;
			Line line = new Line(i*section, 0.0, i*section, windowHeight_);
			line.setStrokeWidth(0.15);
			this.getChildren().add(line);
		}
		for(int i = 1; i < (int)this.workspaceHeight; i++) {
			double section = windowHeight_/this.workspaceHeight;
			Line line = new Line(0.0, i*section, windowWidth_, i*section);
			line.setStrokeWidth(0.2);
			line.setStroke(Color.LIGHTSLATEGREY);
			this.getChildren().add(line);
		}
		
		// Draw the trailing line
		Polyline trailer = new Polyline();
		if(this.showTrailer) {
			trailer.setStrokeWidth(1);
			trailer.getStrokeDashArray().addAll(15.0, 15.0); // Dash 10 units, gap 5 units
			for (int i = 0; i < numberOfPoints; i++) {
				trailer.getPoints().addAll(xPoints[i], yPoints[i]);
	        }
			this.getChildren().add(trailer);
		}
			
		if(this.shape == Shape.CENTERED_CIRCLE) {
			if(this.showTrailer)
				trailer.setStroke(Color.PALEVIOLETRED);
			this.paintCenteredCircle();
		}
		else if(this.shape == Shape.CENTERED_SQUARED) {
			if(this.showTrailer)
				trailer.setStroke(Color.RED);
			this.paintCenteredSquared();
		}
		else if(this.shape == Shape.FRONT_WHEEL) {
			if(this.showTrailer)
				trailer.setStroke(Color.LIGHTSKYBLUE);
			this.paintFrontWheel();
		}
		else if(this.shape == Shape.REAR_WHEEL) {
			if(this.showTrailer)
				trailer.setStroke(Color.LIGHTCORAL);
			this.paintRearWheel();
		}
	}
	
	private void paintCenteredCircle() {
		Circle robotBody = new Circle();
		Rectangle rightWheel = new Rectangle();
		Rectangle leftWheel = new Rectangle();
		
		// Position the robot body
		robotBody.setRadius(robotLength_/2);
		robotBody.setCenterX(xPose_);
		robotBody.setCenterY(windowHeight_ - yPose_);
		robotBody.setFill(Color.PALEVIOLETRED);
		
		// Position the robot left wheel
		double leftWheelRadius_ = DimensionsMapper.metersToPixelsX(this, leftWheelRadius);
		double leftWheelX_ = xPose_ - (wheelSeparation_/2) * Math.cos(Math.PI/2 - orientation) - leftWheelRadius_;
		double leftWheelY_ = windowHeight_ - yPose_ - (wheelSeparation_/2) * Math.sin(Math.PI/2 - orientation) - leftWheelRadius_/2.6;
		leftWheel.setX(leftWheelX_);
		leftWheel.setY(leftWheelY_);
		leftWheel.setHeight(leftWheelRadius_/1.3);
		leftWheel.setWidth(leftWheelRadius_*2);
		leftWheel.setRotate(-orientation_);
		leftWheel.setFill(Color.BLACK);
		
		// Position the robot right wheel
		double rightWheelRadius_ = DimensionsMapper.metersToPixelsX(this, rightWheelRadius);
		double rightWheelX_ = xPose_ + (wheelSeparation_/2) * Math.cos(Math.PI/2 - orientation) - rightWheelRadius_;
		double rightWheelY_ = windowHeight_ - yPose_ + (wheelSeparation_/2) * Math.sin(Math.PI/2 - orientation) - rightWheelRadius_/2.6;
		rightWheel.setX(rightWheelX_);
		rightWheel.setY(rightWheelY_);
		rightWheel.setHeight(rightWheelRadius_/1.3);
		rightWheel.setWidth(rightWheelRadius_*2);
		rightWheel.setRotate(-orientation_);
		rightWheel.setFill(Color.BLACK);
		
		this.getChildren().addAll(robotBody, leftWheel, rightWheel);
		
	}
	private void paintCenteredSquared() {
		Rectangle robotBody = new Rectangle();
		Rectangle rightWheel = new Rectangle();
		Rectangle leftWheel = new Rectangle();
		
		// Position the robot body
		robotBody.setWidth(robotLength_);
		robotBody.setHeight(wheelSeparation_);
		robotBody.setX(xPose_ - robotLength_/2);
		robotBody.setY(this.getHeight() - yPose_ - wheelSeparation_/2);
		robotBody.setRotate(-orientation_);
		robotBody.setFill(Color.RED);
		
		// Position the robot left wheel
		double leftWheelRadius_ = DimensionsMapper.metersToPixelsX(this, leftWheelRadius);
		double leftWheelX_ = xPose_ - (wheelSeparation_/2) * Math.cos(Math.PI/2 - orientation) - leftWheelRadius_;
		double leftWheelY_ = windowHeight_ - yPose_ - (wheelSeparation_/2) * Math.sin(Math.PI/2 - orientation) - leftWheelRadius_/2.6;
		leftWheel.setX(leftWheelX_);
		leftWheel.setY(leftWheelY_);
		leftWheel.setHeight(leftWheelRadius_/1.3);
		leftWheel.setWidth(leftWheelRadius_*2);
		leftWheel.setRotate(-orientation_);
		leftWheel.setFill(Color.BLACK);
		
		// Position the robot right wheel
		double rightWheelRadius_ = DimensionsMapper.metersToPixelsX(this, rightWheelRadius);
		double rightWheelX_ = xPose_ + (wheelSeparation_/2) * Math.cos(Math.PI/2 - orientation) - rightWheelRadius_;
		double rightWheelY_ = windowHeight_ - yPose_ + (wheelSeparation_/2) * Math.sin(Math.PI/2 - orientation) - rightWheelRadius_/2.6;
		rightWheel.setX(rightWheelX_);
		rightWheel.setY(rightWheelY_);
		rightWheel.setHeight(rightWheelRadius_/1.3);
		rightWheel.setWidth(rightWheelRadius_*2);
		rightWheel.setRotate(-orientation_);
		rightWheel.setFill(Color.BLACK);
		
		this.getChildren().addAll(robotBody, leftWheel, rightWheel);
	}
	
	private void paintFrontWheel() {
		Rectangle robotBody = new Rectangle();
		Rectangle rightWheel = new Rectangle();
		Rectangle leftWheel = new Rectangle();
		Circle caster = new Circle();
		
		// Position the robot body
		robotBody.setWidth(robotLength_);
		robotBody.setHeight(wheelSeparation_);
		robotBody.setX(xPose_ - robotLength_);
		robotBody.setY(this.getHeight() - yPose_ - wheelSeparation_/2);
		robotBody.getTransforms().add(new Rotate(-orientation_, xPose_, windowHeight_ - yPose_));
		robotBody.setFill(Color.LIGHTSKYBLUE);
		
		// Position the robot left wheel
		double leftWheelRadius_ = DimensionsMapper.metersToPixelsX(this, leftWheelRadius);
		double leftWheelX_ = xPose_ - (wheelSeparation_/2) * Math.cos(Math.PI/2 - orientation) - leftWheelRadius_;
		double leftWheelY_ = this.getHeight() - yPose_ - (wheelSeparation_/2) * Math.sin(Math.PI/2 - orientation) - leftWheelRadius_/2.6;
		leftWheel.setX(leftWheelX_);
		leftWheel.setY(leftWheelY_);
		leftWheel.setHeight(leftWheelRadius_/1.3);
		leftWheel.setWidth(leftWheelRadius_*2);
		leftWheel.setRotate(-orientation_);
		leftWheel.setFill(Color.BLACK);
		
		// Position the robot right wheel
		double rightWheelRadius_ = DimensionsMapper.metersToPixelsX(this, rightWheelRadius);
		double rightWheelX_ = xPose_ + (wheelSeparation_/2) * Math.cos(Math.PI/2 - orientation) - rightWheelRadius_;
		double rightWheelY_ = windowHeight_ - yPose_ + (wheelSeparation_/2) * Math.sin(Math.PI/2 - orientation) - rightWheelRadius_/2.6;
		rightWheel.setX(rightWheelX_);
		rightWheel.setY(rightWheelY_);
		rightWheel.setHeight(rightWheelRadius_/1.3);
		rightWheel.setWidth(rightWheelRadius_*2);
		rightWheel.setRotate(-orientation_);
		rightWheel.setFill(Color.BLACK);
		
		// Position the caster wheel
		double casterDist_ = 0.75 * robotLength_;
		double casterX_ = xPose_ - Math.cos(orientation)*casterDist_;
		double casterY_ = windowHeight_ - yPose_ + Math.sin(orientation)*casterDist_;
		caster.setRadius(rightWheelRadius_/4.0);
		caster.setCenterX(casterX_);
		caster.setCenterY(casterY_);
		caster.setFill(Color.BLACK);
		
		this.getChildren().addAll(robotBody, leftWheel, rightWheel, caster);
	}
	
	private void paintRearWheel() {
		Rectangle robotBody = new Rectangle();
		Rectangle rightWheel = new Rectangle();
		Rectangle leftWheel = new Rectangle();
		Circle caster = new Circle();
		
		// Position the robot body
		robotBody.setWidth(robotLength_);
		robotBody.setHeight(wheelSeparation_);
		robotBody.setX(xPose_);
		robotBody.setY(windowHeight_ - yPose_ - wheelSeparation_/2);
		robotBody.getTransforms().add(new Rotate(-orientation_, xPose_, windowHeight_ - yPose_));
		robotBody.setFill(Color.LIGHTCORAL);
		
		// Position the robot left wheel
		double leftWheelRadius_ = DimensionsMapper.metersToPixelsX(this, leftWheelRadius);
		double leftWheelX_ = xPose_ - (wheelSeparation_/2) * Math.cos(Math.PI/2 - orientation) - leftWheelRadius_;
		double leftWheelY_ = windowHeight_ - yPose_ - (wheelSeparation_/2) * Math.sin(Math.PI/2 - orientation) - leftWheelRadius_/2.6;
		leftWheel.setX(leftWheelX_);
		leftWheel.setY(leftWheelY_);
		leftWheel.setHeight(leftWheelRadius_/1.3);
		leftWheel.setWidth(leftWheelRadius_*2);
		leftWheel.setRotate(-orientation_);
		leftWheel.setFill(Color.BLACK);
		
		// Position the robot right wheel
		double rightWheelRadius_ = DimensionsMapper.metersToPixelsX(this, rightWheelRadius);
		double rightWheelX_ = xPose_ + (wheelSeparation_/2) * Math.cos(Math.PI/2 - orientation) - rightWheelRadius_;
		double rightWheelY_ = windowHeight_ - yPose_ + (wheelSeparation_/2) * Math.sin(Math.PI/2 - orientation) - rightWheelRadius_/2.6;
		rightWheel.setX(rightWheelX_);
		rightWheel.setY(rightWheelY_);
		rightWheel.setHeight(rightWheelRadius_/1.3);
		rightWheel.setWidth(rightWheelRadius_*2);
		rightWheel.setRotate(-orientation_);
		rightWheel.setFill(Color.BLACK);
		
		// Position the caster wheel
		double casterDist_ = 0.75 * robotLength_;
		double casterX_ = xPose_ + Math.cos(orientation)*casterDist_;
		double casterY_ = windowHeight_ - yPose_ - Math.sin(orientation)*casterDist_;
		caster.setRadius(rightWheelRadius_/4.0);
		caster.setCenterX(casterX_);
		caster.setCenterY(casterY_);
		caster.setFill(Color.BLACK);
		
		this.getChildren().addAll(robotBody, leftWheel, rightWheel, caster);
	}
	
	public void connectJoystick(Joystick joystick) {
		this.joystick = joystick;
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
	void showTrailer(boolean show) {
		this.showTrailer = show;
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
	
	double getMaxRightWheelSpeed() { return this.maxRightWheelSpeed; }
	double getMaxLeftWheelSpeed() { return this.maxLeftWheelSpeed; }
	
	@Override
	public void setWidth(double width) {
		super.setWidth(width);
		robotLength_ = DimensionsMapper.metersToPixelsX(this, this.robotLength);
		wheelSeparation_ = DimensionsMapper.metersToPixelsY(this, this.wheelsSeparation);
		for(int i=0; i<xPoints.length; i++) {
			xPoints[i] = 0.0;
			yPoints[i] = 0.0;
		}
		numberOfPoints = 0;
		windowWidth_ = this.getWidth();
		this.paint();
	}
	@Override
	public void setHeight(double height) {
		super.setHeight(height);
		robotLength_ = DimensionsMapper.metersToPixelsX(this, this.robotLength);
		wheelSeparation_ = DimensionsMapper.metersToPixelsY(this, this.wheelsSeparation);
		for(int i=0; i<xPoints.length; i++) {
			xPoints[i] = 0.0;
			yPoints[i] = 0.0;
		}
		numberOfPoints = 0;
		windowHeight_ = this.getHeight();
		this.paint();
	}
}
