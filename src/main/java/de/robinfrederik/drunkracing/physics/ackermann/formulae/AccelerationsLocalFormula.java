package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;

public interface AccelerationsLocalFormula {
    void formula (Car car, double delta);
}
