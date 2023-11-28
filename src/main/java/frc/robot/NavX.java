/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.kauailabs.navx.frc.AHRS;

/**
 * Add your docs here.
 */
public class NavX extends AHRS{
    public static enum Orient
    {
        STANDARD("X+ left, Y+ back, Angle+ clockwise, flat");

        private final String desc;

        private Orient(String desc)
        {
            this.desc = desc;
        }

        @Override
        public String toString()
        {
            return desc;
        }
    }

    private Orient o;

    public NavX()
    {
        this(Orient.STANDARD);
    }

    public NavX(Orient o)
    {
        this.o = o;
    }
}
