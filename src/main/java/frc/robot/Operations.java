/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Timer;
import java.util.TimerTask;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.DriverStation;

import com.playingwithfusion.TimeOfFlight;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTable;

/**
 * Add your docs here.
 */
public class Operations {
    private static final int ACTIVE_LENGTH = 2;
    private static final int DESCENDING = 0;
    private static final int ASCENDING = 1;
    private static final double CLIMB_UP_SPD = .65;
    private static final double CLIMB_DOWN_SPD = .3;
    private static final double WINCH_DOWN_SPD = .70;
    private static final double INTAKE_DEPLOY_POS = 67;
    private static final int INTAKE_RETRACT_POS = 15;
    private static final int INTAKE_CLIMB_POS = 25;
    private static final double INTAKE_POS_TOLERANCE = 1.5;
    private static final double INTAKE_IN_SPD = 0.4;
    private static final double INTAKE_OUT_SPD = -0.5;
    private static final int HOOD_UP = 160;
    private static final int SERVO_MIN = 0;
    private static final int SERVO_MAX = 180;
    private static final int HOOD_UP_T = 2100;
    private static final int HOOD_DOWN_T = 2100;
    private static final int SHOOT_BALLS_T = 7000;
    private static final double SHOOTER_SPD = 1;
    private static final double SPIN_WHEEL_SPD = .5;
    private static final int COLOR_WHEEL_DELAY = 3000;
    private static final int COLOR_UP_T = 5000;
    private static final int COLOR_DOWN_T = 5000;
    private static final int THREE_SPINS_NUM_C = 25;
    private static final double EMPTY = 254;
    private static final double EMPTY_B_COEF = .55;
    private static final double EMPTY_T_COEF = .55;
    private static final int EMPTY_T = 2000 / 20;
    private static final double RETRACT_SPEED = -.3;
    private static final double DEPLOY_SPEED = .2;
    private static final int RETRACT_T = 800;
    private static final int DEPLOY_T = 750;
    private static final double INTAKE1_COEF = 1.2;
    private static final int SPIN_UP = 6000;

    // constant for talonFX
    private static final int UNITS_PER_REV = 2048;

    private boolean[] active;
    private boolean deployed;
    private boolean deployed2;
    private boolean deployed3;
    private boolean shootable;
    private boolean shoot;
    private int shootStage;
    private Timer t;
    private int winchCycles;
    private boolean hoodSet;
    private boolean hoodDown;
    private boolean beltsSet;
    private boolean puking;
    private boolean indexing;
    private boolean spinning;
    private int spinStage;
    private ColorSensor.EColor last;
    private int numColors;
    private boolean colorSpinSet;
    private boolean togglingColorEx;
    private double throttleTime;
    private boolean sensing;
    private int emptyCycles;
    private String gameData;

    // private double TARGETING_SPEED = .25;
    // private double TARGETING_AREA = 100;
    // private double TARGETING_AREA_OFFSET = 25;
    // private double TARGETING_XCOORD = 100;
    // private double TARGETING_YCOORD = 100;
    // private double TARGETING_COORD_OFFSET = 25;
    // private double nTXSpeed = 0;
    // private double nTYSpeed = 0;
    // private double area;
    // private double xEntry;
    // private double yEntry;

    
    // private boolean nTables = true;
    // private double visionArea = 0;
    // private double visionX = 0;
    // private double visionY = 0;
    // private NetworkTable table;
    // private final NetworkTableInstance inst = NetworkTableInstance.getDefault();

    //Color sensor
    private ColorSensor colorS;

    //Limit switches
    DigitalInput extendLimitSwitch;

    //*NOTE*: VictorSPX controllers are smaller than TalonSRX controllers
    //motor variables
    private WPI_TalonSRX intakeDep;
        //positive deploys, negative retracts
        //Hold button (RB, cont 2) down to keep deployed and keep stage 1 of intake active

    private WPI_TalonSRX intake1;
        //positive moves balls toward shooter
        //Press button (B, cont 2) to eject balls out of front

    private WPI_TalonSRX intake2;
        //positive moves balls toward shooter

    private WPI_TalonFX shooter;
        //positive shoots balls up and out
        //Press button (A, cont 2) to shoot all balls in robot

    private TimeOfFlight laser1;
    private TimeOfFlight laser2;
    private TimeOfFlight laserS;

    private WPI_VictorSPX extender;
        //positive extends the scissor climber upwards
        //Hold button (RB, cont 1) to extend scissor climber

    private WPI_VictorSPX winch;
        //positive pulls the robot upwards, negative lets it back down
        //Hold button (RT, cont 1) to retract scissor climber, lifting robot (no power to scissor climber motor, power to winch motor)

    private WPI_VictorSPX spin;
        //Press button (X, cont 2) to rotate the color wheel 3 times
        //Press button (Y, cont 2) to rotate the color wheel to a given color

    //servo variables
    private Servo hood;
        //0 degrees is fully closed, 180 degrees is fully open (90 degrees upward)

    private Servo colorEx;
        //

    private Servo winchLock;
        //

    public Operations()
    {
        active = new boolean[ACTIVE_LENGTH];
        deployed = false;
        deployed2 = false;
        deployed3 = false;
        shootable = false;
        shoot = false;
        shootStage = 0;
        t = new Timer();
        winchCycles = 0;
        hoodSet = true;
        hoodDown = true;
        beltsSet = true;
        puking = false;
        indexing = false;
        spinning = false;
        spinStage = 0;
        numColors = 0;
        colorSpinSet = true;
        togglingColorEx = false;
        throttleTime = 0;
        sensing = false;
        emptyCycles = 0;
        colorS = new ColorSensor();
        //motor variable initialization
        intakeDep = new WPI_TalonSRX(21);
        // intakeDep.setNeutralMode(NeutralMode.Brake);
        intake1 = new WPI_TalonSRX(22);
        intake1.setInverted(true);
        intake2 = new WPI_TalonSRX(23);
        shooter = new WPI_TalonFX(24);
        shooter.setInverted(true);
        laser1 = new TimeOfFlight(31);
        laser2 = new TimeOfFlight(32);
        laserS = new TimeOfFlight(33);
        extender = new WPI_VictorSPX(41);
        extender.setInverted(true);
        extender.setNeutralMode(NeutralMode.Brake);
        winch = new WPI_VictorSPX(42);
        winch.setInverted(true);
        spin = new WPI_VictorSPX(51);
        spin.setNeutralMode(NeutralMode.Brake);
        hood = new Servo(0);
        hood.setBounds(2.0, 1.8, 1.5, 1.3, 1.0);
        // hood.setSpeed(0);
        colorEx = new Servo(1);
        colorEx.setBounds(2.0, 1.8, 1.5, 1.3, 1.0);
        winchLock = new Servo(2);
        extendLimitSwitch = new DigitalInput(1);
        intakeDep.setSelectedSensorPosition(0);
        // // Network Tables
        // if (nTables) {
        //     inst.startClientTeam(3667);
        // }

        // Analog Switch code
        // Values Found Online: 630,680,650,810,845,860,890,905,920,940,950,980
        // Actual Values: 4030, 2450, 2685, 2990, 3184, 3313, 3416, 3495, 3553, 3648, 3708, 3770
    }

    public void operate(Joystick j1, Joystick j2, DriveSystem d)
    {
        // if (j1.getRawButton(4)){
        //     intake1.set(.35);
        //     intake2.set(.35);
        // }

        // if (j1.getRawButton(6)){
        //  //lower hood
        //     hoodAngle = (hoodAngle-5<0)? 0:hoodAngle-5;
        //     hood.setSpeed(-1.0) ;
        //     hood.setAngle(hoodAngle);
        //     SmartDashboard.putNumber("hood Angle", hoodAngle);
    
        // }
       
        // if (j1.getRawButton(5)){
        //     // raise hood
        //     hoodAngle = (hoodAngle+5>180)? 180:hoodAngle+5;
        //     hood.setSpeed(1.0) ;
        //     hood.setAngle(hoodAngle);
        //     SmartDashboard.putNumber("hood Angle", hoodAngle);
        // }
        // if (j1.getRawButton(1)){
        //     // slower shooter speed
        //     shooterSpeed = (shooterSpeed-.1<0)? 0:shooterSpeed-.1;
        //     shooter.set(shooterSpeed);
        //     SmartDashboard.putNumber("spped", shooterSpeed);
        //  }
        // if (j1.getRawButton(2)){
        //     // faster shooter speed
        //     shooterSpeed = (shooterSpeed+.1>1.0)? 1:shooterSpeed+.1;
        //     shooter.set(shooterSpeed);
        //     SmartDashboard.putNumber("spped", shooterSpeed);
        //  }
        //  if (j1.getRawButton(3)){
        //      // off
        //      intake1.set(0);
        //      intake2.set(0);
        //     shooterSpeed = 0;
        //     shooter.set(shooterSpeed);
        //     SmartDashboard.putNumber("spped", shooterSpeed);
        //  }
        SmartDashboard.putNumber("laser1", laser1.getRange());
        SmartDashboard.putNumber("laser2", laser2.getRange());
        SmartDashboard.putNumber("laserS", laserS.getRange());
        SmartDashboard.putNumber("encoder", intakeDep.getSelectedSensorPosition(0) / UNITS_PER_REV);
        if(j2.getRawAxis(LogitechJoy.LEFT_TRIGGER) > LogitechJoy.TRIGGER_THRESH)
        {  
            hood.setAngle(HOOD_UP);
            hoodDown = false;
            if(hoodSet)
            {
                hoodSet = false;
                t.schedule(new TimerTask(){
                    @Override
                    public void run()
                    {
                        shootable = true;
                    }
                }, (long) HOOD_UP_T);
            }
        }
        else
        {
            hood.setAngle(SERVO_MIN);
            if(!hoodDown)
            {
                hoodDown = true;
                shootStage = 3;
            }
            else
            {
                shootStage = 0;
            }
            hoodSet = true;
        }
        if((j2.getRawButton(1) || shoot) && shootable)
        {
            fireCells(d, j2);
        }
        else
        {
            if(j2.getRawButton(LogitechJoy.BTN_RB) && !deployed2)
            {
                intake1.set(INTAKE_IN_SPD);
                if(!deployed)
                {
                    deployed = true;
                    deployed3 = true;
                    t.schedule(new TimerTask(){
                        @Override
                        public void run()
                        {
                            deployed3 = false;
                        }
                    }, (long) ((double) DEPLOY_T));
                    // * (double) intakeDep.getSelectedSensorPosition(0) / (double) UNITS_PER_REV / INTAKE_DEPLOY_POS));
                }
                if(deployed3)
                {
                    intakeDep.set(DEPLOY_SPEED);
                }
                else
                {
                    intakeDep.set(0);
                }
            }
            else
            {
                if(deployed)
                {
                    deployed = false;
                    deployed2 = true;
                    t.schedule(new TimerTask(){
                        @Override
                        public void run()
                        {
                            deployed2 = false;
                            intakeDep.setSelectedSensorPosition(0);
                        }
                    }, (long) ((double) RETRACT_T));
                    //  * (double) intakeDep.getSelectedSensorPosition(0) / (double) UNITS_PER_REV / INTAKE_DEPLOY_POS));
                }
                if(deployed2)
                {
                    intakeDep.set(RETRACT_SPEED);
                }
                else
                {
                    intakeDep.set(0);
                }
                // else
                // {
                //     setCellIntakePos(0);
                // }
            }
            if(j2.getRawAxis(LogitechJoy.LEFT_TRIGGER) > LogitechJoy.TRIGGER_THRESH)
            {
                toggleColorEx();
            }

            // if(j2.getRawButton(LogitechJoy.BTN_LB))
            // {
            //     intake2.set(INTAKE_IN_SPD);
            // }

            //Puking method to empty all balls. Not currently working
            // if(j2.getRawButton(LogitechJoy.BTN_B) || puking)
            // {
            //     puke();
            // }

            //Previous puking method
            if(j2.getRawButton(LogitechJoy.BTN_B))
            {
                intake1.set(INTAKE_OUT_SPD);
                intake2.set(INTAKE_OUT_SPD);
            }
            else
            {
                indexBalls(j2);
            }

            // if(j2.getRawButton(LogitechJoy.BTN_B))
            // {
            //     intake1.set(INTAKE_OUT_SPD);
            //     intake2.set(INTAKE_OUT_SPD);
            //     throttleTime = 0;
            // }
            // else if(j2.getRawButton(LogitechJoy.BTN_LB) && throttleTime <= System.currentTimeMillis())
            // {
            //     intake2.set(INTAKE_IN_SPD);
            //     throttleTime = System.currentTimeMillis() + 1000;
            // } 
            // else if(throttleTime <= System.currentTimeMillis())
            // {
            //     intake2.set(0);
            // }

            // if(!j2.getRawButton(LogitechJoy.BTN_LB) && !j2.getRawButton(LogitechJoy.BTN_B))
            // {
            //     intake2.set(0);
            // }
            if(!j2.getRawButton(LogitechJoy.BTN_B) && !indexing)
            {
                intake2.set(0);
            }
            if(!j2.getRawButton(LogitechJoy.BTN_RB) && !j2.getRawButton(LogitechJoy.BTN_B) && !indexing)
            {
                intake1.set(0);
            }

            // Code for color wheel. Currently unused, since the color wheel appartus has been removed.
            // if(j2.getRawButton(LogitechJoy.BTN_Y) || sensing)
            // {
            //     spinToColor();
            // }
            // else if(j2.getRawButton(LogitechJoy.BTN_X) || spinning)
            // {
            //     spinWheel();
            // }
            climb(j1);
        }
        healthCheck();
    }

    public boolean isEmpty()
    {
        return emptyCycles >= EMPTY_T;
    }

    // private void puke()
    // {
    //     if(emptyCycles >= EMPTY_T)
    //     {
    //         emptyCycles = 0;
    //         intake1.set(0);
    //         intake2.set(0);
    //         puking = false;
    //         return;
    //     }
    //     puking = true;
    //     intake1.set(INTAKE_OUT_SPD);
    //     intake2.set(INTAKE_OUT_SPD);
    //     if(laser1.getRange() >= EMPTY && laser2.getRange() >= EMPTY && laserS.getRange() >= EMPTY)
    //     {
    //         emptyCycles++;
    //     }
    //     else
    //     {
    //         emptyCycles = 0;
    //     }
    // }

    private void indexBalls(Joystick j2)
    {
        if(laserS.getRange() >= EMPTY)
        {
            if(laser1.getRange() < EMPTY * EMPTY_B_COEF)
            {
                indexing = true;
                intake1.set(INTAKE_IN_SPD);
                intake2.set(INTAKE_IN_SPD);
            }
            else
            {
                if(!j2.getRawButton(LogitechJoy.BTN_RB))
                {
                    intake1.set(0);
                }
                intake2.set(0);
                indexing = false;
            }
        }
        else
        {
            if(laser1.getRange() < EMPTY * EMPTY_B_COEF)
            {
                intake1.set(0);
            }
            intake2.set(0);
            indexing = false;
        }
    }

    private void healthCheck() {}

    private void fireCells(DriveSystem d, Joystick j2) {
        shoot = true;
        switch(shootStage)
        {
            case 0:
                // hood.setAngle(HOOD_UP);
                shooter.set(SHOOTER_SPD);
                intake1.set(0);
                intake2.set(0);
                SmartDashboard.putNumber("rpm of shooter", shooter.getSelectedSensorVelocity() / UNITS_PER_REV * 600);
                if((double) shooter.getSelectedSensorVelocity() / (double) UNITS_PER_REV * 600.0 >= SPIN_UP)
                {
                    shootStage++;
                }

                //This line was added solely for the purpose of fixing the servo mishap at Kettering
                // shootStage++;

                // if(hoodSet)
                // {
                //     hoodSet = false;
                //     t.schedule(new TimerTask(){
                //         @Override
                //         public void run()
                //         {
                //             shootStage++;
                //         }
                //         }, (long) HOOD_UP_T);
                // }
                break;
            case 1:
                if(emptyCycles >= EMPTY_T)
                {
                    emptyCycles = 0;
                    shootStage++;
                    return;
                }
                intake1.set(INTAKE_IN_SPD * INTAKE1_COEF);
                intake2.set(INTAKE_IN_SPD);
                if(laser1.getRange() >= EMPTY && laser2.getRange() >= EMPTY && laserS.getRange() >= EMPTY)
                {
                    emptyCycles++;
                }
                else
                {
                    emptyCycles = 0;
                }

                //Previous method to shoot all balls
                // if(beltsSet)
                // {
                //     beltsSet = false;
                //     t.schedule(new TimerTask(){
                //         @Override
                //         public void run()
                //         {
                //             shootStage++;
                //         }
                //         }, (long) SHOOT_BALLS_T);
                // }
                break;
            default:
                // hood.setAngle(0);
                shooter.set(0);
                intake1.set(0);
                intake2.set(0);
                // hoodSet = true;
                t.cancel();
                t = new Timer();
                beltsSet = true;
                shootStage = 0;
                shoot = false;
                if(j2.getRawAxis(LogitechJoy.LEFT_TRIGGER) <= LogitechJoy.TRIGGER_THRESH)
                {
                    shootable = false;
                }
        }
    }

    private void toggleColorEx()
    {
        if(!togglingColorEx)
        {
            togglingColorEx = true;
            if(colorEx.getAngle() < SERVO_MAX / 2)
            {
                colorEx.setAngle(SERVO_MAX);
                t.schedule(new TimerTask(){
                    @Override
                    public void run()
                    {
                        togglingColorEx = false;
                        spinStage = 1;
                    }
                    }, (long) COLOR_UP_T);
            }
            else
            {
                colorEx.setAngle(SERVO_MIN);
                t.schedule(new TimerTask(){
                    @Override
                    public void run()
                    {
                        togglingColorEx = false;
                        spinStage = 1;
                    }
                    }, (long) COLOR_DOWN_T);
            }
        }
    }

    private void spinToColor()
    {
        sensing = true;
        spin.set(SPIN_WHEEL_SPD);
        gameData = DriverStation.getInstance().getGameSpecificMessage();
        if(gameData.length() > 0 && colorS.getColorOff() == ColorSensor.EColor.toEnum(gameData.charAt(0)))
        {
            spin.set(0);
            sensing = false;
        }
    }

    private void spinWheel()
    {
        spinning = true;
        switch(spinStage)
        {
            case 1:
                last = colorS.getColorDetected();
                break;
            case 2:
                spin.set(SPIN_WHEEL_SPD);
                ColorSensor.EColor cur = colorS.getColorDetected();
                if(last != cur)
                {
                    last = cur;
                    numColors++;
                }
                if(numColors >= THREE_SPINS_NUM_C)
                {
                    spin.set(0);
                    spinStage = 0;
                    colorSpinSet = true;
                    spinning = false;
                }
                break;
        }
    }

    private void climb(Joystick j) {
        if(j.getRawAxis(LogitechJoy.RIGHT_TRIGGER) >= LogitechJoy.TRIGGER_THRESH)
        {
            if(!active[DESCENDING])
            {
                extender.setNeutralMode(NeutralMode.Coast);
                active[DESCENDING] = true;
            }
            winch.set(WINCH_DOWN_SPD);
            winchCycles++;
        } 
        else if(j.getRawButton(LogitechJoy.BTN_RB) && !extendLimitSwitch.get()) 
        {
            setCellIntakePos(INTAKE_DEPLOY_POS);
            if(winchCycles > 0)
            {
                winch.set(-WINCH_DOWN_SPD);
                winchCycles--;
            }
            else
            {
                if(!active[ASCENDING])
                {
                    extender.setNeutralMode(NeutralMode.Brake);
                    active[ASCENDING] = true;
                }
                extender.set(CLIMB_UP_SPD);
            }
        }
        if(active[DESCENDING] && j.getRawAxis(LogitechJoy.RIGHT_TRIGGER) < LogitechJoy.TRIGGER_THRESH)
        {
            active[DESCENDING] = false;
            extender.setNeutralMode(NeutralMode.Brake);
            extender.set(0);
            winch.set(0);
        }
        if((active[ASCENDING] && !j.getRawButton(LogitechJoy.BTN_RB)) || extendLimitSwitch.get())
        {
            active[ASCENDING] = false;
            extender.set(0);
        }
    }

    private void setCellIntakePos(double targetPos) {
        double intakeDepCurrentPos = ((double) intakeDep.getSelectedSensorPosition(0) / UNITS_PER_REV);
        if(intakeDepCurrentPos > targetPos + INTAKE_POS_TOLERANCE)
        {
            /* Check if over tolerance */
            intakeDep.set(RETRACT_SPEED);
        } 
        else if(intakeDepCurrentPos < targetPos - INTAKE_POS_TOLERANCE)
        {
            /* Check if under tolerance */
            intakeDep.set(DEPLOY_SPEED);
        }
        else
        {
            /* Position is within tolerance */
            intakeDep.set(0);
        }
    }

    // public void target(DriveSystem d, NetworkTable table) {
    //     double area = table.getEntry("Area").getDouble(-1);
    //     double xEntry = table.getEntry("Coords-x").getDouble(-1);
    //     double yEntry = table.getEntry("Coords-y").getDouble(-1);
    //     if (area >= TARGETING_AREA + TARGETING_AREA_OFFSET) { // Robot is too far forward
    //         nTYSpeed = -TARGETING_SPEED;
    //     } else if (area <= TARGETING_AREA - TARGETING_AREA_OFFSET) { // Robot is too far back
    //         nTYSpeed = TARGETING_SPEED;
    //     }

    //     if (xEntry >= TARGETING_XCOORD + TARGETING_COORD_OFFSET) { // Robot is too far right
    //         nTXSpeed = -TARGETING_SPEED;
    //     } else if (xEntry <= TARGETING_XCOORD - TARGETING_COORD_OFFSET) { // Robot is too far right
    //         nTXSpeed = TARGETING_SPEED;
    //     }

    //     if (mecanum) {
    //         _mDrive.driveCartesian(nTYSpeed, nTXSpeed, 0, 0);
    //     } else {
    //         _aDrive.arcadeDrive(nTXSpeed, 0);
    //     }
    // }

    // public boolean isInTargetingRange() {
    //     if (area >= TARGETING_AREA - TARGETING_AREA_OFFSET && area <= TARGETING_AREA + TARGETING_AREA_OFFSET) { // Checks Area
    //         if(xEntry >= TARGETING_XCOORD - TARGETING_COORD_OFFSET && xEntry <= TARGETING_XCOORD + TARGETING_COORD_OFFSET) { // Checks xCord
    //             return true;
    //         }
    //     }
    //     return false;
    // }
}
