package com.fel.bond.grids;

import com.fel.bond.utility.Rand;
import com.fel.bond.utility.Gaussian2d;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point2d;

/**
 *
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public class Grid2D implements Cloneable, Serializable {

    private Cell[][] grid; // [positionY][positionX]
    private int rows;
    private int cols;
    private static final long serialVersionUID = 4571L;
    private double cellEdgeSizeDegrees;
    private double min, max;
    private boolean minInit = false, maxInit = false;

    public Grid2D(Point2d topLeft, Point2d bottomRight, double cellEdgeSize) {
        if (topLeft.x >= bottomRight.x || topLeft.y <= bottomRight.y) {
            throw new IllegalArgumentException();
        }

        cellEdgeSizeDegrees = cellEdgeSize;
        rows = 0;
        cols = 0;

        double longitude = topLeft.x;
        while (longitude < bottomRight.x) {
            cols++;
            longitude += cellEdgeSize;
        }

        double latitude = topLeft.y;
        while (latitude > bottomRight.y) {
            rows++;
            latitude -= cellEdgeSize;
        }

        grid = new Cell[rows][cols];

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                double centerX = topLeft.x + col * cellEdgeSize + 0.5 * cellEdgeSize;
                double centerY = topLeft.y - row * cellEdgeSize - 0.5 * cellEdgeSize;

                grid[row][col] = new Cell(centerX, centerY);
            }
        }

    }

    /**
     * Creates Grid2d with random values.
     * @param rows
     * @param cols
     * @param minValue minimal random value
     * @param maxValue maximal random value
     */
    public Grid2D(int rows, int cols, double minValue, double maxValue) {
        this.rows = rows;
        this.cols = cols;
        grid = new Cell[rows][cols];

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                grid[row][col] = new Cell(Rand.nextDoubleBetween(minValue,
                        maxValue));
            }
        }

    }
    
    public Grid2D(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Cell[rows][cols];

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                grid[row][col] = new Cell();
            }
        }
    }

    public Grid2D() {
    }

    @Override
    public Grid2D clone() {
        Grid2D clone = new Grid2D();
        clone.grid = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                clone.grid[i][j] = grid[i][j].clone();
            }
        }
        clone.cols = cols;
        clone.rows = rows;
        clone.cellEdgeSizeDegrees = cellEdgeSizeDegrees;
        clone.min = min;
        clone.max = max;
        clone.minInit =  minInit;
        clone.maxInit = maxInit;

        return clone;

    }

    public double getMinValue() {
        if (!minInit) {
            min = Double.MAX_VALUE;
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    min = Math.min(grid[i][j].getValue(), min);
                }
            }
            minInit = true;
        }
        return min;
    }

    public double getMaxValue() {
        if (!maxInit) {
            max = Double.MIN_VALUE;
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    max = Math.max(grid[i][j].getValue(), max);
                }
            }
        }
        return max;
    }

    public int getRowSize() {
        return rows;
    }

    public int getColSize() {
        return cols;
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    public double getCellEdgeSizeDegrees() {
        return cellEdgeSizeDegrees;
    }
    
    public static int linearizePosition(int rowPos, int colPos, int rowSize) {
        return colPos * rowSize + rowPos;
    }

    /**
     * 
     * @param linearizedPosition
     * @param rowSize
     * @return row index of given linearized position
     */
    public static int getRowIndex(int linearizedPosition, int rowSize) {
        return linearizedPosition % rowSize;
    }

    /**
     * 
     * @param linearizedPosition
     * @param rowSize
     * @return column index of given linearized position
     */
    public static int getColIndex(int linearizedPosition, int rowSize) {
        return linearizedPosition / rowSize;
    }
    
    /**
     * 
     * @param point
     * @return linearized position of cell containing given point
     */
    public int getLinearizedPosition(Point2d point) {
        double topLeftX = getCell(0, 0).getCenter().x - cellEdgeSizeDegrees/2;
        double topLeftY = getCell(0, 0).getCenter().y - cellEdgeSizeDegrees/2;
        
        double xDiff = Math.abs(topLeftX - point.x);
        double yDiff = Math.abs(topLeftY - point.y);
        
        int rowIndex = (int) Math.floor(yDiff/cellEdgeSizeDegrees);
        int colIndex = (int) Math.floor(xDiff/cellEdgeSizeDegrees);

        if (rowIndex > rows) {
            rowIndex = rows;
        }
        if (colIndex > cols) {
            colIndex = cols;
        }
        return linearizePosition(rowIndex, colIndex, rows);
    }

    /**
     * Adds values of gaussians to the cell values.
     * @param gaussians 
     */
    public void includeGaussians(List<Gaussian2d> gaussians) {
        
        if (gaussians.isEmpty()) {
            return;
        }

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                Cell cell = grid[row][col];
                for (Gaussian2d gauss : gaussians) {
                    cell.setValue(cell.getValue() +
                      gauss.getValueAt(cell.getCenter().x, cell.getCenter().y));
                }
            }
        }
    }

    public boolean setValue(int row, int col, double value) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            grid[row][col].setValue(value);
            return true;
        }
        return false;
    }


    public List<Point2d> getCellCorners(int row, int col) {
        List<Point2d> out = new ArrayList<>(4);

        Cell cell = getCell(row, col);
        Point2d center = cell.getCenter();

        double halfEdge = cellEdgeSizeDegrees / 2.0;

        out.add(new Point2d(center.x - halfEdge, center.y + halfEdge));
        out.add(new Point2d(center.x + halfEdge, center.y + halfEdge));
        out.add(new Point2d(center.x + halfEdge, center.y - halfEdge));
        out.add(new Point2d(center.x - halfEdge, center.y - halfEdge));

        return out;

    }
}
