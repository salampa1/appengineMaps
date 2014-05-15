package development;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point2d;

/**
 *
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public abstract class HarborProvider {
    private static List<Point2d> smugglerEntryPoints;
    private static List<Point2d> smugglerExitPoints;
    
    private static void initSmugglerEntryHarbors() {
        smugglerEntryPoints = new ArrayList<>();
        smugglerEntryPoints.add(new Point2d(-78.25,2.75));
        smugglerEntryPoints.add(new Point2d(-78.75,2.25));
        smugglerEntryPoints.add(new Point2d(-79.25,1.75));
        smugglerEntryPoints.add(new Point2d(-79.75,1.25));
        smugglerEntryPoints.add(new Point2d(-80.25,0.75));
    }
    
    private static void initSmugglerExitHarbors() {
        smugglerExitPoints = new ArrayList<>();
        smugglerExitPoints.add(new Point2d(-90.25,13.75));
        smugglerExitPoints.add(new Point2d(-91.25,13.75));
        smugglerExitPoints.add(new Point2d(-92.25,14.25));
        smugglerExitPoints.add(new Point2d(-93.25,15.25));
        smugglerExitPoints.add(new Point2d(-94.25,15.75));
    }

    public static List<Point2d> getSmugglerEntryPoints() {
        if (smugglerEntryPoints == null) {
            initSmugglerEntryHarbors();
        }
        return smugglerEntryPoints;
    }

    public static List<Point2d> getSmugglerExitPoints() {
        if (smugglerExitPoints == null) {
            initSmugglerExitHarbors();
        }
        return smugglerExitPoints;
    }
    
    
    
}
