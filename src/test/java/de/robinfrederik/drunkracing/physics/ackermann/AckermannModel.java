package de.robinfrederik.drunkracing.physics.ackermann;

import de.robinfrederik.drunkracing.car.Car;

public interface AckermannModel {
    void updateState (Car car,
                      double deltaInput,
                      double forceInput,
                      double deltaT);
}