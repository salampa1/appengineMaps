package com.fel.bond.grids;

import java.io.Serializable;

/**
 * 2d grid with timesteps
 *
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public class TimeGrid implements Cloneable, Serializable {

    private Grid2D[] timesteps; // [timestep]

    private static final long serialVersionUID = 1211L;

    public TimeGrid(int timeStepsNum) {
        timesteps = new Grid2D[timeStepsNum];
    }
    
    public TimeGrid(int timeStepsNum, int rows, int cols, int timestepDurationHours) {
        this(timeStepsNum);
        
        for (int i = 0; i < timesteps.length; i++) {
            timesteps[i] = new Grid2D(rows, cols);
        }
    }
    


    public TimeGrid() {
    }

    @Override
    public TimeGrid clone() {
        TimeGrid clone = new TimeGrid();
        clone.timesteps = new Grid2D[timesteps.length];
        for (int i = 0; i < timesteps.length; i++) {
            clone.timesteps[i] = timesteps[i].clone();

        }
        return clone;

    }

    public Grid2D getTimeStep(int timestep) {
        return timesteps[timestep];
    }

    public void setTimeStep(int timestep, Grid2D grid) {
        timesteps[timestep] = grid;
    }

    public double getValue(int timestep, int row, int col) {
        return timesteps[timestep].getCell(row, col).getValue();
    }

    public int getRowSize() {
        return timesteps[0].getRowSize();
    }

    public int getColSize() {
        return timesteps[0].getColSize();
    }

    public boolean setValue(int time, int row, int col, double value) {
        return timesteps[time].setValue(row, col, value);
    }

    public int getTimeStepsNum() {
        return timesteps.length;
    }

    public double getMaxValue() {
        double maxVal = Double.MIN_VALUE;
        for (Grid2D iter : timesteps) {
            maxVal = Math.max(maxVal, iter.getMaxValue());
        }
        return maxVal;
    }
    
    public double getMinValue() {
        double minVal = Double.MAX_VALUE;
        for (Grid2D iter : timesteps) {
            minVal = Math.min(minVal, iter.getMinValue());
        }
        return minVal;
    }
}
