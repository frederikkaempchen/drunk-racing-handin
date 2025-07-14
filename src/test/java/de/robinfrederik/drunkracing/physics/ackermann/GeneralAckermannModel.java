package de.robinfrederik.drunkracing.physics.ackermann;

import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.physics.ackermann.formulae.*;

public class GeneralAckermannModel implements AckermannModel{
    //Formulas used
    private final SlipAngleFormula slipAngleFormula;
    private final TyreRelaxationDynamicFormula tyreRelaxationDynamicFormula;
    private final LateralForceFormula lateralForceFormula;
    private final LongitudinalForceFormula longitudinalForceFormula;
    private final FrictionModel frictionModelFormulas;
    private final AccelerationsLocalFormula accelerationsLocalFormula;
    private final IntegrationFormula integrationFormula;

    public GeneralAckermannModel (SlipAngleFormula slipAngleFormula,
                                  TyreRelaxationDynamicFormula tyreRelaxationDynamicFormula,
                                  LateralForceFormula lateralForceFormula,
                                  LongitudinalForceFormula longitudinalForceFormula,
                                  FrictionModel frictionModelFormulas,
                                  AccelerationsLocalFormula accelerationsLocalFormula,
                                  IntegrationFormula integrationFormula
                                  ) {
        this.slipAngleFormula = slipAngleFormula;
        this.tyreRelaxationDynamicFormula = tyreRelaxationDynamicFormula;
        this.lateralForceFormula = lateralForceFormula;
        this.longitudinalForceFormula = longitudinalForceFormula;
        this.frictionModelFormulas = frictionModelFormulas;
        this.accelerationsLocalFormula = accelerationsLocalFormula;
        this.integrationFormula = integrationFormula;
    }


    @Override
    public void updateState (Car car,
                             double delta,
                             double forceInput,
                             double deltaT){
        slipAngleFormula.formula (car, delta, deltaT);
        tyreRelaxationDynamicFormula.formula(car, deltaT);
        longitudinalForceFormula.formula (car, forceInput);
        lateralForceFormula.formula (car);
        frictionModelFormulas.ForceEllipse(car, 0.9);
        frictionModelFormulas.LateralForceFriction(car, 7, 50, 100);
        frictionModelFormulas.LongitudinalForceFriction(car, 0.5, 0.025);
        frictionModelFormulas.ProjectForcesIntoBody(car, delta);
        accelerationsLocalFormula.formula (car, delta);
        integrationFormula.formula(car, deltaT); //updates state
    }
}