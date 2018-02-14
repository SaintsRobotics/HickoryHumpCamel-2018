package com.saintsrobotics.hickoryhumpcamel;

import com.github.dozer.TaskRobot;
import com.github.dozer.coroutine.Task;
import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.coroutine.helpers.RunSequentialTask;
import com.saintsrobotics.hickoryhumpcamel.input.OI;
import com.saintsrobotics.hickoryhumpcamel.input.Sensors;
import com.saintsrobotics.hickoryhumpcamel.input.TestSensors;
import com.saintsrobotics.hickoryhumpcamel.output.*;
import com.saintsrobotics.hickoryhumpcamel.tasks.auton.ForwardAtHeadingTask;
import com.saintsrobotics.hickoryhumpcamel.tasks.auton.SimpleMoveForward;
import com.saintsrobotics.hickoryhumpcamel.tasks.auton.TurnToHeadingTask;
import com.saintsrobotics.hickoryhumpcamel.tasks.teleop.ArcadeDrive;
import com.saintsrobotics.hickoryhumpcamel.tasks.teleop.LiftTaskWithMotor;
import com.saintsrobotics.hickoryhumpcamel.util.PIDConfiguration;
import com.saintsrobotics.hickoryhumpcamel.util.UpdateMotors;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the IterativeRobot documentation. If you change the name of this class
 * or the package after creating this project, you must also update the manifest file in the
 * resource directory.
 */
public class Robot extends TaskRobot {

  public RobotMotors motors;
  public Servos servos;
  public OI oi;
  public Flags flags;
  public Sensors sensors;
  public PIDConfiguration forwardPidConfig;
  public PIDConfiguration turnPidConfig;

  public static Robot instance;

  @Override
  public void robotInit() {
    Robot.instance = this;
    this.oi = new OI();

    this.motors = new TestBotMotors();
    this.motors.init();
    this.flags = new Flags();
    this.sensors = new TestSensors();
    this.sensors.init();
    this.forwardPidConfig = new PIDConfiguration(this.sensors.gyro, this.sensors.average);
    this.turnPidConfig = new PIDConfiguration(this.sensors.gyro);
  }

  @Override
  public void autonomousInit() {
    this.sensors.leftEncoder.reset();
    this.sensors.rightEncoder.reset();
    this.sensors.gyro.reset();
    this.autonomousTasks = new Task[] {
        new RunSequentialTask(
            new ForwardAtHeadingTask(0, 232 * 2, this.forwardPidConfig),
            new TurnToHeadingTask(45, this.turnPidConfig),
            new ForwardAtHeadingTask(0, 232 * 3, this.forwardPidConfig),
            new TurnToHeadingTask(-45, this.turnPidConfig),
            new ForwardAtHeadingTask(0, 232 * 4.486, this.forwardPidConfig)
        )
    };
    super.autonomousInit();
  }

  @Override
  public void teleopInit() {
    this.teleopTasks = new Task[] {new ArcadeDrive(),/*, new LiftTaskWithMotor()*/ new RunEachFrameTask() {

      @Override
      protected void runEachFrame() {
        SmartDashboard.putNumber("Right X", Robot.instance.motors.rightDrive.get());
        SmartDashboard.putNumber("left X", Robot.instance.motors.leftDrive.get());
        SmartDashboard.putNumber("drive diff", Robot.instance.motors.leftDrive.get() - Robot.instance.motors.rightDrive.get());
        
      }
      
    }
    };
    super.teleopInit();
  }
}
