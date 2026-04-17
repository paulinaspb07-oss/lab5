package org.example.model;

public class Coordinates {
    private long x;
    private double y; // max 663

    public Coordinates(long x, double y) {
        this.x = x;
        setY(y);
    }

    public long getX() { return x; }
    public double getY() { return y; }
    public void setY(double y) {
        if (y > 663) throw new IllegalArgumentException("y cannot exceed 663");
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + "}";
    }
}

