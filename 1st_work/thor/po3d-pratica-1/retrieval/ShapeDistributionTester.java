package retrieval;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import thor.Model;
import thor.graphics.Point3D;
import thor.model.io.ModelIO;
import thor.modelanalysis.utils.Normalize;

public class ShapeDistributionTester {

    public static void main(String[] args) throws IllegalArgumentException, IOException {
        final String modelFilename = args[0];
        final int shapeFunctionSamples = 1000000;
        final int shapeDistrBins = 64;
        
        final String outputFilename = modelFilename+".distr"+".png";
        
        Model model = ModelIO.read(new File(modelFilename));
        Normalize.translation(model);
        Normalize.scale(model);
        
        final int requiredPointNum = shapeFunctionSamples * 2;
        UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(model.getMeshes().get(0));
        ArrayList<Point3D> points = new ArrayList<Point3D>(requiredPointNum);
        for (int p = 0; p < requiredPointNum; p++) {
            points.add(sampler.getPoint());
        }
        
        double[] shapeFunctionValues = ShapeFunction.D2.computeValues(shapeFunctionSamples, points.toArray(new Point3D[0]));
        
        ShapeDistribution distr = new ShapeDistribution(shapeDistrBins, 0.0, Math.sqrt(3));
        distr.addSamples(shapeFunctionValues);
        
        BufferedImage img = ShapeDistributionDrawer.drawShapeDistribution(distr);
        ImageIO.write(img, "png", new File(outputFilename));
    }

}
