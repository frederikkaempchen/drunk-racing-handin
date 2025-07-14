package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;

public class AccelerationsLocalDynamic implements AccelerationsLocalFormula{

    private final double yawRateDampening;
    private final double lateralScrubbingDrag;

    public AccelerationsLocalDynamic (double yawRateDampening,
                                      double lateralScrubbingDrag) {
        this.yawRateDampening = yawRateDampening;
        this.lateralScrubbingDrag = lateralScrubbingDrag;
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
        //

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
