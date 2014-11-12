package retrieval;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import thor.Model;
import thor.graphics.Point3D;
import thor.model.io.ModelIO;
import thor.modelanalysis.utils.Normalize;

public class ShapeFunctionTester {
    public static void main(String[] args) throws IllegalArgumentException, IOException {
        final String modelFilename = args[0];
        final int numShapeFunctionSamples = 100000;
        
        final String modelD1Filename = modelFilename+".D1"+".png";
        final String modelD2Filename = modelFilename+".D2"+".png";
        final String modelD3Filename = modelFilename+".D3"+".png";
        final String modelD4Filename = modelFilename+".D4"+".png";
        final String modelA3Filename = modelFilename+".A3"+".png";
        
        Model model = ModelIO.read(new File(modelFilename));
        Normalize.translation(model);
        Normalize.scale(model);
        
        //Test D1
        testD1(numShapeFunctionSamples, modelD1Filename, model);
        //Test D2
        testD2(numShapeFunctionSamples, modelD2Filename, model);
        //Test D3
        testD3(numShapeFunctionSamples, modelD3Filename, model);
        //Test D4
        testD4(numShapeFunctionSamples, modelD4Filename, model);
        //Test A3
        testA3(numShapeFunctionSamples, modelA3Filename, model);
    }

    private static void testD1(final int numShapeFunctionSamples,
            final String outputFilename, Model model) throws IOException {
        Point3D fixedPoint = model.getBarycenter();
        UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(model.getMeshes().get(0));
        Point3D[] uniformPoints = getSamplerPoints(sampler, numShapeFunctionSamples);
        
        Point3D[] d2Points = new Point3D[numShapeFunctionSamples*2];
        for(int i=0; i<numShapeFunctionSamples; i++) {
            d2Points[i*2] = fixedPoint;
            d2Points[i*2+1] = uniformPoints[i];
        }
        
        double[] d1Values = ShapeFunction.D2.computeValues(numShapeFunctionSamples, d2Points);
        
        BufferedImage d1Image = ShapeFunctionDrawer.D1.draw(d2Points,d1Values);
        ImageIO.write(d1Image, "png", new File(outputFilename));
    }
    
    private static void testD2(final int numShapeFunctionSamples,
            final String outputFilename, Model model) throws IOException {
        UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(model.getMeshes().get(0));
        Point3D[] d2Points = getSamplerPoints(sampler, numShapeFunctionSamples*2);
        
        double[] d1Values = ShapeFunction.D2.computeValues(numShapeFunctionSamples, d2Points);
        
        BufferedImage d1Image = ShapeFunctionDrawer.D2.draw(d2Points,d1Values);
        ImageIO.write(d1Image, "png", new File(outputFilename));
    }
    
    private static void testD3(final int numShapeFunctionSamples,
            final String outputFilename, Model model) throws IOException {
        UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(model.getMeshes().get(0));
        Point3D[] d3Points = getSamplerPoints(sampler, numShapeFunctionSamples*3);
        
        double[] d3Values = ShapeFunction.D3.computeValues(numShapeFunctionSamples, d3Points);
        
        BufferedImage d3Image = ShapeFunctionDrawer.D3.draw(d3Points,d3Values);
        ImageIO.write(d3Image, "png", new File(outputFilename));
    }
    
    private static void testD4(final int numShapeFunctionSamples,
            final String outputFilename, Model model) throws IOException {
        UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(model.getMeshes().get(0));
        Point3D[] d4Points = getSamplerPoints(sampler, numShapeFunctionSamples*4);
        
        double[] d4Values = ShapeFunction.D4.computeValues(numShapeFunctionSamples, d4Points);
        
        BufferedImage d4Image = ShapeFunctionDrawer.D4.draw(d4Points,d4Values);
        ImageIO.write(d4Image, "png", new File(outputFilename));
    }
    
    private static void testA3(final int numShapeFunctionSamples,
            final String outputFilename, Model model) throws IOException {
        UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(model.getMeshes().get(0));
        Point3D[] a3Points = getSamplerPoints(sampler, numShapeFunctionSamples*3);
        
        double[] a3values = ShapeFunction.A3.computeValues(numShapeFunctionSamples, a3Points);
        
        BufferedImage a3Image = ShapeFunctionDrawer.A3.draw(a3Points, a3values);
        ImageIO.write(a3Image, "png", new File(outputFilename));
    }
    
    private static Point3D[] getSamplerPoints(UniformMeshSurfaceSampler sampler, int numPoints) {
        Point3D[] points = new Point3D[numPoints];
        for (int i=0; i<numPoints; ++i) {
            points[i] = sampler.getPoint();
        }
        return points;
    }
}
