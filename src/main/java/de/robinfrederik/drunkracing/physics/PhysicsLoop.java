package de.robinfrederik.drunkracing.physics;
import de.robinfrederik.drunkracing.physics.*;
import de.robinfrederik.drunkracing.car.*;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannModel;

//runs on a separate thread with a chosen physics model and updates the state of the car 1000x a second
//main then takes the most recent value when it needs it for visualisation
public class PhysicsLoop {
    private final AckermannModel physicsModel;
    private final double deltaT;
    private final double nsDeltaT;
    private volatile boolean loopActive;
    private Thread loopThread;

    public PhysicsLoop(AckermannModel physicsModel,
                       double deltaT) {
        this.physicsModel = physicsModel;
        this.deltaT = deltaT;
        this.nsDeltaT = 1e9 * deltaT; //convert deltaT(0.001 s) to ns
    }

    public void startLoop(CarVisual carVisual) {
        //prevent three threads disaster
        if (loopActive) return;
        loopActive = true;

        //separate thread to keep physics and visualisation separate and keep physics from being distorted by lag
        loopThread = new Thread(() -> {
            double lastTime = System.nanoTime();
            while (loopActive) {
                double now = System.nanoTime();
                if (now - lastTime >= this.nsDeltaT) {
                    carVisual.getCar().updateState( //update physical body of visual car
                            this.physicsModel,
                            carVisual.getSteeringInput(),//collect user input
                            carVisual.getAccelInput(),
                            this.deltaT
                            );
                    lastTime += this.nsDeltaT;
                }

                try {
                    Thread.sleep(0, 200_000);; //save CPU some work, no need to constantly check time
                } catch (InterruptedException ignored) {}
            }
        });

        loopThread.start();
    }

    public void stopLoop() {
        loopActive = false;
        if (loopThread != null) {
            try {
                loopThread.join();
            } catch (InterruptedException ignored) {}
        }
    }
}
