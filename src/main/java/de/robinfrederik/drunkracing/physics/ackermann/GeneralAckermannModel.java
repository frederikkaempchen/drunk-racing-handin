package de.robinfrederik.drunkracing.physics.ackermann;

import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannModel;
import de.robinfrederik.drunkracing.physics.ackermann.formulae.*;

//the structure of the model is a Dynamic Ackermann Bycicle Model
//gets more complex than most models via Nonlinear Tire Forces (Pakejka), Tire Relaxation, Friction Ellipse, Load Transfer
public class GeneralAckermannModel implements AckermannModel {
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
        slipAngleFormula.formula (car, delta, deltaT); // first calculate slip angles (angle between wheels facing and moving direction)
        tyreRelaxationDynamicFormula.formula(car, deltaT); // calculate effective slip angles, taking into account that tires react delayed to the change since rubber is flexible
        longitudinalForceFormula.formula (car, forceInput); // applies braking and acceleration forces to the car, taking into account brake bias and RWD
        lateralForceFormula.formula (car); // lateral Forces that apply to tires based on slip angles - In GoKartModel using Pacejka
        frictionModelFormulas.ForceEllipse(car, 0.9); //keeps lateral + longitudinal force capped and scales both down if needed, also applies longitudinal load transfer based on braking/accelerating
        frictionModelFormulas.LateralForceFriction(car, 7, 50, 100); //models the wish of the body to align with the tires - a little bit sketchy but this isn't a 4 tire model...
        frictionModelFormulas.LongitudinalForceFriction(car, 0.5, 0.025); //aero drag, rolling friction
        frictionModelFormulas.ProjectForcesIntoBody(car, delta); //the tire forces are at different angles than the forces that apply to the body -> this basically rotates and adjusts them to apply to the body
        accelerationsLocalFormula.formula (car, delta); //accelerations (long, lat, yaw) based on forces applied to car, viciously dampened with some coefficients for stability
        integrationFormula.formula(car, deltaT); //updates car state based on time between calculation steps
    }
}