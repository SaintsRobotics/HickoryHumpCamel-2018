package com.saintsrobotics.hickoryhumpcamel.output;

import edu.wpi.first.wpilibj.SpeedController;
import com.github.dozer.output.Motor;

public class MotorRamping implements Motor{
  public static final double MOTOR_RAMPING = 0.3;

  private SpeedController speedController;

  public MotorRamping(SpeedController speedController, boolean inverted) {
    this.speedController = speedController;
    this.speedController.setInverted(inverted);
  }

  private double setpoint = 0;
  private double current = 0;

  public double get(){
    return speedController.get();
  }


  public void set(double speed) {
    setpoint = speed;
  }

  public void stop() {
    speedController.stopMotor();
    setpoint = 0;
    current = 0;
  }

  public void update() {
    if (Math.abs(setpoint - current) < MOTOR_RAMPING) {
      current = setpoint;
    } else if (setpoint > current) {
      current += MOTOR_RAMPING;
    } else if (setpoint < current) {
      current -= MOTOR_RAMPING;
    }
    speedController.set(current);
  }
}
