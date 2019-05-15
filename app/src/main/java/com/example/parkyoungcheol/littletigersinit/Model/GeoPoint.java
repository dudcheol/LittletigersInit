package com.example.parkyoungcheol.littletigersinit.Model;

public class GeoPoint {
    double x;
    double y;
    double z;
    String x_s;
    String y_s;

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setX_s(String x_s) {
        this.x_s = x_s;
    }

    public void setY_s(String y_s) {
        this.y_s = y_s;
    }

    /**
     *
     */
    public GeoPoint() {
        super();
    }

    /**
     * @param x
     * @param y
     */
    public GeoPoint(double x, double y) {
        super();
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    /**
     * @param x
     * @param y
     * @param y
     */
    public GeoPoint(double x, double y, double z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getX_s() {
        return x_s;
    }

    public String getY_s() {
        return y_s;
    }
}
