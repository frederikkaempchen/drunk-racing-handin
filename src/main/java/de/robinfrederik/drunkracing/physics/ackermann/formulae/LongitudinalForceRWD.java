package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.physics.ackermann.formulae.LongitudinalForceFormula;

// rear wheel drive car
// braking, accelerating and driving backwards all held separate
// acceleration based on engine power and force
// braking based on brake force
public class LongitudinalForceRWD implements LongitudinalForceFormula {
    private final double tolerance;
    private final double brakeBias;     //distribution of braking force between front and back brakes
    public LongitudinalForceRWD(double tolerance, double brakeBias) {
        this.tolerance = tolerance;
        this.brakeBias  = brakeBias;
    }

    @Override
    public void formula (Car car, double forceInput) {
        double power = car.getPower();
        double engineForce = car.getEngineForce();
        double brakeForce = car.getBrakeForce();
        double longVel = car.getState().getLongVel();

        //there once was some artificial rolling drag here, can be reimplemented if one feels like it
        if (forceInput == 0) {
            car.getState().setLongForceFront(- 0 * longVel);
            car.getState().setLongForceBack(- 0 * longVel);
        } else if (forceInput * longVel < 0) {    //braking
            car.getState().setLongForceFront(forceInput * this.brakeBias * brakeForce);
            car.getState().setLongForceBack(forceInput * (1 - this.brakeBias) * brakeForce);
        } else if (forceInput < 0) {     //accelerating backwards
            car.getState().setLongForceFront(0);
            car.getState().setLongForceBack(
                    forceInput * 0.1 *
                            Math.min(engineForce,
                                    (power / Math.max(Math.abs(longVel), this.tolerance))));
        } else { //accelerating forwards
            car.getState().setLongForceFront(0);
            car.getState().setLongForceBack(
                    forceInput *
                            Math.min(engineForce,
                                    (power / Math.max(Math.abs(longVel), this.tolerance))));
        }
    }
}
