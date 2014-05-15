package com.fel.bond.grids;

import java.io.Serializable;
import javax.vecmath.Point2d;

/**
 * Represents one cell in grids.
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public class Cell implements Cloneable, Serializable {
    private double value;
    private Point2d center;
    
    public Cell(double value, Point2d center) {
        this.value = value;
        this.center = center;
    }
    
    public Cell(double value, double centerX, double centerY) {
        this(value, new Point2d(centerX, centerY));
    }
    
    public Cell(double centerX, double centerY) {
        this.center = new Point2d(centerX, centerY);
        this.value = 0;
    }
    
    public Cell(double value) {
        this.value = value;
    }

    public Cell() {
    }

    public Point2d getCenter() {
        return center;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    @Override
    public Cell clone() {
        Cell clone = new Cell();
        if (center != null){
            clone.center = (Point2d) center.clone();
        }
        clone.value = value;
        return clone;
    }
    
    
    
}
