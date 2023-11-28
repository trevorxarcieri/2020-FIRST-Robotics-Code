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
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class DriveSystem {
    private static class Auton {
        private static class pseuJoy extends Joystick {
            private static enum TurnE
            {
                TURN, MOVE_FWD_TRUE, STRAFE_TRUE;

                private static TurnE get(int i)
                {
                    switch(i)
                    {
                        case MOVING_FORWARD:
                            return MOVE_FWD_TRUE;

                        case STRAFING:  
                            return STRAFE_TRUE;
                    }
                    return TURN;
                }
            }

            public static final int MOVE_FWD_FULL_SPD = -1;
            public static final int STRAFE_FULL_SPD = 1;
            public static final double MOVE_FWD_SLOW_SPD = -.15;
            public static final double STRAFE_SLOW_SPD = .3;
            public static final double TURN_FULL_SPD = .4;
            public static final double TURN_SLOW_SPD = .3;
            public static final double TRUE_MOVE_FWD_SPD = .02;
            public static final double TRUE_STRAFE_SPD = .05;
            private static final double TRUING_FAST_COEF = 3;

            private double[] ax;
            private boolean[] bu;
            private double ax0;
            private double ax1;
            private double ax4;

            public static double getFast(TurnE t)
            {
                switch(t)
                {
                    case TURN:
                        return TURN_FULL_SPD;

                    case MOVE_FWD_TRUE:
                        return TRUE_MOVE_FWD_SPD * TRUING_FAST_COEF;

                    case STRAFE_TRUE: 
                        return TRUE_STRAFE_SPD * TRUING_FAST_COEF;
                }
                return 0;
            }

            public static double getSlow(TurnE t)
            {
                switch(t)
                {
                    case TURN:
                        return TURN_SLOW_SPD;

                    case MOVE_FWD_TRUE:
                        return TRUE_MOVE_FWD_SPD;

                    case STRAFE_TRUE: 
                        return TRUE_STRAFE_SPD;
                }
                return 0;
            }

            public pseuJoy()
            {
                super(50);
                ax = new double[5];
                bu = new boolean[10];
                ax0 = 0;
                ax1 = 0;
                ax4 = 0;
            }

            public String toString()
            {
                return "Axis 0: " + ax0 + "\nAxis 1: " + ax1 + "\nAxis 4: " + ax4;
            }

            public void addToAxis(int i, double val)
            {
                setAxis(i, getRawAxis(i) + val);
            }

            public void setAxis(int i, double val)
            {
                if(i < ax.length)
                {
                    ax[i] = val;
                }
            }

            @Override
            public double getRawAxis(int i)
            {
                return (i < ax.length)? ax[i] : 0;
            }

            public void setButton(int i, boolean b)
            {
                if(i < ax.length)
                {
                    bu[i] = b;
                }
            }

            @Override
            public boolean getRawButton(int i)
            {
                return (i < bu.length)? bu[i] : false;
            }
        }

        //Constants for various depths of future autoDrive function
        // private static final int L_DEPTH = 0;
        // private static final int M_DEPTH = 1;
        // private static final int H_DEPTH = 2;

        /*Constants for array of booleans describing
        whether various autonomous driving functions
        are active */
        private static final int ACTIVE_LENGTH = 3;
        private static final int MOVING_FORWARD = 0;
        private static final int TURNING = 1;
        private static final int STRAFING = 2;

        //Miscellaneous constants
        private static final int TIME_SAFE = 500;
        private static final int ANGLE_SAFE = 30;

        private static final double AUTON_BASIC_T = .75;

        private DriveSystem d;
        private Operations op;
        private pseuJoy j1;
        private pseuJoy j2;
        private Timer t;
        private boolean[] active;
        private double trueHead;
        private double curAngle;
        private double targetAngle;
        private int stage;
        private int stage2;
        
        //Auton switches
        private Rotary leftRot;
        private Rotary rightRot;

        public Auton(DriveSystem d, Operations op)
        {
            this.d = d;
            this.op = op;
            j1 = new pseuJoy();
            j2 = new pseuJoy();
            t = new Timer();
            active = new boolean[ACTIVE_LENGTH];
            trueHead = 0;
            curAngle = 0;
            targetAngle = 0;
            stage = 0;
            stage2 = 0;
            leftRot = new Rotary(3);
            rightRot = new Rotary(2);
        }

        public void run()
        {
            //Test Drive Code
            // switch(stage)
            // {
            //     case 0:
            //         move(pseuJoy.MOVE_FWD_FULL_SPD * .5, pseuJoy.MOVE_FWD_SLOW_SPD, 2);
            //         break;

            //     case 1:
            //         turn(90);
            //         break;

            //     case 2:
            //         strafe(pseuJoy.STRAFE_FULL_SPD * .5, pseuJoy.STRAFE_SLOW_SPD, 3.4);
            //         break;
                    
            //     case 3:
            //         turn(-90);
            //         break;
            // }
            // d.drive(j1);
            // SmartDashboard.putNumber("Axis 0:", j1.getRawAxis(0));
            // SmartDashboard.putNumber("Axis 1:", j1.getRawAxis(1));
            // SmartDashboard.putNumber("Axis 4:", j1.getRawAxis(4));
            // SmartDashboard.putNumber("Current Angle: ", d.nav.getAngle());
            // SmartDashboard.putNumber("Target Angle: ", targetAngle);
            switch(stage)
            {
                case 0:
                    stage++;
                    t.schedule(new TimerTask(){
                        @Override
                        public void run()
                        {
                            stage++;
                        }
                    }, (long) (rightRot.getPos() - 1) * 1000);
                    break;
                case 2:
                    if(leftRot.getPos() == 1)
                    {
                        switch(stage2)
                        {
                            case 0:
                                stage2++;
                                move(pseuJoy.MOVE_FWD_FULL_SPD, pseuJoy.MOVE_FWD_FULL_SPD, AUTON_BASIC_T);
                                break;
                        }
                    }
                    if(leftRot.getPos() == 2)
                    {
                        switch(stage2)
                        {
                            case 0:
                                j2.setButton(LogitechJoy.BTN_B, true);
                                stage2++;
                                break;
                            case 1:
                                j2.setButton(LogitechJoy.BTN_B, false);
                                stage2++;
                            case 2:
                                if(op.isEmpty())
                                {
                                    stage2++;
                                }
                            case 3:
                                stage2++;
                                strafe(-pseuJoy.STRAFE_FULL_SPD, -pseuJoy.STRAFE_FULL_SPD, AUTON_BASIC_T);
                        }
                    }
                    if(leftRot.getPos() == 3)
                    {
                        switch(stage2)
                        {
                            case 0:
                                stage2++;
                                turn(180);
                                break;
                            case 1:
                                j2.setButton(LogitechJoy.BTN_A, true);
                                stage2++;
                            case 2:
                                j2.setButton(LogitechJoy.BTN_A, false);
                                stage2++;
                        }
                    }
                    if(leftRot.getPos() == 4)
                    {
                        switch(stage2)
                        {
                            case 0:
                                stage2++;
                                move(-pseuJoy.MOVE_FWD_FULL_SPD, -pseuJoy.MOVE_FWD_FULL_SPD, 1);
                                break;
                        }
                    }
                    if(leftRot.getPos() == 5)
                    {
                        switch(stage2)
                        {
                            case 0:
                                j2.setButton(LogitechJoy.BTN_B, true);
                                stage2++;
                                break;
                            case 1:
                                j2.setButton(LogitechJoy.BTN_B, false);
                                stage2++;
                            case 2:
                                if(op.isEmpty())
                                {
                                    stage2++;
                                }
                            case 3:
                                stage2++;
                                strafe(pseuJoy.STRAFE_FULL_SPD, -pseuJoy.STRAFE_FULL_SPD, 1);
                        }
                    }
            }
            d.drive(j1);
            op.operate(j1, j2, d);
        }
        
        //Positive movement is forward
        private void move(double speed, double slowS, double sec)
        {
            moveArc(speed, slowS, sec, 0);
        }

        private void moveArc(double speed, double slowS, double sec, double angle)
        {
            moveAdv(MOVING_FORWARD, 1, speed, slowS, sec, angle);
        }

        //Positive strafing is to the right
        private void strafe(double speed, double slowS, double sec)
        {
            strafeArc(speed, slowS, sec, 0);
        }

        private void strafeArc(double speed, double slowS, double sec, double angle)
        {
            if(d.isMecanum())
            {
                moveAdv(STRAFING, 0, speed, slowS, sec, angle);
            }
        }

        private void moveAdv(int boolInd, int axis, double speed, double slowS, double sec, double angle)
        {
            if(!active[boolInd])
            {
                //*initialization operations*
                j1.setAxis(axis, speed);
                trueHead = d.nav.getAngle();
                active[boolInd] = true;
                t.schedule(new TimerTask(){
                  @Override
                  public void run()
                  {
                    j1.setAxis(axis, slowS);
                  }
                }, (long) (sec * 1000) - TIME_SAFE);
                t.schedule(new TimerTask(){
                    @Override
                    public void run()
                    {
                        active[boolInd] = false;
                        active[TURNING] = false;
                        j1.setAxis(axis, 0);
                        j1.setAxis(4, 0);
                        stage2++;
                    }
                  }, (long) (sec * 1000));
            }
            if(active[boolInd])
            {
                turn(trueHead - d.nav.getAngle() + angle, false, pseuJoy.TurnE.get(boolInd));
            }
        }

        private void turn(double angle)
        {
            turn(angle, true, pseuJoy.TurnE.TURN);
        }

        private void turn(double angle, boolean advance, pseuJoy.TurnE type)
        {
            if(!active[TURNING])
            {
                //initialization operations
                curAngle = d.nav.getAngle();
                targetAngle = curAngle + angle;
                active[TURNING] = true;
                if(curAngle - ANGLE_SAFE > targetAngle)
                {
                    j1.setAxis(4, -pseuJoy.getFast(type));
                }
                else if(curAngle + ANGLE_SAFE < targetAngle)
                {
                    j1.setAxis(4, pseuJoy.getFast(type));
                }
                else if(curAngle > targetAngle)
                {
                    j1.setAxis(4, -pseuJoy.getSlow(type));
                }
                else if(curAngle < targetAngle)
                {
                    j1.setAxis(4, pseuJoy.getSlow(type));
                }
                else
                {
                    active[TURNING] = false;
                    if(advance)
                    {
                        stage2++;
                    }
                }
            }
            else
            {
                curAngle = d.nav.getAngle();
                if(j1.getRawAxis(4) > 0) 
                {
                    if(curAngle >= targetAngle)
                    {
                        active[TURNING] = false;
                        j1.setAxis(4, 0);
                        //advance the stage:
                        if(advance)
                        {
                            stage2++;
                        }
                    }
                    if(j1.getRawAxis(4) > pseuJoy.getSlow(type) && curAngle + ANGLE_SAFE >= targetAngle)
                    {
                        j1.setAxis(4, pseuJoy.getSlow(type));
                    }
                }
                else if(j1.getRawAxis(4) != 0)
                {
                    if(curAngle <= targetAngle)
                    {
                        active[TURNING] = false;
                        j1.setAxis(4, 0);
                        //advance the stage:
                        if(advance)
                        {
                            stage2++;
                        }
                    }
                    if(j1.getRawAxis(4) < -pseuJoy.getSlow(type) && curAngle - ANGLE_SAFE <= targetAngle)
                    {
                        j1.setAxis(4, -pseuJoy.getSlow(type));
                    }
                }
            }
        }
    }

    private boolean mecanum;
    private WPI_VictorSPX frontLeftMotor;
    private WPI_VictorSPX frontRightMotor;
    private WPI_VictorSPX rearRightMotor;
    private WPI_VictorSPX  rearLeftMotor;
    private MecanumDrive _mDrive;
    private DifferentialDrive _aDrive;
    private NavX nav;
    private Auton auto;

    public DriveSystem(boolean mecanum, Operations op)
    {
        this.mecanum = mecanum;
        frontLeftMotor = new WPI_VictorSPX(14);
        frontLeftMotor.setInverted(true);
        frontLeftMotor.setNeutralMode(NeutralMode.Brake);
        frontRightMotor = new WPI_VictorSPX(13);
        frontRightMotor.setInverted(true);
        frontRightMotor.setNeutralMode(NeutralMode.Brake);
        rearRightMotor = new WPI_VictorSPX(12);
        rearRightMotor.setInverted(true);
        rearRightMotor.setNeutralMode(NeutralMode.Brake);
        rearLeftMotor = new WPI_VictorSPX(11);
        rearLeftMotor.setInverted(true);
        rearLeftMotor.setNeutralMode(NeutralMode.Brake);
        if(mecanum)
        {
            _mDrive = new MecanumDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
        }
        else
        {
            _aDrive = new DifferentialDrive(new SpeedControllerGroup(frontLeftMotor, rearLeftMotor),
            new SpeedControllerGroup(frontRightMotor, rearRightMotor));
        }
        nav = new NavX();
        auto = new Auton(this, op);
    }

    //autoDrive based on depth and angle
    // public void autoDrive(int depth, int angle)
    // {
    //     auto.drive(depth, angle);
    // }

    public void autoRun()
    {
        auto.run();
    }

    public void drive(Joystick j)
    {
        if(mecanum)
        {
            _mDrive.driveCartesian(j.getRawAxis(0), -j.getRawAxis(1), j.getRawAxis(4), 0);
        }
        else
        {
            _aDrive.arcadeDrive(-j.getRawAxis(1), j.getRawAxis(4));
        }
    }

    public boolean isMecanum() {
        return mecanum;
    }

    public void setMecanum(boolean mecanum) {
        this.mecanum = mecanum;
    }

}
