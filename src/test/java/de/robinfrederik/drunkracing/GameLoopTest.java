package de.robinfrederik.drunkracing;

import de.robinfrederik.drunkracing.car.CarGoKartSport;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannModel;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannState;
import de.robinfrederik.drunkracing.physics.ackermann.CarGoKartSportModel;
import de.robinfrederik.drunkracing.physics.ackermann.GeneralAckermannModel;

public class GameLoopTest {
    public static void main (String[] args) throws InterruptedException {
        CarGoKartSport racingSim = new CarGoKartSport();
        AckermannModel simModel = new CarGoKartSportModel();

        double deltaInputGoal = -1;
        double deltaInput = 0;
        double inputForce = -1;
        double deltaT = 0.001;

        for (int i = 0; i <= 4000; i++) {
            AckermannState currentState = racingSim.getState();

            double xCoM = currentState.getXCoM();
            double yCoM = currentState.getYCoM();
            double vx = currentState.getLongVel();
            double vy = currentState.getLatVel();
            double yaw = Math.toDegrees(currentState.getYaw());
            double yawRate = Math.toDegrees(currentState.getYawRate());
            double slipEffFront = Math.toDegrees(currentState.getSlipEffFront());
            System.out.println();
            System.out.printf("Step %d - Position: x: %.2f, y: %.2f, vx : %.2f m/s, vy: %.2f m/s, yaw: %.2f, yaw rate: %.2f%n", i, xCoM, yCoM, vx, vy, yaw, yawRate);

            if ( i >= 2000 && i < 4000) {
                inputForce = 1;
                deltaInput = deltaInputGoal * (i - 2000) / 2000;
            } else if ( i >= 4000 && i < 6000) {
                deltaInput = deltaInputGoal * (6000 - i) / 2000;
            } else if (i > 6000){
                deltaInput = 0;
                //inputForce = (vx != 0 ? 0.001 : 0);
               // inputForce = 0;
            }
            racingSim.updateState(simModel, deltaInput, inputForce, deltaT);
        }
    }
}
