package de.robinfrederik.drunkracing.mvp;

// since this is our first idea of the car (mvp) and was later replaced by "carvisuel", we won't explain the code here, because it is mostly similar to the better version "carvisuel"
// so basically this is unsued in the code but we left it for nostalgia reasons (and so can see it xD)

import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;


import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.car.CarGoKartSport;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;

public class CarVisualTest extends Group {

    private ImageView carImage;

    private Car carPhysics;

    //  Force arrows
    private Line frontForceArrow; // front axle, horizontal
    private Line rearForceArrow;  // rear axle, horizontal
    private Line midForceArrow;   // middle of car, horizontal
    private Line speedArrow;      // center, vertical

    //  Force values
    public double frontLongForce = 0;  // -2000 to 2000 (side)
    public double frontLatForce = 0;
    public double rearForce  = 0;  // -2000 to 2000 (side)
    public double midForce   = 0;  // -2000 to 2000 (side)
    //  Movement
    private double speed = 0;
    private final double maxSpeed = 5;
    private final double acceleration = 0.1;
    private final double deceleration = 0.05;
    private double dx = 0, dy = 0;
    private double lastDx = 0, lastDy = 0;

    //Show/hide speed arrow
    private boolean showSpeedArrow = true;

    public CarVisualTest() {
        // Load car image
        Image carImg = new Image(getClass().getResource("/images/car.png").toExternalForm());
        carImage = new ImageView(carImg);
        carImage.setFitWidth(50);
        carImage.setPreserveRatio(true);
        carImage.setTranslateX(-25);
        carImage.setTranslateY(-50);
        carPhysics = new CarGoKartSport();

        // Create arrows
        frontForceArrow = new Line(0, -40, 0, -40);
        frontForceArrow.setStroke(Color.GREEN);
        frontForceArrow.setStrokeWidth(3);

        rearForceArrow = new Line(0, 40, 0, 40);
        rearForceArrow.setStroke(Color.GREEN);
        rearForceArrow.setStrokeWidth(3);

        midForceArrow = new Line(0, 0, 0, 0); // NEW: middle force arrow
        midForceArrow.setStroke(Color.PURPLE); // example color
        midForceArrow.setStrokeWidth(3);

        speedArrow = new Line(0, 0, 0, 0);
        speedArrow.setStroke(Color.BLUE);
        speedArrow.setStrokeWidth(2);

        this.getChildren().addAll(carImage, frontForceArrow, rearForceArrow, midForceArrow, speedArrow);
    }

    // movement
    public void update(boolean up, boolean down, boolean left, boolean right) {
        dx = 0; dy = 0;

        if (up) dy -= 1;
        if (down) dy += 1;
        if (left) dx -= 1;
        if (right) dx += 1;

        double length = Math.sqrt(dx * dx + dy * dy);
        if (length != 0) {
            dx /= length;
            dy /= length;
            lastDx = dx;
            lastDy = dy;
        }

        if (up && !down) {
            if (speed < maxSpeed) speed += acceleration;
        } else if (down && !up) {
            if (speed > -maxSpeed) speed -= acceleration;
        } else {
            if (speed > 0) {
                speed -= deceleration;
                if (speed < 0) speed = 0;
            } else if (speed < 0) {
                speed += deceleration;
                if (speed > 0) speed = 0;
            }
        }
    }

    public double getMoveDx() { return lastDx; }
    public double getMoveDy() { return lastDy; }
    public double getSpeed()  { return speed; }

    // Update all force arrows
    public void updateForceArrows() {
        double scaleForce = 0.02; // scale for side forces
        double scaleSpeed = 10;   // scale for speed

        // Front force: horizontal
        double fLen = frontLatForce * scaleForce;
        frontForceArrow.setEndX(frontForceArrow.getStartX() + fLen);
        frontForceArrow.setEndY(frontForceArrow.getStartY());

        // Rear force: horizontal
        double rLen = rearForce * scaleForce;
        rearForceArrow.setEndX(rearForceArrow.getStartX() + rLen);
        rearForceArrow.setEndY(rearForceArrow.getStartY());

        // Mid force: horizontal at center
        double mLen = midForce * scaleForce;
        midForceArrow.setEndX(midForceArrow.getStartX() + mLen);
        midForceArrow.setEndY(midForceArrow.getStartY());

        // Speed arrow: vertical
        if (showSpeedArrow) {
            double sLen = speed * scaleSpeed;
            speedArrow.setEndY(speedArrow.getStartY() - sLen);
            speedArrow.setEndX(speedArrow.getStartX());
        } else {
            speedArrow.setEndY(speedArrow.getStartY());
            speedArrow.setEndX(speedArrow.getStartX());
        }
    }

    public void reset() {
        speed = 0;
        frontLatForce = 0;
        rearForce = 0;
        midForce = 0;
        updateForceArrows();
    }
}