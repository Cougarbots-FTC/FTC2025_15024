package org.firstinspires.ftc.teamcode.opModes;

import static android.os.SystemClock.sleep;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;


@Autonomous(name = "Pedro Auto Test")
public class PedroAuto extends OpMode {
    private Follower follower;
    private Timer pathTimer, opModeTimer;
    private Outtake outtake;

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

    private final Pose startPos = new Pose(56, 8, Math.toRadians(90));
    private final Pose shootPosition = new Pose(60,9, Math.toRadians(113));
    private final Pose firstSpike = new Pose(40.3, 31, Math.toRadians(0));
    private final Pose collectFirstSpike = new Pose(17, 31);
    private final Pose endPose = new Pose(60, 35, Math.toRadians(113));

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
                .addPath(new BezierLine(shootPosition, firstSpike))
                .setLinearHeadingInterpolation(shootPosition.getHeading(), firstSpike.getHeading())
                .build();

        driveFirstSpikeCollect = follower.pathBuilder()
                .addPath(new BezierLine(firstSpike, collectFirstSpike))
                .setLinearHeadingInterpolation(firstSpike.getHeading(), collectFirstSpike.getHeading())
                .build();

        driveCollectShoot = follower.pathBuilder()
                .addPath(new BezierLine(collectFirstSpike, shootPosition))
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
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 0.5) {
                    follower.followPath(driveShootFirstSpike, true);
                    setPathState(PathState.DRIVE_FIRSTSPIKE_COLLECT);
                    //intake.turnOnIntake(1); //maybe move if it doesn't turn on soon enough
                }
                break;
            case DRIVE_FIRSTSPIKE_COLLECT:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 5) {
                    follower.setMaxPower(0.4);
                    follower.followPath(driveFirstSpikeCollect, true);
                    setPathState(PathState.DRIVE_COLLECT_SHOOTPOS);
                }
                break;
            case DRIVE_COLLECT_SHOOTPOS:
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 0.5) {
                    //intake.turnOnIntake(0);
                    follower.followPath(driveCollectShoot, true);
                    setPathState(PathState.SHOOT);
                }
                break;
            case SHOOT:
                //check is the follower done
                if (!follower.isBusy() || pathTimer.getElapsedTimeSeconds() > 5) {
                    setPathState(PathState.DRIVE_SHOOT_FIRSTSPIKE);
                    //TODO add logic for flywheel shooter

                }
                break;
            case DRIVE_SHOOTPOS_ENDPOS:
                if (!follower.isBusy()) {
                    follower.followPath(driveShootPosEndPos, true);
                    setPathState(PathState.DONE);
                  //TODO turn stuff off

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

        pathTimer = new Timer();
        opModeTimer = new Timer();

        buildPath();

        pathState = PathState.DRIVE_STARTPOS_SHOOT_POS;
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