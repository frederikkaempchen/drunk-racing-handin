package de.robinfrederik.drunkracing.physics.ackermann.formulae;

import de.robinfrederik.drunkracing.car.Car;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;
import de.robinfrederik.drunkracing.physics.ackermann.formulae.IntegrationFormula;
//semi implicit euler integration for the final step to the new state
public class IntegrationSemiImplicit implements IntegrationFormula {

    @Override
    public void formula (Car car,
                         double deltaT) {

        AckermannState state = car.getState();

        car.getState().setLongVel(state.getLongVel() + deltaT * state.getLongAccel());
        car.getState().setLatVel(state.getLatVel() + deltaT * state.getLatAccel());
        car.getState().setYawRate(state.getYawRate() + deltaT * state.getYawRateAccel());

        state = car.getState();

        double globalLongDeriv = state.getLongVel() * Math.cos(state.getYaw()) - state.getLatVel() * Math.sin(state.getYaw());  // global longitudinal acceleration
        double globalLatDeriv  = state.getLongVel() * Math.sin(state.getYaw()) + state.getLatVel() * Math.cos(state.getYaw());  // global lateral acceleration
        double globalYawDeriv  = state.getYawRate();

        car.getState().setXCoM(state.getXCoM() + deltaT * globalLongDeriv * 30); //30 factor for adjustment to visual movement
        car.getState().setYCoM(state.getYCoM() + deltaT * globalLatDeriv * 30);
        car.getState().setYaw(state.getYaw() + deltaT * globalYawDeriv);
        car.getState().setYaw((state.getYaw() + Math.PI) % (2 * Math.PI) - Math.PI); //prevents yaw from growing to infinity if one does donuts
    }
}
