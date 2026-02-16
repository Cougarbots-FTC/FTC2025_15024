package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.pedropathing.util.Timer;

public class Outtake2 {

    // Motors
    private DcMotorEx leftShooter;
    private DcMotorEx rightShooter;

    // OpMode & Gamepads
    private final OpMode opMode;
    private final Gamepad driver2;

    // Other subsystems
    private final Delivery delivery;
    private final Intake intake;

    // State
    private boolean outtakeOn = false;

    // Button edge tracking
    private boolean prevX = false;
    private boolean prevRB = false;
    private boolean prevLB = false;
    private boolean prevDpadUp = false;

    // Power control
    private double outtakePower = -0.8;
    private static final double POWER_STEP = 0.075;
    private static final double MAX_POWER = 1.0;
    private static final double MIN_POWER = -1.0;

    // Triple shoot state machine
    private boolean tripleShooting = false;
    private int tripleCount = 0;
    private Timer tripleTimer = new Timer();

    private enum TripleState {
        SPINUP,
        FEED,
        RESET,
        DONE
    }

    private TripleState tripleState = TripleState.DONE;

    // Constructor
    public Outtake2(OpMode opMode, Delivery delivery, Intake intake) {
        this.opMode = opMode;
        this.delivery = delivery;
        this.intake = intake;

        HardwareMap hardwareMap = opMode.hardwareMap;
        driver2 = opMode.gamepad2;

        leftShooter = hardwareMap.get(DcMotorEx.class, "outtake1");
        rightShooter = hardwareMap.get(DcMotorEx.class, "outtake2");

        leftShooter.setDirection(DcMotor.Direction.FORWARD);
        rightShooter.setDirection(DcMotor.Direction.FORWARD);

        leftShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    // Call every loop in TeleOp
    public void teleOp() {
        handleToggle();
        handleSpeedAdjust();
        handleTripleShoot();

        if (tripleShooting) {
            runTripleShoot();
        } else {
            if (outtakeOn) {
                setPower(outtakePower);
            } else {
                stop();
            }
        }

        sendTelemetry();
    }

    // =============================
    // NORMAL OUTTAKE CONTROLS
    // =============================

    private void handleToggle() {
        boolean xPressed = driver2.x;

        if (xPressed && !prevX) {
            outtakeOn = !outtakeOn;
        }

        prevX = xPressed;
    }

    private void handleSpeedAdjust() {
        boolean rb = driver2.right_bumper;
        boolean lb = driver2.left_bumper;

        if (rb && !prevRB) {
            outtakePower += POWER_STEP;
        }

        if (lb && !prevLB) {
            outtakePower -= POWER_STEP;
        }

        outtakePower = Math.max(MIN_POWER, Math.min(MAX_POWER, outtakePower));

        prevRB = rb;
        prevLB = lb;
    }


    // TRIPLE SHOOT


    private void handleTripleShoot() {
        boolean dpadUp = driver2.dpad_up;

        if (dpadUp && !prevDpadUp && !tripleShooting) {
            tripleShoot();
        }

        prevDpadUp = dpadUp;
    }

    // PUBLIC FUNCTION (what you asked for)
    public void tripleShoot() {
        tripleShooting = true;
        tripleCount = 0;
        tripleState = TripleState.SPINUP;
        tripleTimer.resetTimer();
    }

    private void runTripleShoot() {

        switch (tripleState) {

            case SPINUP:
                setPower(outtakePower);
                if (tripleTimer.getElapsedTimeSeconds() > 1.2) {
                    tripleState = TripleState.FEED;
                    tripleTimer.resetTimer();
                }
                break;

            case FEED:
                intake.intakeOn(-1);
                delivery.deliveryOn(0.8);

                if (tripleTimer.getElapsedTimeSeconds() > 1.2) {
                    intake.intakeOff();
                    delivery.deliveryOff();
                    tripleCount++;
                    tripleTimer.resetTimer();

                    if (tripleCount >= 3) {
                        tripleState = TripleState.DONE;
                    } else {
                        tripleState = TripleState.RESET;
                    }
                }
                break;

            case RESET:
                if (tripleTimer.getElapsedTimeSeconds() > 0.5) {
                    tripleState = TripleState.FEED;
                    tripleTimer.resetTimer();
                }
                break;

            case DONE:
                stop();
                tripleShooting = false;
                tripleState = TripleState.DONE;
                break;
        }
    }

    // =============================
    // MOTOR HELPERS
    // =============================

    public void setPower(double power) {
        leftShooter.setPower(power);
        rightShooter.setPower(power);
    }

    private void stop() {
        leftShooter.setPower(0);
        rightShooter.setPower(0);
    }

    // =============================
    // TELEMETRY
    // =============================

    public void sendTelemetry() {
        opMode.telemetry.addData("Outtake", outtakeOn ? "ON" : "OFF");
        opMode.telemetry.addData("Triple Shooting", tripleShooting);
        opMode.telemetry.addData("Outtake Power", "%.2f", outtakePower);
        opMode.telemetry.addData("Velocity L", leftShooter.getVelocity());
        opMode.telemetry.addData("Velocity R", rightShooter.getVelocity());
    }
}
