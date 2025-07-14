package de.robinfrederik.drunkracing.physics.ackermann;

import de.robinfrederik.drunkracing.physics.ackermann.formulae.*;

public class CarGoKartSportModel extends GeneralAckermannModel{
    public CarGoKartSportModel () {
        super(
                new SlipAngleLinear(
                        3,
                        Math.toRadians(25)),

                new TyreRelaxationDynamic(
                        0.1,
                        0.15,
                        0.1,
                        100000),

                new LateralForcesPacejkaSimple(
                        5,
                        5,
                        1.1,
                        1.1,
                        1,
                        1),

                new LongitudinalForceRWD(
                        1,
                        0.55),

                new FrictionModel(),

                new AccelerationsLocalDynamic(
                        400,
                        1500),

                new IntegrationSemiImplicit()
        );
    }

}
