package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;

public interface LongitudinalForceFormula {
    void formula (Car car, double forceInput);
}
