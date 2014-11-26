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
        
        //Load and normalize model.
        Model model = ModelIO.read(new File(modelFilename));
        Normalize.translation(model);
        Normalize.scale(model);
        
        //Compute necessary points.
        final int requiredPointNum = shapeFunctionSamples * 2;
        UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(model.getMeshes().get(0));
        ArrayList<Point3D> points = new ArrayList<Point3D>(requiredPointNum);
        for (int p = 0; p < requiredPointNum; p++) {
            points.add(sampler.getPoint());
        }
        
        //Compute shape function values.
        double[] shapeFunctionValues = ShapeFunction.D2.computeValues(shapeFunctionSamples, points.toArray(new Point3D[0]));
        
        //Create and fill shape distribution.
        ShapeDistribution distr = new ShapeDistribution(shapeDistrBins, 0.0, Math.sqrt(3));
        distr.addSamples(shapeFunctionValues);
        
        //Create and saved visual representation of the shape distribution.
        BufferedImage img = ShapeDistributionDrawer.drawShapeDistribution(distr);
        ImageIO.write(img, "png", new File(outputFilename));
    }

}
