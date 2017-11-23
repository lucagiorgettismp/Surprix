package com.lucagiorgetti.collectionhelper.model;

import com.lucagiorgetti.collectionhelper.R;

/**
 * Created by Luca on 06/11/2017.
 */

public final class Colors {
    private static final String LIGHT_BLUE = "Light_blue";
    private static final String BLUE = "Blue";
    public static final String RED = "Red";
    public static final String ORANGE = "Orange";
    private static final String PURPLE = "Purple";
    private static final String PINK = "Pink";
    private static final String YELLOW = "Yellow";
    private static final String GREEN = "Green";
    private static final String LIGHT_GREEN = "Light_green";
    private static final String BROWN = "Brown";
    private static final String GREY = "Grey";

    private Colors(){}

    public static int getHexColor(String color){
        int c = -2;
        switch (color){
            case LIGHT_BLUE:
                c = R.color.LightBluSet;
                break;
            case BLUE:
                c = R.color.BlueSet;
                break;
            case RED:
                c = R.color.RedSet;
                break;
            case ORANGE:
                c = R.color.OrangeSet;
                break;
            case PURPLE:
                c = R.color.PurpleSet;
                break;
            case PINK:
                c = R.color.PinkSet;
                break;
            case YELLOW:
                c = R.color.YellowSet;
                break;
            case GREEN:
                c = R.color.GreenSet;
                break;
            case LIGHT_GREEN:
                c = R.color.LightGreenSet;
                break;
            case GREY:
                c = R.color.GreySet;
                break;
            case BROWN:
                c = R.color.BrownSet;
                break;
        }
        return c;
    }
}
