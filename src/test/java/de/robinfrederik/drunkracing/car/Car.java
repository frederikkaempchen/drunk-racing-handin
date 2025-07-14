package de.robinfrederik.drunkracing.car;

import de.robinfrederik.drunkracing.physics.ackermann.AckermannModel;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;

public interface Car {
    double getMass ();
    double getDistFront();
    double getDistBack();
    double getHeightCoM();
    double getResRot();
    double getCorStifFront();
    double getCorStifBack();
    double getPower();
    double getEngineForce();
    double getBrakeForce();
    double getStaticMaxLoadFront();
    double getStaticMaxLoadBack();
    AckermannState getState();
    void setState(AckermannState state);
    void updateState (AckermannModel model, double delta, double forceInput, double deltaT);
}
