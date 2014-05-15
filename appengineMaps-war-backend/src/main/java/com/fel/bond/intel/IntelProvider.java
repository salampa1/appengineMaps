package com.fel.bond.intel;

import com.fel.bond.utility.Rand;
import com.fel.bond.grids.Grid2D;
import com.fel.bond.grids.TimeGrid;
import com.fel.bond.utility.Gaussian2d;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.vecmath.Point2d;
import org.joda.time.DateTime;

/**
 *
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public class IntelProvider {
    // percent of the whole timespan in which smugglers sail out
    public static final double ENTRY_TIME_LIMIT = 0.35; 
    
    // number of smuggler trajectories to embed
    public static final int trajectoriesCount = 200;

    /**
     * Generates smuggler intelligence data.
     * @param topLeftDeg
     * @param bottomRightDeg
     * @param cellEdgeSizeDeg
     * @param date
     * @param timestepsNum
     * @param timestepDurationHours
     * @return
     */
    public static TimeGrid generateIntel(Point2d topLeftDeg,
            Point2d bottomRightDeg, double cellEdgeSizeDeg, DateTime date,
            int timestepsNum, int timestepDurationHours) {
        Random random = Rand.getRandom();

        List<Point2d> smugglerEntryPoints = HarborProvider.getSmugglerEntryPoints();
        List<Point2d> smugglerExitPoints = HarborProvider.getSmugglerExitPoints();

        List<Smuggler> smugglers = new ArrayList<>(trajectoriesCount);

        for (int i = 0; i < trajectoriesCount; i++) {
            Point2d entryPoint = smugglerEntryPoints.get(random.nextInt(
                    smugglerEntryPoints.size()));
            Point2d exitPoint = smugglerExitPoints.get(random.nextInt(
                    smugglerEntryPoints.size()));
            int entryTimestep = random.nextInt((int) Math.round(timestepsNum *
                    ENTRY_TIME_LIMIT));

            smugglers.add(new Smuggler(entryPoint, exitPoint, entryTimestep,
                    Smuggler.EASTERN_INDIAN_OCEAN));
        }

        TimeGrid intel = new TimeGrid(timestepsNum, date, timestepDurationHours);
        Grid2D dummy = new Grid2D(topLeftDeg, bottomRightDeg, cellEdgeSizeDeg);

        List<Gaussian2d> toInclude = new ArrayList<>();

        for (int t = 0; t < timestepsNum; t++) {
            Grid2D timeStep = dummy.clone();
            toInclude.clear();

            for (Smuggler smuggler : smugglers) {
                if (smuggler.isActive(t)) {
                    toInclude.add(smuggler.getGaussianAndMove(cellEdgeSizeDeg));
                }
            }
            timeStep.includeGaussians(toInclude);
            intel.setTimeStep(t, timeStep);

        }

        return intel;
    }
}
