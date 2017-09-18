package com.example.dekube.ips;

/**
 * Created by dekube on 28.08.2017.
 */

public class Calculator {

    private double calculateDistance(int rssi) {

        double distanceInMeter;
        final double txPower = -60;

        if (rssi == 0) {
            return -1;
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return distance;
        }

    }
    private MyLocation calculatePosition(double ra, double rb, double rc) {

        MyLocation myLocation;

        double xa = 0;
        double ya = 0;
        double xb = 3;
        double yb = 0;
        double xc = 1.5;
        double yc = 4;

        double S = (Math.pow(xc, 2.) - Math.pow(xb, 2.) + Math.pow(yc, 2.) - Math.pow(yb, 2.) + Math.pow(rb, 2.) - Math.pow(rc, 2.)) / 2.0;
        double T = (Math.pow(xa, 2.) - Math.pow(xb, 2.) + Math.pow(ya, 2.) - Math.pow(yb, 2.) + Math.pow(rb, 2.) - Math.pow(ra, 2.)) / 2.0;
        double y = ((T * (xb - xc)) - (S * (xb - xa))) / (((ya - yb) * (xb - xc)) - ((yc - yb) * (xb - xa)));
        double x = ((y * (ya - yb)) - T) / (xb - xa);

        myLocation = new MyLocation(x, y);

        return myLocation;
    }


}
