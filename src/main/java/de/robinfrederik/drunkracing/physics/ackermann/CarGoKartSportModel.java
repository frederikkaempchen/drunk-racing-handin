package de.robinfrederik.drunkracing.physics.ackermann;

import de.robinfrederik.drunkracing.physics.ackermann.formulae.*;

//generally just don't change anything, this is finely tuned to work with the car
public class CarGoKartSportModel extends GeneralAckermannModel{
    public CarGoKartSportModel () {
        super(
                new SlipAngleLinear(
                        3,
                        Math.toRadians(25)),

                //short tyre relaxation and some dampening to prevent extreme slip angles and speed up slip angle decay
                new TyreRelaxationDynamic(
                        0.1,
                        0.15,
                        0.1,
                        100),

                //soft tires that don't show any extreme behaviour, just saturate grip
                new LateralForcesPacejkaSimple(
                        5,
                        5,
                        1.1,
                        1.1,
                        1,
                        1),

                //slight brake bias forwards, rear wheel drive, both interesting for where to apply forces, load transfer and force ellipse
                new LongitudinalForceRWD(
                        1,
                        0.55),

                new FrictionModel(),

                new AccelerationsLocalDynamic(
                        500,
                        1500),

                new IntegrationSemiImplicit() //semi implicit euler integration for better results than basic euler
        );
    }
}
