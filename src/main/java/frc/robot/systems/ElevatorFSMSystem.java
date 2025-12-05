package frc.robot.systems;

// WPILib Imports

// Third party Hardware Imports

// Robot Imports
import frc.robot.TeleopInput;
import frc.robot.constants.Constants;
import frc.robot.motors.TalonFXWrapper;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RadiansPerSecond;

import org.littletonrobotics.junction.Logger;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.StaticFeedforwardSignValue;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.HardwareMap;

enum FSMState {
	// put states here
	EXAMPLE_STATE
}

public class ElevatorFSMSystem extends FSMSystem<FSMState> {
	/* ======================== Constants ======================== */

	/* ======================== Private variables ======================== */

	// Hardware devices should be owned by one and only one system. They must
	// be private to their owner system and may not be used elsewhere.
	private TalonFXWrapper elevatorMotor;
	private DigitalInput groundLimitSwitch;

	/* ======================== Constructor ======================== */
	/**
	 * Create FSMSystem and initialize to starting state. Also perform any
	 * one-time initialization or configuration of hardware required. Note
	 * the constructor is called only once when the robot boots.
	 */
	public ElevatorFSMSystem() {
		// Perform hardware init using a wrapper class
		// this is so we can see motor outputs during simulatiuons
		elevatorMotor = new TalonFXWrapper(HardwareMap.CAN_ID_TALON_ELEVATOR);

		var talonFXConfigs = new TalonFXConfiguration();

		var outputConfigs = talonFXConfigs.MotorOutput;
		outputConfigs.NeutralMode = NeutralModeValue.Brake;

		// apply sw limit
		var swLimitSwitch = talonFXConfigs.SoftwareLimitSwitch;
		swLimitSwitch.ForwardSoftLimitEnable = true; // enable top limit
		swLimitSwitch.ReverseSoftLimitEnable = true; // enable bottom limit
		swLimitSwitch.ForwardSoftLimitThreshold = Constants.ELEVATOR_UPPER_THRESHOLD
			.in(Inches);
		swLimitSwitch.ReverseSoftLimitThreshold = Inches.of(0).in(Inches);

		var sensorConfig = talonFXConfigs.Feedback;
		sensorConfig.SensorToMechanismRatio = Constants.ELEVATOR_ROTS_TO_INCHES;

		var slot0Configs = talonFXConfigs.Slot0;
		slot0Configs.GravityType = GravityTypeValue.Elevator_Static;
		slot0Configs.kG = Constants.ELEVATOR_KG;
		slot0Configs.kS = Constants.ELEVATOR_KS;
		slot0Configs.kV = Constants.ELEVATOR_KV;
		slot0Configs.kA = Constants.ELEVATOR_KA;
		slot0Configs.kP = Constants.ELEVATOR_KP;
		slot0Configs.kI = Constants.ELEVATOR_KI;
		slot0Configs.kD = Constants.ELEVATOR_KD;
		slot0Configs.StaticFeedforwardSign = StaticFeedforwardSignValue.UseClosedLoopSign;

		elevatorMotor.getConfigurator().apply(talonFXConfigs);

		var motionMagicConfigs = talonFXConfigs.MotionMagic;
		motionMagicConfigs.MotionMagicCruiseVelocity = Constants.ELEVATOR_CRUISE_VELO;
		motionMagicConfigs.MotionMagicAcceleration = Constants.ELEVATOR_TARGET_ACCEL;
		motionMagicConfigs.MotionMagicExpo_kV = Constants.ELEVATOR_EXPO_KV;

		BaseStatusSignal.setUpdateFrequencyForAll(
				Constants.UPDATE_FREQUENCY_HZ,
				elevatorMotor.getPosition(),
				elevatorMotor.getVelocity(),
				elevatorMotor.getAcceleration(),
				elevatorMotor.getMotorVoltage(),
				elevatorMotor.getRotorPosition(),
				elevatorMotor.getRotorVelocity()
		);

		elevatorMotor.optimizeBusUtilization();

		elevatorMotor.setPosition(0);

		groundLimitSwitch = new DigitalInput(HardwareMap.ELEVATOR_GROUND_LIMIT_SWITCH_DIO_PORT);


		// Reset state machine
		reset();
	}

	/* ======================== Public methods ======================== */

	// overridden methods don't require javadocs
	// however, you may want to add implementation specific javadocs

	@Override
	public void reset() {
		setCurrentState(FSMState.EXAMPLE_STATE);

		// Call one tick of update to ensure outputs reflect start state
		update(null);
	}

	@Override
	public void update(TeleopInput input) {
		switch (getCurrentState()) {
			case EXAMPLE_STATE:
				handleExampleState(input);
				break;

			default:
				throw new IllegalStateException("Invalid state: " + getCurrentState().toString());
		}
		setCurrentState(nextState(input));

		Logger.recordOutput(
			"Elevator Pos Radians",
			elevatorMotor.getPosition().getValue().in(Radians)
		);
		Logger.recordOutput(
			"Elevator Pos Radians/Second",
			elevatorMotor.getVelocity().getValue().in(RadiansPerSecond)
		);
	}

	/* ======================== Protected methods ======================== */

	@Override
	protected FSMState nextState(TeleopInput input) {
		switch (getCurrentState()) {
			case EXAMPLE_STATE:
				return FSMState.EXAMPLE_STATE;

			default:
				throw new IllegalStateException("Invalid state: " + getCurrentState().toString());
		}
	}

	/* ------------------------ FSM state handlers ------------------------ */
	/**
	 * Handle behavior in EXAMPLE_STATE.
	 * @param input Global TeleopInput if robot in teleop mode or null if
	 *        the robot is in autonomous mode.
	 */
	private void handleExampleState(TeleopInput input) {

	}

}
