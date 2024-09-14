# Introduction
Imagine you have a joystick and a differential drive robot. How would you use the x and y coordinates of the joystick to control the speed of both motors? There are many ways to approach this problem, some good and some not so effective. Finding the simplest, most intuitive, and maneuverable solution isn't an easy task.

At my company, a team of four of us tackled this exact challenge. After much deliberation, we came up with 2-3 different solutions. However, testing each approach on the physical robot proved time-consuming, as each iteration required flashing firmware, updating software, connecting hardware, and more.

**DiffBot-Sim** is a personal effort to streamline this process. It provides a joystick GUI, a customizable simulated differential drive robot, and a single function where you can implement your desired control logic. In just 5-7 lines of code, you can start testing different strategies immediately.

Here is a screeshot of the result:

![alt text](image.png)

# Features
DiffBot-Sim is not just a visualizer, it is an accurate simulator that solves the __differential forward kinematics equation__ of your particular robot. The simulator takes into consideration the following parameters:
* Robot length
* Initial robot position in the map
* Right/Left wheels radius
* Wheels separation
* Max wheels speed
* Width/Height of the environment the robot is operating in
* Initial orientation of the robot

# Documentation
## Overview
The DiffBot-Sim project provides a simple way to simulate a differential drive robot controlled by a joystick. The simulation includes a graphical interface for both the robot's movement and the joystick controls. The core class handles the robot's configuration and animation, allowing you to easily visualize how joystick input affects the robot's behavior.
## Quick Start
Below is a basic example of how to set up and run the differential drive robot simulation:
### The Main function (Configuration)
``` java
    public void start(Stage primaryStage) throws CannotProceedException {
    
    // Initialize the robot and joystick
    RobotGraphics robot = new RobotGraphics();
    Joystick joystick = new Joystick(250, 250); // Width and height of the joystick

    // Units are in SI (meters, radians, seconds)

    // Set the initial configuration of the robot
    robot.setRobotShape(RobotGraphics.Shape.CENTERED_CIRCLE); // Shape of the robot
    robot.setRobotLength(2.6); // Length of the robot in meters
    robot.setWheelsSeparation(1.9); // Distance between the wheels (in meters)
    robot.setWheelsRadius(0.6, 0.6); // Radius of the left and right wheels (in meters)
    robot.setWorkspaceDimensions(32, 16); // Size of the workspace (width x height in meters)
    robot.setRobotPose(8, 0.9); // Initial position (x, y in meters)
    robot.setRobotOrientation(Math.PI / 2); // Initial orientation (in radians)
    robot.setMaxWheelsSpeed(9.0, 9.0); // Max speed for the left and right wheels (in m/s)

    // Connect the joystick to control the robot
    robot.connectJoystick(joystick);

    // Start the animation of the robot
    robot.startAnimation();

    // Create the main simulation scene
    Scene scene = new Scene(robot, 800, 400);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Differential Robot Simulator");
    primaryStage.show();

    // Create a separate window for the joystick controller
    Stage joystickStage = new Stage();
    Scene joystickScene = new Scene(joystick);
    joystickStage.setScene(joystickScene);
    joystickStage.setResizable(false);
    joystickStage.setTitle("Controller");
    joystickStage.show();
}
```
### The Control function (Control logic)
``` java
public class JoystickRobotLogic {

    public JoystickRobotLogic(RobotGraphics robot, Joystick joystick) {
        double joystickX = joystick.getHandleX();  // X-axis position of the joystick
        double joystickY = joystick.getHandleY();  // Y-axis position of the joystick
        double maxRightVel = robot.getMaxRightWheelSpeed();  // Max speed for the right wheel
        double maxLeftVel = robot.getMaxLeftWheelSpeed();  // Max speed for the left wheel
        double rightWheelVel = 0.0;  // Initial right wheel velocity
        double leftWheelVel = 0.0;   // Initial left wheel velocity

        // Logic linking joystick position to wheel velocities
        leftWheelVel = (joystickY * maxLeftVel) + (joystickX * maxLeftVel);
        rightWheelVel = (joystickY * maxRightVel) - (joystickX * maxRightVel);

        // Normalize wheel speeds if they exceed the maximum allowed values
        double maxWheelVel = Math.max(Math.abs(leftWheelVel), Math.abs(rightWheelVel));
        if (maxWheelVel > maxLeftVel || maxWheelVel > maxRightVel) {
            leftWheelVel = (leftWheelVel / maxWheelVel) * maxLeftVel;
            rightWheelVel = (rightWheelVel / maxWheelVel) * maxRightVel;
        }

        // Set the calculated wheel velocities on the robot
        robot.setWheelsSpeed(leftWheelVel, rightWheelVel);
    }
}
```


## Key Components
### RobotGraphics
This class manages the graphical representation of the robot, including its size, shape, and motion. It also handles the connection to the joystick for real-time control of the robot.

* `setRobotShape(Shape shape)`: Defines the shape of the robot.
* `setRobotLength(double length)`: Sets the length of the robot (in meters).
* `setWheelsSeparation(double separation)`: Sets the distance between the robot's wheels.
* `setWheelsRadius(double leftRadius, double rightRadius)`: Sets the radius of the left and right wheels.
* `setWorkspaceDimensions(double width, double height)`: Defines the size of the workspace (in meters).
* `setRobotPose(double x, double y)`: Sets the initial position of the robot (in meters).
* `setRobotOrientation(double orientation)`: Sets the initial orientation of the robot (in radians).
* `setMaxWheelsSpeed(double leftMaxSpeed, double rightMaxSpeed)`: Configures the maximum speed for each wheel (in meters per second).
* `connectJoystick(Joystick joystick)`: Connects a joystick to control the robot.
* `startAnimation()`: Begins the animation that represents the robot's movement after completing the required configuration.

### Joystick
This class creates a graphical joystick control interface.
* **Constructor**: `Joystick(double width, double height)` creates a joystick with the specified dimensions (in pixels).

### JoystickRobotLogic
This class is responsible for translating the joystick's x and y coordinates into velocities for the robot's left and right wheels. This is where the user's logic is implemented.
This is how the current implementation works (Change it as needed)

* `joystick.getHandleX()` and `joystick.getHandleY()` retrieve the joystick's current position.
* The velocities of the wheels are calculated based on these joystick inputs and the maximum allowable speeds for each wheel.
* The code ensures that the wheel velocities are normalized if they exceed the maximum values.

### Customizing the Logic
You can modify the logic inside `JoystickRobotLogic` to suit your needs. The current implementation links the joystick’s y-axis to forward/backward movement and the x-axis to rotational movement. The logic is designed to normalize the velocities if the joystick values would push the robot's motors beyond their maximum allowed speed.

## Running the Simulation
1. Set up the robot in the `Main()` method.
2. Implement your custom logic in `JoystickRobotLogic`.
3. Run the simulation and observe how your logic affects the robot’s movement.

## Robot Shapes
The simualator supports four different types of differential drive robot, selected via the `setRobotShape(Shape shape)` method.
1. `RobotGraphics.Shape.CENTERED_CIRCLE`: A circular robot body with centrally positioned differential wheels. In this case, the 'robot length' refers to the diameter of the circular body.

![alt text](image-1.png)

2. `RobotGraphics.Shape.CENTERED_SQUARED`: A rectangular robot with centrally positioned differential wheels. In this case, the robot's length refers to its actual length (the longer side in the screenshot), while the wheel separation distance corresponds to the robot's body width (the shorter side in the screenshot).

![alt text](image-2.png)

3. `RobotGraphics.Shape.FRONT_WHEEL`: A rectangular robot featuring differential drive wheels at the front and a caster wheel at the rear.

![alt text](image-3.png)

4. `RobotGraphics.Shape.REAR_WHEEL`: A rectangular robot featuring differential drive wheels at the rear and a caster wheel at the front.

![alt text](image-4.png)

