package com.example.voogle;

import com.example.voogle.PojoClasses.Fairs;
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
    public static Double sourceLat;
    public static Double sourceLng;
    public static Double destinationLat;
    public static Double destinationLng;
    public static Fairs fairs;

}
