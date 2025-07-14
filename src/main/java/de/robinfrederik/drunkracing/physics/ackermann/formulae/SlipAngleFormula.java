package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;

public interface SlipAngleFormula {
    void formula (Car car, double delta, double deltaT);
}