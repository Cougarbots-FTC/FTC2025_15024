package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDriveTest;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
import org.firstinspires.ftc.teamcode.subsystems.Sorters;

@Autonomous(name="Bum Auto", group="Autonomous")
public class BumAuto extends LinearOpMode {

    private MecanumDriveTest mecanumDrive;
    private Sorters sort;
    private Intake intake;

    private Outtake out;

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

            mecanumDrive.driveForTime(300,1);
            telemetry.addLine("Auto Complete");
            telemetry.update();
        }
    }
}
