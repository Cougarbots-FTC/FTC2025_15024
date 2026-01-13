package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.opMode;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * This is an example minimal implementation of the mecanum drivetrain
 * for demonstration purposes.  Not tested and not guaranteed to be bug free.
 *
 * @author Brandon Gong
 */
@Disabled
public class MecanumDrive {

    private DcMotor front_left  = null;
    private DcMotor front_right = null;
    private DcMotor back_left   = null;
    private DcMotor back_right  = null;

    private final Gamepad driver1;
    private final Gamepad driver2;
    private ElapsedTime runtime = new ElapsedTime();

    public MecanumDrive(OpMode opMode) {

        driver1 = opMode.gamepad1;
        driver2 = opMode.gamepad2;

        HardwareMap hardwareMap = opMode.hardwareMap;
        // Name strings must match up with the config on the Robot Controller
        // app.
        front_left   = hardwareMap.get(DcMotor.class, "FrontLeft");
        front_right  = hardwareMap.get(DcMotor.class, "FrontRight");
        back_left    = hardwareMap.get(DcMotor.class, "BackLeft");
        back_right   = hardwareMap.get(DcMotor.class, "BackRight");

        front_left.setDirection(DcMotor.Direction.REVERSE);
        front_right.setDirection(DcMotor.Direction.FORWARD);

    }

    public void teleOp() {

    //initialize gamepad or whatever late

        double speed;

        if(driver1.right_bumper) {
            speed = 0.5;
        } else {
            speed = 1;
        }

        // You may need to multiply some of these by -1 to invert direction of
        // the motor.  This is not an issue with the calculations themselves.
        double vertical = driver1.left_stick_y;
        double horizontal = driver1.left_stick_x * 1.1; //  Multiply by 1.1 to negate imperfect strafing
        double pivot = driver1.right_stick_x;

        //Obtains values for each motor through the positions through values
        //from  left joystick which has up/down(vertical) and left/right values(horizontal), and right joystick which has left/right values(pivot)
        double denominator = Math.max(Math.abs(vertical) + Math.abs(horizontal) + Math.abs(pivot), 1);
        double rightFrontPower = (vertical + horizontal + pivot) / denominator * speed;
        double rightBackPower = (vertical - horizontal + pivot) / denominator * speed;
        double LeftFrontPower = (vertical - horizontal - pivot) / denominator * speed;
        double LeftBackPower = (vertical + horizontal - pivot) / denominator * speed;

        front_left.setPower(LeftFrontPower);
        front_right.setPower(rightFrontPower);
        back_left.setPower(LeftBackPower);
        back_right.setPower(rightBackPower);


    }

    public void driveForTime(double seconds, double power) {
        if (!(opMode instanceof LinearOpMode)) return;
        LinearOpMode linearOpMode = (LinearOpMode) opMode;

        setAllPower(power);
        runtime.reset();

        while (linearOpMode.opModeIsActive() && runtime.seconds() < seconds) {
            opMode.telemetry.addData("Driving", "%.1f sec", runtime.seconds());
            opMode.telemetry.update();
        }

        stop();
    }

    public void strafeForTime(double seconds, double power) {
        if (!(opMode instanceof LinearOpMode)) return;
        LinearOpMode linearOpMode = (LinearOpMode) opMode;

        // Mecanum strafing directions
        front_left.setPower(power);
        front_right.setPower(-power);
        back_left.setPower(-power);
        back_right.setPower(power);

        runtime.reset();
        while (linearOpMode.opModeIsActive() && runtime.seconds() < seconds) {
            opMode.telemetry.addData("Strafing", "%.1f sec", runtime.seconds());
            opMode.telemetry.update();
        }

        stop();
    }




    public void turnForTime(double seconds, double power) {
        if (!(opMode instanceof LinearOpMode)) return;
        LinearOpMode linearOpMode = (LinearOpMode) opMode;

        // Turning = left side opposite of right
        front_left.setPower(power);
        back_left.setPower(power);
        front_right.setPower(-power);
        back_right.setPower(-power);

        runtime.reset();
        while (linearOpMode.opModeIsActive() && runtime.seconds() < seconds) {
            opMode.telemetry.addData("Turning", "%.1f sec", runtime.seconds());
            opMode.telemetry.update();
        }

        stop();
    }

    public void diagonalForTime(double seconds, double power, boolean forwardRight) {
        if (!(opMode instanceof LinearOpMode)) return;
        LinearOpMode linearOpMode = (LinearOpMode) opMode;

        // Forward-right: front_left + back_right
        if (forwardRight) {
            front_left.setPower(power);
            back_right.setPower(power);
            front_right.setPower(0);
            back_left.setPower(0);
        } else {
            // Forward-left: front_right + back_left
            front_right.setPower(power);
            back_left.setPower(power);
            front_left.setPower(0);
            back_right.setPower(0);
        }

        runtime.reset();
        while (linearOpMode.opModeIsActive() && runtime.seconds() < seconds) {
            opMode.telemetry.addData("Diagonal Move", "%.1f sec", runtime.seconds());
            opMode.telemetry.update();
        }

        stop();
    }

    public void stop() {
        setAllPower(0);
    }

    private void setAllPower(double power) {
        front_left.setPower(power);
        front_right.setPower(power);
        back_left.setPower(power);
        back_right.setPower(power);
    }
    
    public void autoSetUp() {
        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        front_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        front_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        back_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }
    
    public void autoTelemetry(OpMode opMode)
    {
        opMode.telemetry.addData("Starting at",  "%7d :%7d",
                front_left.getCurrentPosition(),
                front_right.getCurrentPosition());
        opMode.telemetry.update();
    }
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                              double COUNTS_PER_INCH) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the OpMode is still active

            // Determine new target position, and pass to motor controller
            newLeftTarget = front_left.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = front_right.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            front_left.setTargetPosition(newLeftTarget);
            front_right.setTargetPosition(newRightTarget);

            newLeftTarget = back_left.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = back_right.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            back_left.setTargetPosition(newLeftTarget);
            back_right.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            front_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            front_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            back_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            back_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            front_left.setPower(Math.abs(speed));
            front_right.setPower(Math.abs(speed));
            back_left.setPower(Math.abs(speed));
            back_right.setPower(Math.abs(speed));

            // Stop all motion;
            front_left.setPower(0);
            front_right.setPower(0);
            back_left.setPower(0);
            back_right.setPower(0);

            // Turn off RUN_TO_POSITION
            front_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            front_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            back_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            back_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

          //  sleep(250);
            // optional pause after each move.

    }
}
