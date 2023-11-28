/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shaRED by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

/**
 * Add your docs here.
 */
public class ColorSensor extends ColorSensorV3{
    public static enum EColor {
        RED, YELLOW, BLUE, GREEN, UNKNOWN;

        public static EColor toEnum(String s) {
            if (s.equals(RED.toString()))
            {
                return RED;
            }
            else if(s.equals(YELLOW.toString()))
            {
                return YELLOW;
            }
            else if(s.equals(BLUE.toString()))
            {
                return BLUE;
            }
            else if(s.equals(GREEN.toString()))
            {
                return GREEN;
            }
            else
            {
                return UNKNOWN;
            }
        }

        public static EColor toEnum(char c)
        {
            switch (c)
            {
                case 'B' :
                    //Blue case code
                    return BLUE;
                case 'G' :
                    //Green case code
                    return GREEN;
                case 'R' :
                    //Red case code
                    return RED;
                case 'Y' :
                    //Yellow case code
                    return YELLOW;
                default :
                //This is corrupt data
                    return UNKNOWN;
            }
        }
    }

    public ColorSensor() {
        super(I2C.Port.kOnboard);
    }

    public EColor getColorDetected() {
        Color detected = getColor();
        if (detected.red > .25 && detected.blue < .25) {
            if (detected.green < .48) {
                return EColor.RED;
            } else {
                return EColor.YELLOW;
            }
        } else if (detected.red < .25) {
            if (detected.blue > .26) {
                return EColor.BLUE;
            } else {
                return EColor.GREEN;
            }
        } else {
            return EColor.UNKNOWN;
        }
    }

    public EColor getColorOff() {
        return offset(getColorDetected());
    }

    public static EColor offset(EColor color) {
        if (color == EColor.UNKNOWN) {
            return EColor.UNKNOWN;
        }
        int x = -1;
        for (int i = 0; i < EColor.values().length - 1; i++) {
            if (color == EColor.values()[i]) {
                x = i;
            }
        }
        if (x == -1) {
            try
            {
                throw new Exception("An invalid color was passed into offset(EColor color)");
            } 
            catch (Exception e) {}
        }
        return EColor.values()[(x + 2) % 4];
    }
}
