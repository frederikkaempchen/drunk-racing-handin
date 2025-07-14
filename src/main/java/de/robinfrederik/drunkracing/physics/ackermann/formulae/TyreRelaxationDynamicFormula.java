package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;

public interface TyreRelaxationDynamicFormula {
    public void formula (Car car,
                         double deltaT);
}
