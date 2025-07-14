package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;

//standard tyre relaxation dynamic, adjusted with some dampening to speed up general slip angle decay
public class TyreRelaxationDynamic implements TyreRelaxationDynamicFormula{

    private final double relaxationLengthFront;
    private final double relaxationLengthBack;
    private final double tolerance;
    private final double dampeningCoeff;

    public TyreRelaxationDynamic (double relaxationLengthFront,
                                  double relaxationLengthBack,
                                  double tolerance,
                                  double dampeningCoeff){
        this.relaxationLengthFront = relaxationLengthFront;
        this.relaxationLengthBack = relaxationLengthBack;
        this.tolerance = tolerance;
        this.dampeningCoeff = dampeningCoeff;
    }
    public void formula (Car car,
                         double deltaT) {
        AckermannState state = car.getState();
        final double slipEffFront = state.getSlipEffFront();
        final double slipEffBack = state.getSlipEffBack();

        //dampening based on current effective slip angle
        final double slipEffDampeningFront =
                - state.getSlipEffFront() * this.dampeningCoeff;

        final double slipEffDampeningBack =
                - state.getSlipEffBack() * this.dampeningCoeff;

        //prevent problems around 0
        final double toleratedLongVel = Math.max(Math.abs(state.getLongVel()), this.tolerance);

        //calculate change in effective slip angle based on slip angle and apply dampening
        final double slipEffDerivFront =
                (state.getSlipFront() - slipEffFront)
                        / (relaxationLengthFront / toleratedLongVel) + slipEffDampeningFront;

        final double slipEffDerivBack =
                (state.getSlipBack() - slipEffBack)
                        / (relaxationLengthBack / toleratedLongVel) + slipEffDampeningBack;

        //integration
        car.getState().setSlipEffFront(slipEffFront + deltaT * slipEffDerivFront);
        car.getState().setSlipEffBack(slipEffBack + deltaT * slipEffDerivBack);
    }
}
