/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Add your docs here.
 */
public class LogitechJoy extends Joystick {
    // Buttons
    public static final int BTN_A = 1;
    public static final int BTN_B = 2;
    public static final int BTN_X = 3;
    public static final int BTN_Y = 4;
    public static final int BTN_LB = 5;
    public static final int BTN_RB = 6;
    public static final int BTN_BACK = 7;
    public static final int BTN_START = 8;
    public static final int BTN_LEFT_JOYSTICK = 9;
    public static final int BTN_RIGHT_JOYSTICK = 10;

    // Axis
    public static final int LEFT_HORIZONTAL = 0;
    public static final int LEFT_VERTICAL = 1;
    public static final int LEFT_TRIGGER = 2;
    public static final int RIGHT_TRIGGER = 3;
    public static final int RIGHT_HORIZONTAL = 4;
    public static final int RIGHT_VERTICAL = 5;

    //Constants
    public static final double TRIGGER_THRESH = .05;

    public LogitechJoy(int port) {
        super(port);
    }
}
