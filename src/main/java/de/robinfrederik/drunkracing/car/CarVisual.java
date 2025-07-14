package de.robinfrederik.drunkracing.car;

// here we import the physics part for the car and some JavaFX libaries as we did in main. Since they are the same we won't explain their function again
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line; // (except for this one) JavaFX shape for force arrows

public class CarVisual extends Group { // CarVisual class extends JavaFX Group to visually represent the car (self-explaining with the class name)

    private ImageView carImage; // Image view of the car

    private CarGoKartSport carPhysics;

    //just in case to prevent thread problems
    private volatile byte accelInput;
    private volatile byte steeringInput;

    // Force arrows (not imortant for the main game, as mentioned just for test purposes)
    private Line frontForceArrow; // front axle, horizontal
    private Line rearForceArrow;  // rear axle, horizontal
    private Line midForceArrow;   // middle of car, horizontal
    private Line longForceArrow;      // center, vertical

    // Show/hide longForceArrow
    private boolean showLongForceArrow = false; // we set it on false, because we only needed it for test purposes

    public CarVisual() {
        Image carImg = new Image(getClass().getResource("/images/car.png").toExternalForm()); // Load car image
        this.carImage = new ImageView(carImg); // Create ImageView
        this.carImage.setFitHeight(60); // Set height
        this.carImage.setPreserveRatio(true); // Keep width/height ratio
        this.carImage.setTranslateX(-150.6 / 2); // Center X
        this.carImage.setTranslateY(-60 / 2); // Center Y

        this.carPhysics = new CarGoKartSport(); // Create new car physics object
        this.accelInput = 0; // Initial acceleration
        this.steeringInput = 0; // Initial steering

        // In this section we created force arrows to test our physics on the car, but they are not needed in the actual game
        //this.frontForceArrow = new Line(0, -40, 0, -40);
        //this.frontForceArrow.setStroke(Color.GREEN);
        //this.frontForceArrow.setStrokeWidth(3);

        //this.rearForceArrow = new Line(0, 40, 0, 40);
        //this.rearForceArrow.setStroke(Color.GREEN);
        //this.rearForceArrow.setStrokeWidth(3);

        //this.midForceArrow = new Line(0, 0, 0, 0); // NEW: middle force arrow
        //this.midForceArrow.setStroke(Color.PURPLE); // example color
        //this.midForceArrow.setStrokeWidth(3);

        //this.longForceArrow= new Line(0, 0, 0, 0);
        //this.longForceArrow.setStroke(Color.BLUE);
        //this.longForceArrow.setStrokeWidth(2);

        this.getChildren().addAll(carImage); // arrow got deleted, if you want them back then implement the following in the part of the code: , frontForceArrow, rearForceArrow, midForceArrow, longForceArrow
    }

    // Updates control input based on key states (so basically movement)
    public void update(boolean up, boolean down, boolean left, boolean right) {
        if (up && !down) {
            this.accelInput = 1; // Forward
        }
        else if (down && !up) {
            this.accelInput = -1; // Reverse
        }
        else {
            this.accelInput = 0; // No vertical input
        }

        if (left && !right) {
            this.steeringInput = 1; // Turn left
        }
        else if (right && !left) {
            this.steeringInput = -1; // Turn right
        }
        else {
            this.steeringInput = 0; // No horizontal input
        }
    }

    public Car getCar() { return this.carPhysics; } // Getter for physics car
    public double getX() { return carPhysics.getState().getXCoM(); } // Get car's X position
    public void setX(double x) { this.carPhysics.getState().setXCoM(x); } // Set car's X position

    public double getY() { return carPhysics.getState().getYCoM(); } // Get car's Y position
    public void setY(double y) { this.carPhysics.getState().setYCoM(y); } // Set car's Y position

    public double getSpeed()  { return this.carPhysics.getState().getLongVel(); } // Get current speed
    public byte getAccelInput() { return this.accelInput; } // Return acceleration input
    public byte getSteeringInput() { return this.steeringInput; } // Return steering input

    // Update all force arrows (as mentioned unused in game)
    public void updateForceArrows() {
        AckermannState state = this.carPhysics.getState(); // Get physical state
        double scaleForce = 0.02; // scale for side forces

        // Front force: horizontal
        double fLen = state.getLatForceFront() * scaleForce;
        frontForceArrow.setEndX(frontForceArrow.getStartX() + fLen);
        frontForceArrow.setEndY(frontForceArrow.getStartY());

        // Rear force: horizontal
        double rLen = state.getLatForceBack() * scaleForce;
        rearForceArrow.setEndX(rearForceArrow.getStartX() + rLen);
        rearForceArrow.setEndY(rearForceArrow.getStartY());

        // Mid force: horizontal at center
        double mLen = 0 * scaleForce;
        midForceArrow.setEndX(midForceArrow.getStartX() + mLen);
        midForceArrow.setEndY(midForceArrow.getStartY());

        // Speed arrow: vertical
        if (showLongForceArrow) {
            double sLen = (state.getLongForceBack() + state.getLongForceFront())* scaleForce;
            longForceArrow.setEndY(longForceArrow.getStartY() - sLen);
            longForceArrow.setEndX(longForceArrow.getStartX());
        } else {
            longForceArrow.setEndY(longForceArrow.getStartY());
            longForceArrow.setEndX(longForceArrow.getStartX());
        }
    }

    // Updates rotation of the car based on its yaw
    public void updateVisuals() {
        AckermannState state = this.carPhysics.getState();
        double yawDeg = Math.toDegrees(state.getYaw()); // Yaw in degree
        this.carImage.setRotate(yawDeg); // Turns car by degree
    }

    public void reset() {
        this.carPhysics.reset(); // Reset physical state
        this.updateForceArrows(); // Update arrows (not used in game)
        this.accelInput = 0; // Reset acceleration
        this.steeringInput = 0; // Reset steering

        updateForceArrows(); // irrelevant for main game
        updateVisuals(); // Reset visuals

    }
}