package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class Sorters {

    private final CRServo servoA;
    private final CRServo servoB;

    private final OpMode opMode;

    private boolean servoAState = false;
    private boolean servoBState = false;

    // For button debounce (to avoid multiple toggles per press)
    private boolean prevYButton = false;
    private boolean prevAButton = false;

    public Sorters(OpMode opMode) {
        this.opMode = opMode;

        HardwareMap hardwareMap = opMode.hardwareMap;
        servoA = hardwareMap.get(CRServo.class, "push1");
        servoB = hardwareMap.get(CRServo.class, "push2");


    }


    public void teleOp() {
        boolean yPressed = opMode.gamepad2.y;
        boolean aPressed = opMode.gamepad2.a;

        // --- Servo A and Servo B toggle forward on Y button ---
        if (yPressed && !prevYButton) {
            servoAState = !servoAState;
            servoBState = !servoBState;

            servoA.setPower(servoAState ? -1.0 : 0.0);
            servoB.setPower(servoBState ? 1.0 : 0.0);
        }

        // --- Servo A toggle backwards A button ---
        if (aPressed && !prevAButton) {
            servoAState = !servoAState;
            servoBState = !servoBState;

            servoA.setPower(servoAState ? 1.0 : 0.0);
            servoB.setPower(servoBState ? -1.0 : 0.0);



        }



        // Update previous button states
        prevYButton = yPressed;
        prevAButton = aPressed;

    }

    public void addTelemetry(OpMode opMode) {

        // Optional telemetry
        opMode.telemetry.addData("Servo A Pos", servoA.getPower());
        opMode.telemetry.addData("Servo B Pos", servoB.getPower());
    }

    public void turnOnGateA(){
        servoA.setPower(1);
    }

    public void turnOnGateB(){
        servoB.setPower(-1);
    }

    public void turnOffGateA(){
        servoA.setPower(0);
    }

    public void turnOffGateB(){
        servoB.setPower(0);
    }

    public void openGates(){
        turnOnGateA();
        turnOnGateB();
    }

    public void closeGates(){
        turnOffGateA();
        turnOffGateB();
    }

}
