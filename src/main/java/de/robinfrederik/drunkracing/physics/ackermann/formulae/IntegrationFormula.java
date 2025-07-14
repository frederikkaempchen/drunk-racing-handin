package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;

public interface IntegrationFormula {
    void formula (Car car, double deltaT);
}
