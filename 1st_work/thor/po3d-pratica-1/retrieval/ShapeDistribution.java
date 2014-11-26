package retrieval;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * A shape distribution represents the PDF of a characteristic of a model.
 * It keeps record, for an interval divided in a number of bins, the amount of samples
 * whose value fell on that bin.
 */
public class ShapeDistribution {
    
    private final ArrayList<Integer> _bins;
    private final double _rightBound;
    private final double _leftBound;
    
    public ShapeDistribution(int binNumber, double leftBound, double rightBound) {
        _rightBound = rightBound;
        _leftBound = leftBound;
        _bins = new ArrayList<Integer>(binNumber);
        Integer zero = Integer.valueOf(0);
        for (int bin = 0; bin < binNumber; bin++) {
            _bins.add(zero);
        }
        
    }
    
    /**
     * Put each of the newValues into the ShapeDistribuion's bins.
     * @param newValues the values to be taken into account by the ShapeDistribution
     */
    public void addSamples(double[] newValues) {
        //foreach sample: compute index; increment bin;
        final double distrIntervalLen_i = 1.0 / (_rightBound-_leftBound);
        final int binsNum = _bins.size()-1;
        for (double val : newValues) {
            //if val <= leftbound -> 0
            //if val >= rightbound -> _bins.length-1
            //From (leftBound, rightBound) to (0, 1),
            double normalized = (val - _leftBound) * distrIntervalLen_i;
            double clamped = clamp(normalized, 0.0, 0.99999999999999999);//Hack: clamped can't be equal to one otherwise bin can be out of bounds.
            int bin = (int) (clamped * binsNum);
            _bins.set(bin, _bins.get(bin)+1);
        }
    }
    
    /**
     * Get an unmodifiable list containing the values of the bins of the distribution.
     * <br/>
     * Note: This method might be later modified to return a list of doubles between 0 and 1.
     * @return the unmodifiable list
     */
    public List<Integer> getBins() {
        return Collections.unmodifiableList(_bins);
    }
    
    public double getRightBound() {
        return _rightBound;
    }
    
    public double getLeftBound() {
        return _leftBound;
    }
    
    /**
     * Utility function. Implements clamp functionality present in GLSL, etc.
     */
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(val, max));
    }
}
