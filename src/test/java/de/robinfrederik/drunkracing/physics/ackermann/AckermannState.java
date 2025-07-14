package de.robinfrederik.drunkracing.physics.ackermann;

public class AckermannState{
    private double xCoM;     // global x coordinate of the Center of Mass (CoM)
    private double yCoM;     // global y coordinate of the Center of Mass (CoM)
    private double yaw;      // global heading angle
    private double longVel;  // longitudinal velocity in local frame of the car
    private double latVel;   // lateral velocity in local frame of the car
    private double yawRate;  // derivative of yaw, angular velocity around CoM
    private double slipEffFront;
    private double slipEffBack;
    private double slipFront;
    private double slipBack;
    private double slipRateFront;
    private double slipRateBack;
    private double latAccel;
    private double longAccel;
    private double yawRateAccel;
    private double longForceFront;
    private double longForceBack;
    private double latForceFront;
    private double latForceBack;
    private double tireGripCoeff;


    public AckermannState(double xCoM,
                                  double yCom,
                                  double longVel,
                                  double longAccel,
                                  double latVel,
                                  double latAccel,
                                  double yaw,
                                  double yawRate,
                                  double yawRateAccel,
                                  double slipEffFront,
                                  double slipEffBack,
                                  double slipRateFront,
                                  double slipRateBack,
                                  double slipFront,
                                  double slipBack,
                                  double longForceFront,
                                  double longForceBack,
                                  double latForceFront,
                                  double latForceBack,
                                  double tireGripCoeff
                                  ) {
        this.xCoM = xCoM;
        this.yCoM = yCom;
        this.longVel = longVel;
        this.latVel = latVel;
        this.longAccel = longAccel;
        this.latAccel = latAccel;
        this.yaw = yaw;
        this.yawRate = yawRate;
        this.yawRateAccel = yawRateAccel;
        this.slipFront = slipFront;
        this.slipBack = slipBack;
        this.slipEffFront = slipEffFront;
        this.slipEffBack = slipEffBack;
        this.slipRateFront = slipRateFront;
        this.slipRateBack = slipRateBack;
        this.longForceFront = longForceFront;
        this.longForceBack = longForceBack;
        this.latForceFront = latForceFront;
        this.latForceBack = latForceBack;
        this.tireGripCoeff = tireGripCoeff;
    }

    public AckermannState() {
        this.tireGripCoeff = 2;
    }

    public double getXCoM () {
        return this.xCoM;
    }
    public void setXCoM (double xCoM) {
        this.xCoM = xCoM;
    }

    public double getYCoM () {
        return this.yCoM;
    }
    public void setYCoM (double yCoM) {
        this.yCoM = yCoM;
    }

    public double getYaw () {
        return this.yaw;
    }
    public void setYaw (double yaw) {
        this.yaw = yaw;
    }

    public double getLongVel () {
        return this.longVel;
    }
    public void setLongVel (double longVel) {
        this.longVel = longVel;
    }

    public double getLatVel () {
        return this.latVel;
    }
    public void setLatVel (double latVel) {
        this.latVel = latVel;
    }

    public double getYawRate () {
        return this.yawRate;
    }

    public void setYawRate (double yawRate) {
        this.yawRate = yawRate;
    }

    public double getSlipEffFront () {
        return this.slipEffFront;
    }
    public void setSlipEffFront (double slipEffFront) {
        this.slipEffFront = slipEffFront;
    }

    public double getSlipEffBack () {
        return this.slipEffBack;
    }
    public void setSlipEffBack (double slipEffBack) {
        this.slipEffBack = slipEffBack;
    }

    public double getSlipFront() {
        return slipFront;
    }
    public void setSlipFront(double slipFront) {
        this.slipFront = slipFront;
    }

    public double getSlipBack () {
        return this.slipBack;
    }
    public void setSlipBack (double slipBack) {
        this.slipBack = slipBack;
    }

    public double getLatAccel () {
        return this.latAccel;
    }
    public void setLatAccel (double latAccel) {
        this.latAccel = latAccel;
    }

    public double getLongAccel () {
        return this.longAccel;
    }
    public void setLongAccel (double longAccel) {
        this.longAccel = longAccel;
    }

    public double getYawRateAccel() {
        return this.yawRateAccel;
    }
    public void setYawRateAccel(double yawRateAccel) {
        this.yawRateAccel = yawRateAccel;
    }

    public double getLongForceFront() {
        return longForceFront;
    }
    public void setLongForceFront(double longForceFront) {
        this.longForceFront = longForceFront;
    }

    public double getLongForceBack() {
        return longForceBack;
    }
    public void setLongForceBack(double longForceBack) {
        this.longForceBack = longForceBack;
    }

    public double getLatForceFront() {
        return latForceFront;
    }
    public void setLatForceFront(double latForceFront) {
        this.latForceFront = latForceFront;
    }

    public double getLatForceBack() {
        return latForceBack;
    }
    public void setLatForceBack(double latForceBack) {
        this.latForceBack = latForceBack;
    }

    public double getTireGripCoeff() {
        return tireGripCoeff;
    }
    public void setTireGripCoeff(double tireGripCoeff) {
        this.tireGripCoeff = tireGripCoeff;
    }

    public double getSlipRateFront() {
        return slipRateFront;
    }
    public void setSlipRateFront(double slipRateFront) {
        this.slipRateFront = slipRateFront;
    }

    public double getSlipRateBack() {
        return slipRateBack;
    }
    public void setSlipRateBack(double slipRateBack) {
        this.slipRateBack = slipRateBack;
    }
}

