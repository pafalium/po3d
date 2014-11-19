package retrieval;

import java.util.Arrays;

public enum NormalizationMethod {
    MAX {
        public ShapeDistribution createShapeDistribution(ShapeFunction shapeFunction, int binNum, double[] values) {
            Arrays.sort(values);
            double max = values[values.length-1];;
            ShapeDistribution distr = new ShapeDistribution(binNum, shapeFunction.getMinBoundary(), max);
            distr.addSamples(values);
            return distr;
        }
    },
    
    MEAN {
        public ShapeDistribution createShapeDistribution(ShapeFunction shapeFunction, int binNum, double[] values) {
            Arrays.sort(values);
            double max = values[values.length-1];
            double sum = 0.0;
            for (int i = 0; i < values.length; i++) {
                sum+=values[i];
            }
            final double mean = sum/values.length;
            final double shiftFromMean = Math.max(Math.abs(mean-max), Math.abs(mean-shapeFunction.getMinBoundary()));
            
            ShapeDistribution distr = new ShapeDistribution(binNum, mean-shiftFromMean, mean+shiftFromMean);
            distr.addSamples(values);
            return distr;
        }
    },
    
    DOMAIN {
        public ShapeDistribution createShapeDistribution(ShapeFunction shapeFunction, int binNum, double[] values) {
            ShapeDistribution distr = new ShapeDistribution(binNum, shapeFunction.getMinBoundary(), shapeFunction.getMaxBoundary());
            distr.addSamples(values);
            return distr;
        }
    };

    public abstract ShapeDistribution createShapeDistribution(ShapeFunction shapeFunction,
            int binNum, double[] values) ;
}