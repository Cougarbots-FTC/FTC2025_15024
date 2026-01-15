package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(name = "Webcam Test")
public class SampleTeleOp  extends LinearOpMode {
    private VisionPortal myVisionPortal;


    @Override
    public void runOpMode() throws InterruptedException {
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1"); //Check congif
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(webcamName);
        myVisionPortal = builder.build();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // add vision processing code here if needed
            telemetry.addData("Status", "Running");
            telemetry.update();
        }

        // Close the VisionPortal when done
        myVisionPortal.close();
    }
}
