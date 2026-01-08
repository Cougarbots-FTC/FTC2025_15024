package org.firstinspires.ftc.teamcode.opModes;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDriveTest;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
import org.firstinspires.ftc.teamcode.subsystems.Sorters;

@Autonomous(name="Two Shot Auto", group="Autonomous")
public class TwoShotAuto extends LinearOpMode {
    private MecanumDriveTest mecanumDrive;
    private Outtake outtake;
    private Sorters sorters;
    private Intake intake;

    public void runOpMode() {
        mecanumDrive = new MecanumDriveTest(this);
        outtake = new  Outtake(this);
        sorters = new Sorters(this);
        intake = new Intake(this);


        waitForStart();

       sorters.closeGates();

        if (opModeIsActive()) {
            //potentially move back an inch
            launchThree();
            //move to collect the next three
            //reverse
            //rotate
            //strafe
            //turn on intake
            //move forward
            //turn off intake
            //reverse
            //go back to goal
            launchThree();


        }
    }

    /**
     * This method should launch the three artifacts the robot is holding
     */
    public void launchThree() {
        /*

        Turn on fly wheel

        Open gate 1
        Run intake
        Close gate 1
        Open gate 2
        Run intake
        Open gate 1

         */
        outtake.startShooters(.90);
        sleep(6000);
        sorters.openGateA();
        sleep(1000);
        intake.intakeAuto(1000,-1);
        sorters.closeGateA();
        sorters.openGateB();
        sleep(1000);
        intake.intakeAuto(3000,-1);
        sorters.closeGates();





    }
}
