package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;

public class FrictionModel {

    private double[] LoadTransferLoads (Car car) {
        final AckermannState state = car.getState();
        final double loadTransfer = car.getMass() * state.getLongAccel()
                * car.getHeightCoM() / (car.getDistFront() + car.getDistBack());

        final double LoadFront = car.getStaticMaxLoadFront() - loadTransfer;
        final double LoadBack = car.getStaticMaxLoadBack() + loadTransfer;

        return new double[] {
                LoadFront,
                LoadBack
        };
    }

    //projects front lateral tire force into body frame of the car
    public void ProjectForcesIntoBody (Car car, double delta) {
        final AckermannState state = car.getState();

        car.getState().setLongForceFront(state.getLongForceFront() - state.getLongForceFront() * Math.sin(delta));
        car.getState().setLatForceFront(state.getLatForceFront() * Math.cos(delta));
    }

    public void ForceEllipse (Car car, double shape) {
        final AckermannState state = car.getState();
        final double [] loadTransferLoads = LoadTransferLoads(car);
        final double maxForceFront = state.getTireGripCoeff() * loadTransferLoads[0];
        final double maxForceBack = state.getTireGripCoeff() * loadTransferLoads[1];

        final double longForceFront = state.getLongForceFront();
        final double latForceFront = state.getLatForceFront();
        final double longForceBack = state.getLongForceBack();
        final double latForceBack = state.getLatForceBack();

        final double frontForce = Math.hypot(longForceFront, latForceFront);
        final double backForce = Math.hypot(longForceBack, latForceBack);


        if (frontForce > maxForceFront) {
            final double scalingFactor = maxForceFront / frontForce;
            car.getState().setLongForceFront( longForceFront * scalingFactor );
            car.getState().setLatForceFront( latForceFront * scalingFactor * shape);
        }

        if (backForce > maxForceBack) {
            final double scalingFactor = maxForceBack / backForce;
            car.getState().setLongForceBack( longForceBack * scalingFactor );
            car.getState().setLatForceBack( latForceBack * scalingFactor * shape);
        }
    }

    public void LateralForceFriction (Car car,
                                      double speedScale,
                                      double latAlignCoeffFront,
                                      double latAlignCoeffBack) {

        final AckermannState state = car.getState();
        final double alignmentDampeningFront = - latAlignCoeffFront * state.getSlipRateFront();
        final double alignmentDampeningBack = - latAlignCoeffBack * state.getSlipRateBack();

        car.getState().setLatForceFront((state.getLatForceFront() - alignmentDampeningFront) * Math.tanh(Math.abs(state.getLongVel()) / speedScale));
        car.getState().setLatForceBack((state.getLatForceBack() - alignmentDampeningBack) * Math.tanh(Math.abs(state.getLongVel()) / speedScale));
    }

    public void LongitudinalForceFriction (Car car,
                                           double aeroDrag,
                                           double rollingRes) {
        final AckermannState state = car.getState();
        final double [] loadTransferLoads = LoadTransferLoads(car);

        final double aeroDragForce = aeroDrag * Math.pow(state.getLongVel(), 2);
        final double rollingResistanceForceFront = rollingRes * loadTransferLoads[0];
        final double rollingResistanceForceBack = rollingRes * loadTransferLoads[1];

        car.getState().setLongForceFront(
                state.getLongForceFront()
                        - Math.signum(state.getLongVel())
                        * (0.5 * aeroDragForce + rollingResistanceForceFront)
        );

        car.getState().setLongForceBack(
                state.getLongForceBack()
                        - Math.signum(state.getLongVel())
                        * (0.5 * aeroDragForce + rollingResistanceForceBack)
        );
    }
}
