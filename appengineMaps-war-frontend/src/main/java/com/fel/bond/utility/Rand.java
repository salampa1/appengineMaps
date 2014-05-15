package com.fel.bond.utility;

import java.util.Random;

/**
 *
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public class Rand {
    private static Random random = new java.util.Random(22117856651L);

    public static Random getRandom() {
        return random;
    }
    
    public static double nextDoubleBetween(double v1, double v2) {
        
        double max = Math.max(v1, v2);
        double min = Math.min(v1, v2);
        
        double nextDouble = random.nextDouble();
        
        double diff = max - min;
        
        return min + nextDouble * diff;
    }
}
