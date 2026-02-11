package org.firstinspires.ftc.teamcode.opModes;

import static android.os.SystemClock.sleep;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Delivery;
//import org.firstinspires.ftc.teamcode.subsystems.Intake24349;
import org.firstinspires.ftc.teamcode.subsystems.FlyWheelPID;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
//import org.firstinspires.ftc.teamcode.subsystems.PIDOuttake;


@Autonomous(name = "Pedro Red Auto")

public class RedPedroAuto extends OpMode {
    private Follower follower;
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
        DONE
    }
    PathState pathState;

    private final Pose startPos = new Pose(21.346, 123.539, Math.toRadians(144));
    private final Pose shootPosition = new Pose(34.304,112.637, Math.toRadians(144));//robot heading
    private final Pose firstSpike = new Pose(45.000, 84.000, Math.toRadians(180)); //new Pose(96, 38.3, Math.toRadians(90));
    private final Pose collectFirstSpike = new Pose(14.849, 83.795); //new Pose(131, 38.3);
    private final Pose endPose = new Pose(21, 105, Math.toRadians(65));

    private PathChain driveStartPosShootPos, driveShootPosEndPos, driveShootFirstSpike, driveFirstSpikeCollect, driveCollectShoot;

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
                .setReversed()
                .build();

        driveCollectShoot = follower.pathBuilder()
                .addPath(new BezierCurve(collectFirstSpike, shootPosition))
                .setLinearHeadingInterpolation(collectFirstSpike.getHeading(), shootPosition.getHeading())
                .build();
    }

    public void startPathUpdate() {
        switch (pathState) {
            case DRIVE_STARTPOS_SHOOT_POS:
                follower.followPath(driveStartPosShootPos, true);
                setPathState(PathState.SHOOT); //reset the timer and make a new state
                break;
            case DRIVE_SHOOT_FIRSTSPIKE:
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 5) {
                    follower.followPath(driveShootFirstSpike, true);
                    setPathState(PathState.DRIVE_FIRSTSPIKE_COLLECT);
                    intake.intakeOn(1); //maybe move if it doesn't turn on soon enough
                }
                break;
            case DRIVE_FIRSTSPIKE_COLLECT:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 3) {
                    follower.setMaxPower(0.4);
                    follower.followPath(driveFirstSpikeCollect, true);
                    setPathState(PathState.DRIVE_COLLECT_SHOOTPOS);
                }
                break;
            case DRIVE_COLLECT_SHOOTPOS:
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 5) {
                    follower.setMaxPower(0.7);
                    intake.intakeOff();
                    follower.followPath(driveCollectShoot, true);
                    setPathState(PathState.SHOOT);
                }
                break;
            case SHOOT:
                //check is the follower done
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 3) {
                    shootCount++;
                    outtake.setPower(.80);
                    if (shootCount == 2) {
                        setPathState(PathState.DRIVE_SHOOTPOS_ENDPOS);
                    } else {
                        setPathState(PathState.DRIVE_SHOOT_FIRSTSPIKE);
                    }
                    outtake.setPower(.80);
                    /*
                    //TODO add logic for flywheel shooter
                    sleep(4000);//4500
                    delivery.turnOnDelivery(-1);
                    sleep(2500);//3000
                    intake.turnOnIntake(1);
                    sleep(3000);
                    telemetry.addLine("Done Path 1");

                    delivery.turnOnDelivery(0);
                    intake.turnOnIntake(0);
                    outtake.turnOffOuttake();


                     */
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

        follower.setPose(startPos);

        outtake = new Outtake(this);
        intake = new Intake(this);
// = new Delivery(this);

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