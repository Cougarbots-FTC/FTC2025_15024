package org.firstinspires.ftc.teamcode.subsystems;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;
import com.pedropathing.util.Timer;

public class Dupe {

    // Motors
    private DcMotorEx leftShooter;
    private DcMotorEx rightShooter;

    // Push servos (lebron)
    private CRServo lebronA;
    private CRServo lebronB;

    // Subsystems
    private final Delivery delivery;
    private final Intake intake;

    // OpMode & Gamepad
    private final OpMode opMode;
    private final Gamepad driver2;

    // Toggle shooting
    private boolean prevDpadUp = false;
    private boolean tripleShooting = false;

    // State machine
    private Timer shootTimer = new Timer();
    private int shootCount = 0;

    private enum ShootState {
        SPINUP_FIRST,
        SHOOTING,
        DONE
    }

    private ShootState shootState = ShootState.DONE;

    public Dupe(OpMode opMode, Delivery delivery, Intake intake) {
        this.opMode = opMode;
        this.delivery = delivery;
        this.intake = intake;

        HardwareMap hardwareMap = opMode.hardwareMap;
        driver2 = opMode.gamepad2;

        leftShooter = hardwareMap.get(DcMotorEx.class, "outtake1");
        rightShooter = hardwareMap.get(DcMotorEx.class, "outtake2");

        lebronA = hardwareMap.get(CRServo.class, "push1");
        lebronB = hardwareMap.get(CRServo.class, "push2");

        leftShooter.setDirection(DcMotor.Direction.FORWARD);
        rightShooter.setDirection(DcMotor.Direction.FORWARD);

        leftShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    // Call every loop in TeleOp
    public void teleOp() {

        boolean dpadUp = driver2.dpad_up;

        if (dpadUp && !prevDpadUp && !tripleShooting) {
            tripleShoot();
        }

        prevDpadUp = dpadUp;

        if (tripleShooting) {
            runTripleShoot();
        }
    }

    //Triple Shoot

    public void tripleShoot() {
        tripleShooting = true;
        shootCount = 0;
        shootState = ShootState.SPINUP_FIRST;
        shootTimer.resetTimer();
    }

    private void runTripleShoot() {

        switch (shootState) {

            // FIRST BALL SPECIAL SPINUP (matches auto shootCount == 0)
            case SPINUP_FIRST:

                setPower(-0.8);

                if (shootTimer.getElapsedTimeSeconds() > 1.5) {
                    shootState = ShootState.SHOOTING;
                    shootTimer.resetTimer();
                }

                break;

            case SHOOTING:

                // Push servos
                lebronA.setPower(1);
                lebronB.setPower(-1);

                sleep(250);
                // Intake + Delivery
                intake.intakeOn(-1);
                sleep(250);
                if (shootCount == 2) {
                    delivery.deliveryOn(0.75); // third ball uses .75
                } else {
                    delivery.deliveryOn(0.80);
                }

                // 4 second feed time (matches auto sleep(4000))
                if (shootTimer.getElapsedTimeSeconds() > 4) {

                    // Stop everything like auto
                    lebronA.setPower(0);
                    lebronB.setPower(0);
                    intake.intakeOn(0);
                    delivery.deliveryOff();

                    shootCount++;
                    shootTimer.resetTimer();

                    if (shootCount >= 3) {
                        shootState = ShootState.DONE;
                    }
                }

                break;

            case DONE:

                setPower(0);
                tripleShooting = false;
                shootState = ShootState.DONE;

                break;
        }
    }

    // Motor helper
    public void setPower(double power) {
        leftShooter.setPower(power);
        rightShooter.setPower(power);
    }
}
