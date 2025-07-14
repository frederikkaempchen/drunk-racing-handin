package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;

public class LateralForcesPacejkaSimple implements LateralForceFormula{
    private final double stiffnessFactorFront;
    private final double stiffnessFactorBack;
    private final double shapeFactorFront;
    private final double shapeFactorBack;
    private final double tireGripFront;
    private final double tireGripBack;



    public LateralForcesPacejkaSimple (double stiffnessFactorFront,
                                double stiffnessFactorBack,
                                double shapeFactorFront,
                                double shapeFactorBack,
                                double tireGripFront,
                                double tireGripBack) {
        this.stiffnessFactorFront = stiffnessFactorFront;
        this.stiffnessFactorBack = stiffnessFactorBack;
        this.shapeFactorFront = shapeFactorFront;
        this.shapeFactorBack = shapeFactorBack;
        this.tireGripFront = tireGripFront;
        this.tireGripBack = tireGripBack;
    }
    @Override
    public void formula (Car car) {
        final AckermannState state = car.getState();

        car.getState().setLatForceFront(
                state.getTireGripCoeff()* this.tireGripFront * car.getStaticMaxLoadFront()
                        * Math.sin( this.shapeFactorFront * Math.atan( stiffnessFactorFront * state.getSlipEffFront())));

        car.getState().setLatForceBack(
                state.getTireGripCoeff() * this.tireGripBack * car.getStaticMaxLoadBack()
                        * Math.sin( this.shapeFactorBack * Math.atan( stiffnessFactorBack * state.getSlipEffBack())));
    }
}
