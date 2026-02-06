package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDriveTest;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
import org.firstinspires.ftc.teamcode.subsystems.Sorters;

@TeleOp(name="Main TeleOp")
public class MainTeleOp extends OpMode {

    private MecanumDriveTest mecanumDrive;
    private Outtake outTake;
    private Intake intake;

    private Sorters sort;

    @Override

    public void init() {
        intake = new Intake(this);
        outTake = new Outtake(this);
        mecanumDrive = new MecanumDriveTest(this);
        sort = new Sorters(this);


    }

    @Override
    public void loop() {
        mecanumDrive.mecanumDrive();
        outTake.teleOp();
        sort.teleOp();
        intake.teleOp();
        sort.addTelemetry(this);

    }

}
