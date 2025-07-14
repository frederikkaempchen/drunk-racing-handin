package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;
import de.robinfrederik.drunkracing.physics.ackermann.formulae.AccelerationsLocalFormula;

//basic newtonian accelerations + vicious dampening for stability
public class AccelerationsLocalDynamic implements AccelerationsLocalFormula {

    private final double yawRateDampening;
    private final double lateralScrubbingDrag;

    public AccelerationsLocalDynamic (double yawRateDampening,
                                      double lateralScrubbingDrag) {
        //vicious dampening for stability
        this.yawRateDampening = yawRateDampening; //slows cars turning
        this.lateralScrubbingDrag = lateralScrubbingDrag; //makes sure friction is applied when the tires start sliding sideways
    }
    @Override
    public void formula(Car car, double delta) {

        final double distFront = car.getDistFront();
        final double distBack = car.getDistBack();
        final double mass = car.getMass();
        final double resRot = car.getResRot();
        AckermannState state = car.getState();
        final double latForceFront = state.getLatForceFront();
        final double latForceBack = state.getLatForceBack();

        double lateralScrubbingDragForce = - this.lateralScrubbingDrag * state.getLatVel();
        double yawDampeningForce =  - this.yawRateDampening * state.getYawRate();

        car.getState().setLongAccel(
                (state.getLongForceBack() + state.getLongForceFront())
                        / mass
                        + state.getYawRate() * state.getLatVel()
        );

        car.getState().setLatAccel(
                (latForceFront + latForceBack + lateralScrubbingDragForce)
                        / mass
                        - state.getYawRate() * state.getLongVel()
        );

        car.getState().setYawRateAccel(
                (distFront * latForceFront - distBack * latForceBack + yawDampeningForce)
                        / resRot
        );
    }
}
