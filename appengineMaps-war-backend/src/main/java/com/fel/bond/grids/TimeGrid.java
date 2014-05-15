package com.fel.bond.grids;

import java.io.Serializable;
import org.joda.time.DateTime;

/**
 * 2d grid with timesteps
 *
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public class TimeGrid implements Cloneable, Serializable {

    private Grid2D[] timesteps; // [timestep]
    //date and time variables to support visualisation to KML with time spans
    private int startTimeYear; 
    private int startTimeMonth;
    private int startTimeDay;
    private int startTimeHour;
    int timestepDurationHours;
    private static final long serialVersionUID = 1211L;

    public TimeGrid(int timeStepsNum, DateTime startTime, int timestepDurationHours) {
        startTimeYear = startTime.getYear();
        startTimeMonth = startTime.getMonthOfYear();
        startTimeDay = startTime.getDayOfMonth();
        startTimeHour = startTime.getHourOfDay();
        timesteps = new Grid2D[timeStepsNum];
        this.timestepDurationHours = timestepDurationHours;
    }
    
    public TimeGrid(int timeStepsNum, int rows, int cols, DateTime startTime,
            int timestepDurationHours) {
        this(timeStepsNum, startTime, timestepDurationHours);
        
        for (int i = 0; i < timesteps.length; i++) {
            timesteps[i] = new Grid2D(rows, cols);
        }
    }
    
    
    /**
     * Creates TimeGrid with random values.
     * @param timeStepsNum
     * @param rows size
     * @param cols size
     * @param minValue minimal random value
     * @param maxValue maximal random value
     */
    public TimeGrid(int timeStepsNum, int rows, int cols, double minValue,
            double maxValue) {
        this(timeStepsNum, new DateTime(), 12);
        for (int i = 0; i < timesteps.length; i++) {
            timesteps[i] = new Grid2D(rows, cols, minValue, maxValue);
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
        clone.startTimeYear = startTimeYear;
        clone.startTimeMonth = startTimeMonth;
        clone.startTimeDay = startTimeDay;
        clone.startTimeHour = startTimeHour;
        clone.timestepDurationHours = timestepDurationHours;
        return clone;

    }

    public DateTime getStartTime() {
        return new DateTime(startTimeYear, startTimeMonth, startTimeDay, 
                startTimeHour, 0);
    }

    public int getTimestepDurationHours() {
        return timestepDurationHours;
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
