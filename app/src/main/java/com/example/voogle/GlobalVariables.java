package com.example.voogle;

import com.example.voogle.PojoClasses.Fares;
import com.example.voogle.PojoClasses.Stops;

import java.util.ArrayList;

public class GlobalVariables {
    public static ArrayList<Integer> sourceRoutes=new ArrayList<>();
    public static ArrayList<Integer> destinationRoutes=new ArrayList<>();
    public static ArrayList<Stops> stops=new ArrayList<>();
    public static int sourceS_no;
    public static int destinationS_no;
    public static String sourceName;
    public static String destinationName;
    public static String sourceNewName;
    public static String destinationNewName;


    public static Double sourceLat;
    public static Double sourceLng;

    public static Double sourceNewLat;
    public static Double sourceNewLng;

    public static Double destinationLat;
    public static Double destinationLng;

    public static Double destinationNewLat;
    public static Double destinationNewLng;

    public static Fares fares;
    public static final double averageSpeedOfDhaka=0.1079;
    public static final double fareNormal=1.7*2.3;
    public static final double farePremium=1.7*3.1;

}
