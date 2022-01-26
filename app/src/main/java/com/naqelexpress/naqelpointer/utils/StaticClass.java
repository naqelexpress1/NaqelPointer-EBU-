package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.DB.DBObjects.Station;

import java.util.ArrayList;
import java.util.List;

public class StaticClass {

    //below 2 for onLineValidation
    public static ArrayList<Station> stationArrayList = new ArrayList<>();
    public static List<Integer> allowedDestStationIDs = new ArrayList<>();

    //Validation Currently isonly TH ,  so if you release CBU or other Please change to false
    public static boolean isDsValidation = true;


}
