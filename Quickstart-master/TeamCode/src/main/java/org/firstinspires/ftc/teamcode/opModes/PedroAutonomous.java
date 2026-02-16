package org.firstinspires.ftc.teamcode.opModes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;

@Autonomous(name = "Pedro Pathing Autonomous", group = "Autonomous")
@Configurable // Panels
public class PedroAutonomous extends OpMode {
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)
    private Paths paths; // Paths defined in the Paths class

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 8, Math.toRadians(90)));

        paths = new Paths(follower); // Build paths

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void loop() {
        follower.update(); // Update Pedro Pathing
        autonomousPathUpdate(); // Update autonomous state machine

        // Log values to Panels and Driver Station
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.update(telemetry);
    }


    public static class Paths {
        public PathChain FirstShoot;
        public PathChain FirstSpikePath;
        public PathChain PickFirstPath;
        public PathChain FirstSpikeShoot;
        public PathChain SecondSpikePath;
        public PathChain PickSecondPath;
        public PathChain SecondSpikeShoot;
        public PathChain ThirdSpikePath;
        public PathChain PickThirdPath;
        public PathChain ThirdSpikeShoot;

        public Paths(Follower follower) {
            FirstShoot = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(21.346, 123.529),

                                    new Pose(34.304, 112.637)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(144), Math.toRadians(144))

                    .build();

            FirstSpikePath = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(34.304, 112.637),

                                    new Pose(45.000, 84.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(144), Math.toRadians(0))

                    .build();

            PickFirstPath = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(45.000, 84.000),

                                    new Pose(14.849, 83.795)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            FirstSpikeShoot = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(14.849, 83.795),

                                    new Pose(34.445, 112.547)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(144))

                    .build();

            SecondSpikePath = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(34.445, 112.547),

                                    new Pose(48.000, 60.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(144), Math.toRadians(180))

                    .build();

            PickSecondPath = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(48.000, 60.000),

                                    new Pose(15.000, 60.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            SecondSpikeShoot = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(15.000, 60.000),

                                    new Pose(33.000, 114.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(144))

                    .build();

            ThirdSpikePath = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(33.000, 114.000),

                                    new Pose(51.000, 36.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(144), Math.toRadians(180))

                    .build();

            PickThirdPath = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(51.000, 36.000),

                                    new Pose(15.000, 36.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            ThirdSpikeShoot = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(15.000, 36.000),

                                    new Pose(33.000, 114.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(144))

                    .build();
        }
    }


    public void autonomousPathUpdate() {
        // Add your state machine Here
        // Access paths with paths.pathName
        // Refer to the Pedro Pathing Docs (Auto Example) for an example state machine
    }
}