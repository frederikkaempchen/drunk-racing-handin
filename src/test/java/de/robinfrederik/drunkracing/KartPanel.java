package de.robinfrederik.drunkracing;

import de.robinfrederik.drunkracing.car.CarGoKartSport;
import de.robinfrederik.drunkracing.physics.ackermann.AckermannModel;
import de.robinfrederik.drunkracing.physics.ackermann.CarGoKartSportModel;
import de.robinfrederik.drunkracing.physics.ackermann.GeneralAckermannModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class KartPanel extends JPanel implements KeyListener, ActionListener {
    private CarGoKartSport racingSim; // Your main simulation object
    private AckermannModel simModel; // Your simulation model object

    private Timer timer;

    // Simulation update rate (dt for physics, must match your physics engine)
    private final double SIM_DT = 0.001; // seconds (e.g., 1000 Hz)

    // GUI refresh rate (how often the screen updates)
    private final int GUI_REFRESH_RATE_MS = 10; // milliseconds (100 Hz refresh)

    // Input ramping speed (controls how fast steering/throttle changes)
    private final double INPUT_RAMP_SPEED = 0.1; // Change per GUI refresh cycle

    // Current normalized input values (-1.0 to 1.0)
    private double currentSteeringInput = 0.0; // -1.0 for full left, 1.0 for full right
    private double currentThrottleBrakeInput = 0.0; // -1.0 for full brake, 1.0 for full throttle

    // Key states
    private boolean keyWPressed = false;
    private boolean keySPressed = false;
    private boolean keyAPressed = false;
    private boolean keyDPressed = false;

    // Drawing scale: Pixels per meter
    private final double SCALE = 50.0; // 50 pixels per meter (adjust for zoom)

    // Visual dimensions of the kart (approximate, for drawing arrow)
    private final double KART_VISUAL_LENGTH = 1.3; // meters
    private final double KART_VISUAL_WIDTH = 0.7; // meters

    public KartPanel() {
        // Initialize your simulation objects
        this.racingSim = new CarGoKartSport();
        this.simModel = new CarGoKartSportModel(); // Assuming GeneralAckermannModel implements AckermannModel

        setFocusable(true); // Panel must be focusable to receive key events
        addKeyListener(this);

        // Set up a Swing Timer to trigger updates and repaints
        timer = new Timer(GUI_REFRESH_RATE_MS, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear the panel
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- 1. Set up coordinate system ---
        // Move origin to center of panel
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        g2d.translate(centerX, centerY);

        // Invert Y-axis to match typical physics coordinates (Y-up)
        g2d.scale(1, -1);

        // Apply drawing scale (pixels per meter)
        g2d.scale(SCALE, SCALE);

        // --- 2. Translate to kart's current position (inverse transform to draw kart at origin of its local space) ---
        // The kart's (x,y) from your simulation is its center of mass in global coordinates
        g2d.translate(-racingSim.getState().getXCoM(), -racingSim.getState().getYCoM());

        // --- 3. Draw Grid/Background (optional, for context) ---
        g2d.setColor(new Color(230, 230, 230)); // Light gray grid
        // Draw some grid lines every 5 meters in world coordinates
        // Adjust bounds based on expected simulation area or window size
        int gridRange = 100; // Draw grid from -100m to +100m
        for (int i = -gridRange; i <= gridRange; i += 5) {
            g2d.drawLine(i, -gridRange, i, gridRange); // Vertical lines
            g2d.drawLine(-gridRange, i, gridRange, i); // Horizontal lines
        }
        // Draw global origin
        g2d.setColor(Color.BLUE);
        g2d.fillOval(-1, -1, 2, 2); // Small circle at global (0,0)

        // --- 4. Draw the Kart (arrow shape) ---
        AffineTransform oldTransform = g2d.getTransform();

        // Move to kart's world position (in view-centered coordinate system, this ends up at screen center)
                g2d.translate(racingSim.getState().getXCoM(), racingSim.getState().getYCoM());

        // Rotate around CoM
                g2d.rotate(racingSim.getState().getYaw());

        // Define arrow points (relative to kart's CoM at 0,0 for drawing purposes)
        Path2D.Double kartShape = new Path2D.Double();
        // Simple arrow/rectangle shape for the kart
        kartShape.moveTo(KART_VISUAL_LENGTH / 2, 0); // Nose
        kartShape.lineTo(-KART_VISUAL_LENGTH / 2 + (KART_VISUAL_LENGTH * 0.1), KART_VISUAL_WIDTH / 2); // Front corner left
        kartShape.lineTo(-KART_VISUAL_LENGTH / 2, KART_VISUAL_WIDTH / 2 * 0.8); // Mid-body left
        kartShape.lineTo(-KART_VISUAL_LENGTH / 2 + (KART_VISUAL_LENGTH * 0.1), 0); // Tail indentation
        kartShape.lineTo(-KART_VISUAL_LENGTH / 2, -KART_VISUAL_WIDTH / 2 * 0.8); // Mid-body right
        kartShape.lineTo(-KART_VISUAL_LENGTH / 2 + (KART_VISUAL_LENGTH * 0.1), -KART_VISUAL_WIDTH / 2); // Front corner right
        kartShape.lineTo(KART_VISUAL_LENGTH / 2, 0); // Back to nose

        g2d.setColor(Color.RED);
        g2d.fill(kartShape);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(0.1f)); // Thinner stroke relative to scale
        g2d.draw(kartShape);

        // Draw a small circle at the CoM (origin of the kart's local frame)
        g2d.setColor(Color.BLACK);
        g2d.fillOval(- (int)(0.1 * SCALE) / (int)SCALE, - (int)(0.1 * SCALE) / (int)SCALE, (int)(0.2 * SCALE) / (int)SCALE, (int)(0.2 * SCALE) / (int)SCALE);

        // Restore transform for drawing other elements (like info text)
        g2d.setTransform(oldTransform);

        // --- 5. Draw Info Text ---
        g2d.setColor(Color.BLACK);
        g2d.scale(1, -1); // Invert Y-axis back for text (since g2d.scale(1,-1) was applied earlier)
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Format and display relevant state variables
        String info = String.format(
                "X: %.2f m | Y: %.2f m\n" +
                        "Vx: %.2f m/s | Vy: %.2f m/s\n" +
                        "Yaw: %.2f deg | YawRate: %.2f deg/s\n" +
                        "SlipEffFront: %.2f deg | SLipEffBack: %.2f\n"+
                        "FrontLatForce: %.2f N | BackLatForce: %.2f N\n"+
                        "FrontLonForce: %.2f N | BackLonForce: %.2f N\n"+
                        "Steering Input: %.2f | Throttle/Brake Input: %.2f",
                racingSim.getState().getXCoM(), racingSim.getState().getYCoM(),
                racingSim.getState().getLongVel(), racingSim.getState().getLatVel(),
                Math.toDegrees(racingSim.getState().getYaw()), Math.toDegrees(racingSim.getState().getYawRate()),
                Math.toDegrees(racingSim.getState().getSlipEffFront()), Math.toDegrees(racingSim.getState().getSlipEffBack()),
                racingSim.getState().getLatForceFront(), racingSim.getState().getLatForceBack(),
                racingSim.getState().getLongForceFront(), racingSim.getState().getLongForceBack(),
                currentSteeringInput, currentThrottleBrakeInput
        );

        // Reset transform to draw text in fixed pixel coordinates
        g2d.setTransform(new AffineTransform());
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));

        int textPixelX = 10;
        int textPixelY = 20;
        for (String line : info.split("\n")) {
            g2d.drawString(line, textPixelX, textPixelY);
            textPixelY += g2d.getFontMetrics().getHeight() + 2; // Add some line spacing
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // --- Update Input Values (Ramp and Decay) ---
        // Throttle/Brake (W/S)
        if (keyWPressed) {
            currentThrottleBrakeInput = Math.min(1.0, currentThrottleBrakeInput + INPUT_RAMP_SPEED);
        } else if (keySPressed) {
            currentThrottleBrakeInput = Math.max(-1.0, currentThrottleBrakeInput - INPUT_RAMP_SPEED);
        } else {
            // Decay input when key is released
            if (currentThrottleBrakeInput > 0) {
                currentThrottleBrakeInput = Math.max(0.0, currentThrottleBrakeInput - 5 * INPUT_RAMP_SPEED);
            } else if (currentThrottleBrakeInput < 0) {
                currentThrottleBrakeInput = Math.min(0.0, currentThrottleBrakeInput + 5 * INPUT_RAMP_SPEED);
            }
        }

        // Steering (A/D)
        if (keyAPressed) {
            currentSteeringInput = Math.max(-1.0, currentSteeringInput - INPUT_RAMP_SPEED);
        } else if (keyDPressed) {
            currentSteeringInput = Math.min(1.0, currentSteeringInput + INPUT_RAMP_SPEED);
        } else {
            // Decay input when key is released
            if (currentSteeringInput > 0) {
                currentSteeringInput = Math.max(0.0, currentSteeringInput - 5 * INPUT_RAMP_SPEED);
            } else if (currentSteeringInput < 0) {
                currentSteeringInput = Math.min(0.0, currentSteeringInput + 5 * INPUT_RAMP_SPEED);
            }
        }

        // --- Update Simulation ---
        // Call your simulation's update method multiple times per GUI frame
        // to maintain the physics time step (SIM_DT).
        int numSimSteps = (int) (GUI_REFRESH_RATE_MS / (SIM_DT * 1000.0));
        for (int i = 0; i < numSimSteps; i++) {
            racingSim.updateState(simModel, currentSteeringInput, currentThrottleBrakeInput, SIM_DT);
        }

        // --- Request GUI Repaint ---
        repaint();
    }

    // --- KeyListener Implementations ---
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_W) keyWPressed = true;
        if (keyCode == KeyEvent.VK_S) keySPressed = true;
        if (keyCode == KeyEvent.VK_A) keyAPressed = true;
        if (keyCode == KeyEvent.VK_D) keyDPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_W) keyWPressed = false;
        if (keyCode == KeyEvent.VK_S) keySPressed = false;
        if (keyCode == KeyEvent.VK_A) keyAPressed = false;
        if (keyCode == KeyEvent.VK_D) keyDPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) { /* Not used */ }
}
