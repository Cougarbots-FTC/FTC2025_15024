package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Disabled
public class Sorters {

    private final Servo servoA;
    private final Servo servoB;

    private final OpMode opMode;

    private boolean servoAState = false;
    private boolean servoBState = false;

    // For button debounce (to avoid multiple toggles per press)
    private boolean prevAButton = false;
    private boolean prevBButton = false;

    public double GATEAOPEN = 0.0;
    public double GATEACLOSE = 1.0;

    public double GATEBOPEN = 0.0;

    public double GATEBCLOSE = 1.0;


    public Sorters(OpMode opMode) {
        this.opMode = opMode;

        HardwareMap hardwareMap = opMode.hardwareMap;
        servoA = hardwareMap.get(Servo.class, "push1");
        servoB = hardwareMap.get(Servo.class, "push2");

    }


    public void teleOp() {
        boolean aPressed = opMode.gamepad1.a;
        boolean bPressed = opMode.gamepad1.b;

        // --- Servo A toggle on A button ---
        if (aPressed && !prevAButton) {
            servoAState = !servoAState;
            servoA.setPosition(servoAState ? 1.0 : 0.0);
        }

        // --- Servo B toggle on B button ---
        if (bPressed && !prevBButton) {
            servoBState = !servoBState;
            servoB.setPosition(servoBState ? 1.0 : 0.0);
        }

        // Update previous button states
        prevAButton = aPressed;
        prevBButton = bPressed;

        // Optional telemetry
        opMode.telemetry.addData("Servo A Pos", servoA.getPosition());
        opMode.telemetry.addData("Servo B Pos", servoB.getPosition());
    }

    public void openGateA(){
        servoA.setPosition(GATEAOPEN);
    }

    public void openGateB(){
        servoB.setPosition(GATEBOPEN);
    }

    public void closeGateA(){
        servoA.setPosition(GATEACLOSE);
    }

    public void closeGateB(){
        servoB.setPosition(GATEBCLOSE);
    }

    public void openGates(){
        openGateA();
        openGateB();
    }

    public void closeGates(){
        closeGateA();
        closeGateB();
    }

}
