package org.example.model;

public class Location {
    private double x;
    private Double y; // not null
    private Integer z; // not null

    public Location(double x, Double y, Integer z) {
        this.x = x;
        if (y == null) throw new IllegalArgumentException("y cannot be null");
        if (z == null) throw new IllegalArgumentException("z cannot be null");
        this.y = y;
        this.z = z;
    }

    public double getX() { return x; }
    public Double getY() { return y; }
    public Integer getZ() { return z; }

    @Override
    public String toString() {
        return "Location{x=" + x + ", y=" + y + ", z=" + z + "}";
    }
}

