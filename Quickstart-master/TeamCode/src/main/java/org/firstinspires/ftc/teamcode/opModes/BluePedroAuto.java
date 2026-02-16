package org.firstinspires.ftc.teamcode.opModes;

import static android.os.SystemClock.sleep;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Delivery;
//import org.firstinspires.ftc.teamcode.subsystems.Intake24349;
import org.firstinspires.ftc.teamcode.subsystems.FlyWheelPID;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
import org.firstinspires.ftc.teamcode.subsystems.Sorters;
//import org.firstinspires.ftc.teamcode.subsystems.PIDOuttake;


@Autonomous(name = "Pedro Blue Auto")

public class BluePedroAuto extends OpMode {

    private Follower follower;
    private CRServo lebronA;

    private CRServo lebronB;

    private Delivery delivery;

    private Timer pathTimer, opModeTimer;
    private Outtake outtake;

    private Intake intake;
 //   private Delivery delivery;
    private int shootCount;

    public enum PathState {
        DRIVE_STARTPOS_SHOOT_POS,
        SHOOT,
        DRIVE_SHOOT_FIRSTSPIKE,
        DRIVE_FIRSTSPIKE_COLLECT,
        DRIVE_COLLECT_SHOOTPOS,
        DRIVE_SHOOTPOS_ENDPOS,
        DRIVE_SECONDSPIKE_COLLECT, DRIVE_SHOOT_SECONDSPIKE, DRIVE_COLLECT_SHOOTPOSSECOND, DONE
    }
    PathState pathState;

    private final Pose startPos = new Pose(21.346, 123.539, Math.toRadians(144));
    private final Pose shootPosition = new Pose(34.304,112.637, Math.toRadians(144));//robot heading
    private final Pose firstSpike = new Pose(45.000, 84.000, Math.toRadians(180)); //new Pose(96, 38.3, Math.toRadians(90));
    private final Pose collectFirstSpike = new Pose(18, 84.795); //new Pose(131, 38.3);

    private final Pose secondSpike = new Pose(48,64,Math.toRadians(180));
    private final Pose collectSecondSpike =  new Pose(20,60);
    private final Pose endPose = new Pose(21, 105, Math.toRadians(65));

    private PathChain driveStartPosShootPos, driveShootPosEndPos, driveShootFirstSpike, driveFirstSpikeCollect, driveCollectShoot, driveSecondSpikeCollect,driveShootSecondSpike, driveCollectShootSecond;

    public void buildPath() {
        driveStartPosShootPos = follower.pathBuilder()
                .addPath(new BezierLine(startPos, shootPosition))
                .setLinearHeadingInterpolation(startPos.getHeading(), shootPosition.getHeading())
                .build();

        driveShootPosEndPos = follower.pathBuilder()
                .addPath(new BezierLine(shootPosition, endPose))
                .setLinearHeadingInterpolation(shootPosition.getHeading(), endPose.getHeading())
                .build();

        driveShootFirstSpike = follower.pathBuilder()
                .addPath(new BezierCurve(shootPosition, firstSpike))
                .setLinearHeadingInterpolation(shootPosition.getHeading(), firstSpike.getHeading())
                .build();

        driveFirstSpikeCollect = follower.pathBuilder()
                .addPath(new BezierLine(firstSpike, collectFirstSpike))
                .setTangentHeadingInterpolation()
                .build();
        driveShootSecondSpike = follower.pathBuilder()
                .addPath(new BezierCurve(shootPosition,secondSpike))
                .setLinearHeadingInterpolation(shootPosition.getHeading(), secondSpike.getHeading())
                .build();
        driveSecondSpikeCollect = follower.pathBuilder()
                .addPath(new BezierLine(secondSpike,collectSecondSpike))
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

    public void startPathUpdate() {
        switch (pathState) {
            case DRIVE_STARTPOS_SHOOT_POS:
                follower.followPath(driveStartPosShootPos, true);
                setPathState(PathState.SHOOT); //reset the timer and make a new state

                lebronA.setPower(-1);
                lebronB.setPower(1);
                break;
            case DRIVE_SHOOT_FIRSTSPIKE:
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 4) {
                    follower.followPath(driveShootFirstSpike, true);
                    setPathState(PathState.DRIVE_FIRSTSPIKE_COLLECT);
                    lebronA.setPower(-1);
                    lebronB.setPower(1);
                    intake.intakeOn(-1); //maybe move if it doesn't turn on soon enough

                }
                break;
            case DRIVE_FIRSTSPIKE_COLLECT:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 2) {
                    follower.setMaxPower(0.8);
                    follower.followPath(driveFirstSpikeCollect, true);
                    setPathState(PathState.DRIVE_COLLECT_SHOOTPOS);
                    lebronA.setPower(-1);
                    lebronB.setPower(1);
                }
                break;
            case DRIVE_SHOOT_SECONDSPIKE:
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 4) {
                    follower.followPath(driveShootSecondSpike, true);
                    setPathState(PathState.DRIVE_SECONDSPIKE_COLLECT);
                    lebronA.setPower(-1);
                    lebronB.setPower(1);
                    intake.intakeOn(-1); //maybe move if it doesn't turn on soon enough

                }
                break;
            case DRIVE_SECONDSPIKE_COLLECT:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 2) {
                    follower.setMaxPower(0.8);
                    follower.followPath(driveSecondSpikeCollect, true);
                    setPathState(PathState.DRIVE_COLLECT_SHOOTPOS);
                    lebronA.setPower(-1);
                    lebronB.setPower(1);
                    intake.intakeOn(-1);


                }
                break;
            case DRIVE_COLLECT_SHOOTPOS:
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 3) {
                    follower.setMaxPower(0.8);
                    intake.intakeOff();
                    follower.followPath(driveCollectShoot, true);
                    setPathState(PathState.SHOOT);

                    lebronA.setPower(1);
                    lebronB.setPower(-1);
                }
            case DRIVE_COLLECT_SHOOTPOSSECOND:
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 3) {
                    follower.setMaxPower(0.8);
                    follower.followPath(driveCollectShootSecond, true);
                    setPathState(PathState.SHOOT);
                    intake.intakeOn(-1);
                    lebronA.setPower(1);
                    lebronB.setPower(-1);
                }
                break;

            case SHOOT:
                //check is the follower done
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 4) {
                    if (shootCount == 1) {

                        lebronA.setPower(1);
                        lebronB.setPower(-1);
                        sleep(250);
                        intake.intakeOn(-1);
                        delivery.deliveryOn(.80);
                        sleep(4000);
                        lebronA.setPower(0);
                        lebronB.setPower(0);
                        intake.intakeOn(0);
                        delivery.deliveryOff();
                        shootCount++;

                    }
                    else if (shootCount == 0){

                        lebronA.setPower(1);
                        lebronB.setPower(-1);

                        outtake.setPower(-.8);
                        sleep(1500);
                        intake.intakeOn(-1);
                        delivery.deliveryOn(.80);
                        sleep(4000);
                        lebronA.setPower(0);
                        lebronB.setPower(0);
                        intake.intakeOn(0);
                        delivery.deliveryOff();
                        shootCount++;
                    } else if (shootCount == 2) {

                         lebronA.setPower(1);
                         lebronB.setPower(-1);
                         sleep(250);
                         intake.intakeOn(-1);
                         delivery.deliveryOn(.75);
                         sleep(4000);
                         lebronA.setPower(0);
                         lebronB.setPower(0);
                         intake.intakeOn(0);
                         delivery.deliveryOff();
                         shootCount++;

                     }
                     if (shootCount == 3){

                         setPathState(PathState.DRIVE_SHOOTPOS_ENDPOS);
                     }

                     else if (shootCount == 1){

                         setPathState(PathState.DRIVE_SHOOT_FIRSTSPIKE);
                    } else if (shootCount == 2) {
                         setPathState(PathState.DRIVE_SECONDSPIKE_COLLECT);
                     }






                }
                break;
            case DRIVE_SHOOTPOS_ENDPOS:
                if (!follower.isBusy()) {
                    follower.followPath(driveShootPosEndPos, true);
                    setPathState(PathState.DONE);
                    outtake.setPower(0);
                    intake.intakeOff();

                }
                break;
            case DONE:
                if (!follower.isBusy()) {
                    telemetry.addLine("All paths done");
                }
            default:
                telemetry.addLine("No State Commanded");
                break;
        }
    }

    public void setPathState(PathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
    }
    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);

        lebronA = hardwareMap.get(CRServo.class, "push1");
        lebronB = hardwareMap.get(CRServo.class, "push2");

        follower.setPose(startPos);

        outtake = new Outtake(this);
        intake = new Intake(this);
        delivery = new Delivery(this);
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


    public void loop() {
        follower.update();
        startPathUpdate();

        telemetry.addData("Pose", follower.getPose());
        telemetry.update();

    }
}