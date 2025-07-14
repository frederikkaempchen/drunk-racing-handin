package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;

//basic slip angle formula adjusted to work for both driving forwards and backwards
public class SlipAngleLinear implements SlipAngleFormula {
    private final double tolerance;
    private final double slipAngleClamp;

    public SlipAngleLinear (double tolerance,
                            double slipAngleClamp){

        this.tolerance = tolerance;
        this.slipAngleClamp = slipAngleClamp;
    }

    @Override
    public void formula (Car car, double delta, double deltaT) {
        AckermannState state = car.getState();
        double distFront = car.getDistFront();
        double distBack = car.getDistBack();

        double toleratedLongVel = (state.getLongVel() >= 0 ?
                Math.max(this.tolerance, state.getLongVel())
                : Math.min(-this.tolerance, state.getLongVel()));

        double frontSlipAngle = Math.atan((state.getLatVel() + distFront * state.getYawRate()) / toleratedLongVel) - Math.signum(state.getLongVel()) * delta;
        double backSlipAngle  = Math.atan((state.getLatVel() - distBack * state.getYawRate()) / toleratedLongVel);

        frontSlipAngle = Math.min( this.slipAngleClamp, Math.max( -this.slipAngleClamp, frontSlipAngle ) );
        backSlipAngle = Math.min( this.slipAngleClamp, Math.max(-this.slipAngleClamp, backSlipAngle ) );

        car.getState().setSlipRateFront((frontSlipAngle - state.getSlipFront()) / deltaT);
        car.getState().setSlipRateBack((backSlipAngle - state.getSlipBack()) / deltaT);

        car.getState().setSlipFront(frontSlipAngle);
        car.getState().setSlipBack(backSlipAngle);
    }
}