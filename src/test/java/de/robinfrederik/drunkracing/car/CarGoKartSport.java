package de.robinfrederik.drunkracing.car;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannModel;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;

public class CarGoKartSport implements Car{
    private final double mass = 150;         // mass of car
    private final double distFront = 0.5;  // distance of Center of Mass (CoM) to front axle
    private final double distBack = 0.55;   // distance of CoM to back axle
    private final double heightCoM = 0.1;   // height of the CoM from the ground
    private final double resRot = 20;      // resistance to rotating (20 - 2000) //156.8 is estimated geometrically
    private final double deltaMax = Math.toRadians(25);    // max steering Angle in radians
    private final double corStifFront = 5000; // front cornering stiffness (5000 - 250000), if CorStifFront > CorStifBack : oversteering
    private final double corStifBack = 5000;  // back cornering stiffness (5000 - 250000), if CorStifFront < CorStifBack : understeering
    private final double power = 13000;        // engine Power (20 000 - 1000 000) W so * 1000
    private final double engineForce = 5000;  // Force of the engine of the car in N, = maxAcceleration * mass
    private final double brakeForce = 5000;   // Force of the brakes of the car in N, = maxDeceleration * mass >= engineForce
    private final double staticMaxLoadFront = 9.81 * mass * distBack / (distFront + distBack);
    private final double staticMaxLoadBack = 9.81 * mass * distFront / (distFront + distBack);

    private AckermannState state = new AckermannState();

    @Override
    public double getMass () {
        return mass;
    }

    @Override
    public double getDistFront() {
        return distFront;
    }

    @Override
    public double getDistBack() {
        return distBack;
    }

    @Override
    public double getHeightCoM() { return heightCoM; }

    @Override
    public double getResRot() {
        return resRot;
    }

    @Override
    public double getCorStifFront() {
        return corStifFront;
    }

    @Override
    public double getCorStifBack() {
        return corStifBack;
    }

    @Override
    public double getPower() {
        return power;
    }

    @Override
    public double getEngineForce() {
        return engineForce;
    }

    @Override
    public double getBrakeForce() {
        return brakeForce;
    }

    @Override
    public double getStaticMaxLoadFront() {
        return staticMaxLoadFront;
    }

    @Override
    public double getStaticMaxLoadBack() {
        return staticMaxLoadBack;
    }

    @Override
    public AckermannState getState() {
        return state;
    }

    @Override
    public void setState(AckermannState state) {
        this.state = state;
    }

    @Override
    public void updateState (AckermannModel model, double deltaInput, double forceInput, double deltaT) {
        double delta = this.deltaMax * deltaInput;
        model.updateState(this, delta, forceInput, deltaT);
    }
}
