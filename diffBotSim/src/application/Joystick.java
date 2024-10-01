package application;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Joystick extends Pane{
	private double baseRadius;
	private double handleRadius;
	private double width;
	private double height;
	
	private double handleX = 0, handleY = 0;
	
	private Circle handle;
	private Circle base;
	private Label label;
	
	private EventHandler<MouseEvent> mouseDragged = e -> {this.mouseDragHandler(e);};
	private EventHandler<MouseEvent> mouseReleased = e -> {this.mouseReleaseHandler(e);};
	
	public Joystick() {
		this(250, 250);
	}
	
	private Joystick(double width, double height) {
		this.height = height;
		this.width = width;
		this.setPrefSize(width, height);
		this.baseRadius = (width>height?height/3:width/3);
		this.handleRadius = (width>height?height/15:width/15);
		
		this.base = new Circle(width/2, height/2, baseRadius);
		base.setStroke(Color.BLACK);
		base.setFill(null);
		this.handle = new Circle(width/2, height/2, handleRadius);
		handle.setFill(Color.NAVY);
		
		Line horLine = new Line(width/2, 0, width/2, height);
		Line vertLine = new Line(0, height/2, width, height/2);
		
		label = new Label("0.0, 0.0");
		label.setLayoutX(15);
		label.setLayoutY(10);
		label.setScaleX(1.13);
		label.setScaleY(1.13);
		 
	    this.getChildren().addAll(label, base, handle, horLine, vertLine);
	     
	    handle.setOnMouseDragged(mouseDragged);
	    handle.setOnMouseReleased(mouseReleased);
	}
	
	private void mouseDragHandler(MouseEvent event) {
		double mouseX = event.getX();
        double mouseY = event.getY();
        double deltaX = mouseX - width/2;
        double deltaY = mouseY - height/2;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance <= baseRadius) {
        	handle.setCenterX(mouseX);
        	handle.setCenterY(mouseY);
		} else {
		    double angle = Math.atan2(deltaY, deltaX);
		    handle.setCenterX(width/2 + baseRadius * Math.cos(angle));
		    handle.setCenterY(height/2 + baseRadius * Math.sin(angle));
		}
        
		// Print the x and y values relative to the center (150, 150)
		this.handleX = (handle.getCenterX() - width/2) / baseRadius;
		this.handleY = -(handle.getCenterY() - height/2) / baseRadius;
		String coordinates = String.format("%.2f, %.2f", this.handleX, this.handleY);

		this.label.setText(coordinates);
	}
	
	private void mouseReleaseHandler(MouseEvent event) {
		handle.setCenterX(width/2);
	    handle.setCenterY(height/2);
	    this.handleX = 0.0;
	    this.handleY = 0.0;
	}
	
	public double getHandleX() {
		return this.handleX;
	}
	public double getHandleY() {
		return this.handleY;
	}
}
