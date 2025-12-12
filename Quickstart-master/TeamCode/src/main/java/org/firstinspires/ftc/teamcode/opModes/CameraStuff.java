package org.firstinspires.ftc.teamcode.opModes;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
@Disabled
@TeleOp(name="Camera Test")
public class CameraStuff extends LinearOpMode {

    private VisionPortal visionPortal;

    private AprilTagProcessor aprilTag;
    @Override
    public void runOpMode() {
        VisionPortal.Builder builder = new VisionPortal.Builder();

        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        builder.addProcessor(aprilTag);
        visionPortal = builder.build();

        telemetry.update();
        waitForStart();

        if(opModeIsActive()) {
            while(opModeIsActive()) {
                ArrayList<AprilTagDetection> current = aprilTag.getDetections();
                telemetry.addData("# April Tags Detected", current.size());

                if(gamepad1.dpad_down) {
                    visionPortal.stopStreaming();

                }else if (gamepad1.dpad_up) {
                    visionPortal.resumeStreaming();
                }
                telemetry.update();
            }
        }

        sleep(20);
    }
}
