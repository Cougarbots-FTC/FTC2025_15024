/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.opModes;

import static org.firstinspires.ftc.teamcode.subsystems.MecanumDrive.back_left;
import static org.firstinspires.ftc.teamcode.subsystems.MecanumDrive.back_right;
import static org.firstinspires.ftc.teamcode.subsystems.MecanumDrive.front_left;
import static org.firstinspires.ftc.teamcode.subsystems.MecanumDrive.front_right;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
import org.firstinspires.ftc.teamcode.subsystems.Sorters;



/*
 * This OpMode illustrates the concept of driving a path based on encoder counts.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: RobotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forward, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backward for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This method assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@Autonomous(name="Robot: Auto Drive By Encoder", group="Auto")
public class RobotAutoDriveByEncoder_Linear extends LinearOpMode {

    /* Declare OpMode members. */

    private MecanumDrive mecanumDrive;

    private ElapsedTime runtime = new ElapsedTime();

    // Calculate the COUNTS_PER_INCH for your specific drive train.
    // Go to your motor vendor website to determine your motor's COUNTS_PER_MOTOR_REV
    // For external drive gearing, set DRIVE_GEAR_REDUCTION as needed.
    // For example, use a value of 2.0 for a 12-tooth spur gear driving a 24-tooth spur gear.
    // This is gearing DOWN for less speed and more torque.
    // For gearing UP, use a gear ratio less than 1.0. Note this will affect the direction of wheel rotation.
    static final double COUNTS_PER_MOTOR_REV = 28;
    ;    // 5203 yellow jacket gobilda motor (this is cpr below is ppr)

    static final double DRIVE_GEAR_REDUCTION = 13.7;     // 13.7 : 1

    static final double COUNTS_PER_WHEEL_REV = COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION;

    static final double WHEEL_DIAMETER_INCHES = 4.09449; //5.511811 ;     // gobilda mechanium diameter in inches
    static final double COUNTS_PER_INCH = 32.25; /*(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * Math.PI);
            */

    static final double DRIVE_SPEED = 1;
    static final double TURN_SPEED = 1;
    private Outtake outtake;
    private Sorters sorters;
    private Intake intake;


    @Override
    public void runOpMode() {

        // Initialize the drive system variables.
        mecanumDrive = new MecanumDrive(this);
        mecanumDrive.autoSetUp();

        outtake = new  Outtake(this);
        sorters = new Sorters(this);
        intake = new Intake(this);

        mecanumDrive.autoTelemetry(this);

        // Wait for the game to start (driver presses START)
        waitForStart();
        if (opModeIsActive()) {



            //front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
           // encoderDrive(DRIVE_SPEED, 15 , 15 , 5);
           // telemetry.addData("Ticks moved", front_left.getCurrentPosition());



            outtake.setPower(.85);
            sleep(4500);
            encoderDrive(DRIVE_SPEED, -4, -4, 5.0); //Move back from starting position to shoot 3
            launchThree();  // Shoot preloaded balls
            encoderDrive(DRIVE_SPEED, -16, -16, 5.0); // Reverse enough to get to turn postion
            sleep(250); //rest
            outtake.setPower(.8);
            encoderDrive(TURN_SPEED, 25, -25, 4.0);  // Turn to get into postion for forward movement for first line
            encoderDrive(DRIVE_SPEED, 15,15, 5.0); // forward for first line
            encoderDrive(TURN_SPEED, -15, 15, 4.0);  // Turn to get into postion for picking up line 1 balls
            intake.intakeOn(-1);

            encoderDrive(DRIVE_SPEED, 22,22, 5.0); // forward to pick up balls
            intake.intakeOff();

            encoderDrive(DRIVE_SPEED, -20,-20, 5.0); // backwards to get back to shooting postion
            encoderDrive(TURN_SPEED, -15, 15, 4.0);  // turn to get back into position
            encoderDrive(DRIVE_SPEED, 16,16, 5.0); // forward for first line
            encoderDrive(TURN_SPEED, 8, -8, 4.0);  // Turn to get into postion for forward movement for first line
            encoderDrive(DRIVE_SPEED, 6, 6, 5.0); // Reverse enough to get to turn postion


            launchThree();


            encoderDrive(TURN_SPEED,-45,45,5.0);
            encoderDrive(DRIVE_SPEED, 40, 40, 5.0); // Reverse enough to get to turn postion





        }
        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);  // pause to display final telemetry message.
    }

    /*
     *  Method to perform a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the OpMode running.
     */

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the OpMode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = front_left.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTarget = front_right.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            int newLeftBackTarget = back_left.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            int newRightBackTarget = back_right.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            front_left.setTargetPosition(newLeftTarget);
            front_right.setTargetPosition(newRightTarget);
            back_left.setTargetPosition(newLeftBackTarget);
            back_right.setTargetPosition(newRightBackTarget);

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

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (back_left.isBusy() && back_right.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Running to", " %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Currently at", " at %7d :%7d",
                        front_left.getCurrentPosition(), front_right.getCurrentPosition());
                telemetry.update();
            }

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

            sleep(250);   // optional pause after each move.
        }
    }


    public void launchThree() {




        sorters.closeGates();
        sleep(1000);
        sorters.turnOnGateA();
        sleep(1000);
        intake.intakeAuto(800,-1);
        sleep(1000);
       // sleep(1000);
        sorters.turnOnGateB();
        sleep(1000);
        intake.intakeAuto(1500,-1);
        sorters.closeGates();



    }
}
