package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDriveTest;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
import org.firstinspires.ftc.teamcode.subsystems.Sorters;

@Autonomous(name="Blue Side Auto", group="Autonomous")
public class BlueSideAuto extends LinearOpMode {

    private MecanumDriveTest mecanumDrive;
    private Sorters sort;

    private Outtake out;
    private Intake intake;

    @Override
    public void runOpMode() {
        // Initialize your drive system after LinearOpMode is ready
        mecanumDrive = new MecanumDriveTest(this);
        sort = new Sorters(this);
        out = new Outtake(this);
        intake = new Intake(this);


        telemetry.addLine("Initialized and Ready");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            mecanumDrive.driveForTime(250,-0.75);
            out.setPower(-0.9);
            sleep(5000);
            intake.intakeOn(-1);
            sort.openGates();
            sleep(5000);
            out.setPower(0);
            intake.intakeOff();
            sort.closeGates();
            //mecanumDrive.driveForTime(500, -0.75);   // Drive forward 1.5 sec/mecanumDrive.turnForTime(0.8, 0.4);    // Turn right 0.8 sec
            //sort.closeGates();
            mecanumDrive.strafeForTime(500, -1);  // Strafe right 1.2 sec
            sleep(1500);
            mecanumDrive.stop();

            telemetry.addLine("Auto Complete");
            telemetry.update();
        }
    }
}
