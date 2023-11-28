/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * Add your docs here.
 */
public class Rotary extends AnalogInput{
    /*The following are constants for the empirically-derived
    * values of each position on the rotary switch. The name of
    * each value is based on the number that position would
    * occupy on an anolog clock. E.g. six is the bottom, 12 is 
    * the top, 3 is the right, etc. 
    */
    public static final int SIX = 4030;
    public static final int SEVEN = 2450;
    public static final int EIGHT = 2685;
    public static final int NINE = 2990;
    public static final int TEN = 3184;
    public static final int ELEVEN = 3313;
    public static final int TWELVE = 3416;
    public static final int ONE = 3495;
    public static final int TWO = 3553;
    public static final int THREE = 3648;
    public static final int FOUR = 3708;
    public static final int FIVE = 3770;

    public static final int RANGE = 30;

    public Rotary(int ch)
    {
        super(ch);
    }

    public int getPos()
    {
        int val = getValue();
        if(Math.abs(val - SIX) < RANGE)
        {
            return 6;
        }
        if(Math.abs(val - SEVEN) < RANGE)
        {
            return 7;
        }
        if(Math.abs(val - EIGHT) < RANGE)
        {
            return 8;
        }
        if(Math.abs(val - NINE) < RANGE)
        {
            return 9;
        }
        if(Math.abs(val - TEN) < RANGE)
        {
            return 10;
        }
        if(Math.abs(val - ELEVEN) < RANGE)
        {
            return 11;
        }
        if(Math.abs(val - TWELVE) < RANGE)
        {
            return 12;
        }
        if(Math.abs(val - ONE) < RANGE)
        {
            return 1;
        }
        if(Math.abs(val - TWO) < RANGE)
        {
            return 2;
        }
        if(Math.abs(val - THREE) < RANGE)
        {
            return 3;
        }
        if(Math.abs(val - FOUR) < RANGE)
        {
            return 4;
        }
        if(Math.abs(val - FIVE) < RANGE)
        {
            return 5;
        }
        return -1;
    }
}
