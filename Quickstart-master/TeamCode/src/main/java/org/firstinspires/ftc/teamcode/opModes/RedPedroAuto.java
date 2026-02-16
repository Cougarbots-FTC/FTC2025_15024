package org.firstinspires.ftc.teamcode.opModes;

import static android.os.SystemClock.sleep;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Delivery;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;

@Autonomous(name = "Pedro Red Auto")

public class RedPedroAuto extends OpMode {

    private Follower follower;
    private CRServo lebronA, lebronB;

    private Delivery delivery;
    private Outtake outtake;
    private Intake intake;

    private Timer pathTimer, opModeTimer;
    private int shootCount;

    public enum PathState {
        DRIVE_STARTPOS_SHOOT_POS,
        SHOOT,
        DRIVE_SHOOT_FIRSTSPIKE,
        DRIVE_FIRSTSPIKE_COLLECT,
        DRIVE_COLLECT_SHOOTPOS,
        DRIVE_SHOOT_SECONDSPIKE,
        DRIVE_SECONDSPIKE_COLLECT,
        DRIVE_COLLECT_SHOOTPOSSECOND,
        DRIVE_SHOOTPOS_ENDPOS,
        DONE
    }

    private PathState pathState;

    // --------- FIELD POSES ---------

    private final Pose startPos = new Pose(120, 123, Math.toRadians(37));
    private final Pose shootPosition = new Pose(110, 114, Math.toRadians(37));

    private final Pose firstSpike = new Pose(90, 84, Math.toRadians(0));
    private final Pose controlFirst = new Pose(96, 114);
    private final Pose collectFirstSpike = new Pose(120, 84);

    private final Pose secondSpike = new Pose(90, 60, Math.toRadians(0));
    private final Pose controlSecond = new Pose(90, 96);
    private final Pose collectSecondSpike = new Pose(110, 60);

    private final Pose endPose = new Pose(122, 105, Math.toRadians(37));

    // --------- PATHS ---------

    private PathChain driveStartPosShootPos;
    private PathChain driveShootPosEndPos;
    private PathChain driveShootFirstSpike;
    private PathChain driveFirstSpikeCollect;
    private PathChain driveCollectShoot;
    private PathChain driveShootSecondSpike;
    private PathChain driveSecondSpikeCollect;
    private PathChain driveCollectShootSecond;

    private void buildPath() {

        driveStartPosShootPos = follower.pathBuilder()
                .addPath(new BezierLine(startPos, shootPosition))
                .setLinearHeadingInterpolation(startPos.getHeading(), shootPosition.getHeading())
                .build();

        driveShootPosEndPos = follower.pathBuilder()
                .addPath(new BezierLine(shootPosition, endPose))
                .setLinearHeadingInterpolation(shootPosition.getHeading(), endPose.getHeading())
                .build();

        driveShootFirstSpike = follower.pathBuilder()
                .addPath(new BezierCurve(shootPosition, controlFirst, firstSpike))
                .setLinearHeadingInterpolation(shootPosition.getHeading(), firstSpike.getHeading())
                .build();

        driveFirstSpikeCollect = follower.pathBuilder()
                .addPath(new BezierLine(firstSpike, collectFirstSpike))
                .setTangentHeadingInterpolation()
                .build();

        driveShootSecondSpike = follower.pathBuilder()
                .addPath(new BezierCurve(shootPosition, controlSecond, secondSpike))
                .setTangentHeadingInterpolation()
                .build();

        driveSecondSpikeCollect = follower.pathBuilder()
                .addPath(new BezierLine(secondSpike, collectSecondSpike))
                .setTangentHeadingInterpolation()
                .build();

        driveCollectShoot = follower.pathBuilder()
                .addPath(new BezierCurve(collectFirstSpike, shootPosition))
                .setLinearHeadingInterpolation(collectFirstSpike.getHeading(), shootPosition.getHeading())
                .build();

        driveCollectShootSecond = follower.pathBuilder()
                .addPath(new BezierCurve(collectSecondSpike, shootPosition))
                .setLinearHeadingInterpolation(collectSecondSpike.getHeading(), shootPosition.getHeading())
                .build();
    }

    // --------- STATE MACHINE ---------

    private void startPathUpdate() {

        switch (pathState) {

            case DRIVE_STARTPOS_SHOOT_POS:
                follower.followPath(driveStartPosShootPos, true);
                setPathState(PathState.SHOOT);
                break;

            case DRIVE_SHOOT_FIRSTSPIKE:
                if (!follower.isBusy()) {
                    follower.followPath(driveShootFirstSpike, true);
                    intake.intakeOn(-1);
                    setPathState(PathState.DRIVE_FIRSTSPIKE_COLLECT);
                }
                break;

            case DRIVE_FIRSTSPIKE_COLLECT:
                if (!follower.isBusy()) {
                    follower.followPath(driveFirstSpikeCollect, true);
                    setPathState(PathState.DRIVE_COLLECT_SHOOTPOS);
                }
                break;

            case DRIVE_COLLECT_SHOOTPOS:
                if (!follower.isBusy()) {
                    intake.intakeOff();
                    follower.followPath(driveCollectShoot, true);
                    setPathState(PathState.SHOOT);
                }
                break;

            case DRIVE_SHOOT_SECONDSPIKE:
                if (!follower.isBusy()) {
                    follower.followPath(driveShootSecondSpike, true);
                    intake.intakeOn(-1);
                    setPathState(PathState.DRIVE_SECONDSPIKE_COLLECT);
                }
                break;

            case DRIVE_SECONDSPIKE_COLLECT:
                if (!follower.isBusy()) {
                    follower.followPath(driveSecondSpikeCollect, true);
                    setPathState(PathState.DRIVE_COLLECT_SHOOTPOSSECOND);
                }
                break;

            case DRIVE_COLLECT_SHOOTPOSSECOND:
                if (!follower.isBusy()) {
                    intake.intakeOff();
                    follower.followPath(driveCollectShootSecond, true);
                    setPathState(PathState.SHOOT);
                }
                break;

            case SHOOT:
                if (!follower.isBusy()) {
                    shootSequence();
                }
                break;

            case DRIVE_SHOOTPOS_ENDPOS:
                if (!follower.isBusy()) {
                    follower.followPath(driveShootPosEndPos, true);
                    setPathState(PathState.DONE);
                }
                break;

            case DONE:
                telemetry.addLine("Auto Complete");
                break;
        }
    }

    // --------- SHOOT LOGIC ---------

    private void shootSequence() {

        lebronA.setPower(1);
        lebronB.setPower(-1);

        outtake.setPower(-0.8);
        sleep(1500);

        intake.intakeOn(-1);
        delivery.deliveryOn(0.8);
        sleep(4000);

        lebronA.setPower(0);
        lebronB.setPower(0);
        intake.intakeOff();
        delivery.deliveryOff();

        shootCount++;

        if (shootCount == 1) {
            setPathState(PathState.DRIVE_SHOOT_FIRSTSPIKE);
        } else if (shootCount == 2) {
            setPathState(PathState.DRIVE_SHOOT_SECONDSPIKE);
        } else {
            setPathState(PathState.DRIVE_SHOOTPOS_ENDPOS);
        }
    }

    private void setPathState(PathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
    }

    // --------- OPMODE ---------

    @Override
    public void init() {

        follower = Constants.createFollower(hardwareMap);
        follower.setPose(startPos);

        lebronA = hardwareMap.get(CRServo.class, "push1");
        lebronB = hardwareMap.get(CRServo.class, "push2");

        delivery = new Delivery(this);
        intake = new Intake(this);
        outtake = new Outtake(this);

        pathTimer = new Timer();
        opModeTimer = new Timer();

        buildPath();

        pathState = PathState.DRIVE_STARTPOS_SHOOT_POS;
        shootCount = 0;
    }

    @Override
    public void start() {
        pathTimer.resetTimer();
        opModeTimer.resetTimer();
    }

    @Override
    public void loop() {
        follower.update();
        startPathUpdate();

        telemetry.addData("Pose", follower.getPose());
        telemetry.update();
    }
}
