package com.fel.bond.intel;

import com.fel.bond.utility.Rand;
import com.fel.bond.utility.Gaussian2d;
import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Point2d;

/**
 * Class for representation of single smuggler vessel.
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public class Smuggler {

    public static final int EASTERN_INDIAN_OCEAN = 1;
    
     // percent of smugglers who use nondirect routes
    private static final double SMART_SMUGGLERS_PERCENT = 50;
    
     // smugglers make waypoint at 33% of the entry to exit distance
    private static final double WAYPOINT_DISTANCE = 0.33;
    
    public static final double DEFAULT_SIGMA_X = 1; // sigma orthogonal to trajectory
    public static final double DEFAULT_SIGMA_Y = 1.3; // sigma along trajectory
    public static final double GAUSS_INTENSITY = 1000; // multiplier for gaussians
    
    /*
     * Random vector is generated from the bias values. Then the vector is added to
     * the entry->exit direction of smuggler which creates waypoint for nondirect route.
     */
    private static final double xBiasMin = -90; //longitude
    private static final double xBiasMax = -80; //longitude
    private static final double yBiasMin = -3; //latitude
    private static final double yBiasMax = 3; //latitude
    
    List<Point2d> waypoints;
    int entryTimestep;
    Gaussian2d gaussian;

    public Smuggler(Point2d entry, Point2d exit, int entryTimestep, int region) {
        this.entryTimestep = entryTimestep;

        if (region == EASTERN_INDIAN_OCEAN) {
            waypoints = new LinkedList<>();

            if (Rand.getRandom().nextDouble() > 1 - SMART_SMUGGLERS_PERCENT/100d) {
                // this is a smart smuggler -> gets additional waypoint
                double xdiff = exit.x - entry.x;
                double ydiff = exit.y - entry.y;

                double waypointX = (xdiff * WAYPOINT_DISTANCE) + entry.x;
                double waypointY = (ydiff * WAYPOINT_DISTANCE) + entry.y;

                double randVectorX = Rand.nextDoubleBetween(xBiasMin, xBiasMax)
                        -entry.x;
                double randVectorY = Rand.nextDoubleBetween(yBiasMin, yBiasMax)
                        -entry.y;

                waypoints.add(new Point2d(waypointX + randVectorX, waypointY +
                        randVectorY));
            }
            waypoints.add(exit);
        }

        createGaussian(entry, waypoints.get(0));
    }

    public boolean isActive(int timestep) {
        return timestep >= entryTimestep && !waypoints.isEmpty();
    }

    private void createGaussian(Point2d from, Point2d to) {
        double xDiff = from.x - to.x;
        double yDiff = from.y - to.y;
        double hypotenuse = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        double theta = Math.acos(xDiff / hypotenuse); // clockwise rotation
        double deg = Math.toDegrees(theta);

        gaussian = new Gaussian2d(from.x,
                from.y,
                DEFAULT_SIGMA_X,
                DEFAULT_SIGMA_Y,
                theta);

        gaussian.setMultiplier(GAUSS_INTENSITY);
    }

    /**
     * Triggers smuggler to move the "one timestep" distance
     * @param movementLengthDegrees
     * @return gaussian representing smuggler detectability in previous timestep
     */
    Gaussian2d getGaussianAndMove(double movementLengthDegrees) {
        Gaussian2d old = gaussian;

        Point2d currentLocation = new Point2d(gaussian.getMix(), gaussian.getMiy());

        double xdiff = waypoints.get(0).x - currentLocation.x;
        double ydiff = waypoints.get(0).y - currentLocation.y;
        double dist = Math.sqrt(xdiff * xdiff + ydiff * ydiff);

        double nextLocationX = (xdiff / dist) * movementLengthDegrees +
                currentLocation.x;
        double nextLocationY = (ydiff / dist) * movementLengthDegrees +
                currentLocation.y;

        Point2d nextLocation = new Point2d(nextLocationX, nextLocationY);

        if (currentLocation.distance(waypoints.get(0)) < movementLengthDegrees) {
            // arrived near waypoint
            waypoints.remove(0);
        }

        if (!waypoints.isEmpty()) {
            createGaussian(nextLocation, waypoints.get(0));
        }

        return old;

    }
}
