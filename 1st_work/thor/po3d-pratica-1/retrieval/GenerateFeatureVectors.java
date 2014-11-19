package retrieval;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import thor.Model;
import thor.graphics.Point3D;
import thor.model.io.ModelIO;
import thor.modelanalysis.utils.Normalize;

public class GenerateFeatureVectors {
    //TODO calcular shape distribution para cada shape function
    //TODO ter vários métodos de normalização de shape distributions (MAX, MEAN, NONE)
    //TODO escrever shape distributions de um modelo para um ficheiro
    
    public static void main(String[] args) throws IllegalArgumentException, IOException {
        //final String modelFilename = args[0];
        final int shapeFunctionSamples = 1000000;
        final int shapeDistrBins = 64;
        
        String resultDirectory = "results/";
        final String outputFilename = resultDirectory+"featureVectors"+".txt";
        NormalizationMethod normMethod = NormalizationMethod.MEAN;
        
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));
        
        Path modelDir = Paths.get("model-samples");
        DirectoryStream<Path> dirStream = Files.newDirectoryStream(modelDir,"*.{ply}");
        for (Path modelPath : dirStream) {
            
            writer.write("Model: "+modelPath.toString()+"\n");
            
            Model model = ModelIO.read(modelPath.toFile());
            Normalize.translation(model);
            Normalize.scale(model);
            
            final int requiredPointNum = shapeFunctionSamples * 4;
            UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(model.getMeshes().get(0));
            ArrayList<Point3D> points = new ArrayList<Point3D>(requiredPointNum);
            for (int p = 0; p < requiredPointNum; p++) {
                points.add(sampler.getPoint());
            }
            
            
            
            double[] a3Values = ShapeFunction.A3.computeValues(shapeFunctionSamples, points.toArray(new Point3D[0]));
            ShapeDistribution a3Distr = normMethod.createShapeDistribution(ShapeFunction.A3, shapeDistrBins, a3Values);
            writer.write("A3-"+normMethod.name()+":");
            for(Integer i: a3Distr.getBins()) {
                writer.write(" "+i);
            }
            writer.write("\n");
            BufferedImage img = ShapeDistributionDrawer.drawShapeDistribution(a3Distr);
            ImageIO.write(img, "png", new File(resultDirectory+modelPath.getFileName().toString()+"-A3-"+normMethod.name()+"-"+".png"));
            
            Point3D barycenter = model.getBarycenter();
            Point3D[] d1Points = new Point3D[shapeFunctionSamples*2];
            for (int i = 0; i < shapeFunctionSamples; i++) {
                d1Points[i*2] = barycenter;
                d1Points[i*2+1] = points.get(i);
            }
            double[] d1Values = ShapeFunction.D2.computeValues(shapeFunctionSamples, d1Points);
            ShapeDistribution d1Distr = normMethod.createShapeDistribution(ShapeFunction.D2, shapeDistrBins, d1Values);
            writer.write("D1-"+normMethod.name()+":");
            for(Integer i: d1Distr.getBins()) {
                writer.write(" "+i);
            }
            writer.write("\n");
             img = ShapeDistributionDrawer.drawShapeDistribution(d1Distr);
            ImageIO.write(img, "png", new File(resultDirectory+modelPath.getFileName().toString()+"-D1-"+normMethod.name()+"-"+".png"));
            
            double[] d2Values = ShapeFunction.D2.computeValues(shapeFunctionSamples, points.toArray(new Point3D[0]));
            ShapeDistribution d2Distr = normMethod.createShapeDistribution(ShapeFunction.D2, shapeDistrBins, d2Values);
            writer.write("D2-"+normMethod.name()+":");
            for(Integer i: d2Distr.getBins()) {
                writer.write(" "+i);
            }
            writer.write("\n");
             img = ShapeDistributionDrawer.drawShapeDistribution(d2Distr);
            ImageIO.write(img, "png", new File(resultDirectory+modelPath.getFileName().toString()+"-D2-"+normMethod.name()+"-"+".png"));
            
            double[] d3Values = ShapeFunction.D3.computeValues(shapeFunctionSamples, points.toArray(new Point3D[0]));
            ShapeDistribution d3Distr = normMethod.createShapeDistribution(ShapeFunction.D3, shapeDistrBins, d3Values);
            writer.write("D3-"+normMethod.name()+":");
            for(Integer i: d3Distr.getBins()) {
                writer.write(" "+i);
            }
            writer.write("\n");
             img = ShapeDistributionDrawer.drawShapeDistribution(d3Distr);
            ImageIO.write(img, "png", new File(resultDirectory+modelPath.getFileName().toString()+"-D3-"+normMethod.name()+"-"+".png"));
            
            double[] d4Values = ShapeFunction.D4.computeValues(shapeFunctionSamples, points.toArray(new Point3D[0]));
            ShapeDistribution d4Distr = normMethod.createShapeDistribution(ShapeFunction.D4, shapeDistrBins, d4Values);
            writer.write("D4-"+normMethod.name()+":");
            for(Integer i: d4Distr.getBins()) {
                writer.write(" "+i);
            }
            writer.write("\n");
             img = ShapeDistributionDrawer.drawShapeDistribution(d4Distr);
            ImageIO.write(img, "png", new File(resultDirectory+modelPath.getFileName().toString()+"-D4-"+normMethod.name()+"-"+".png"));
            
            
        }
        
        writer.flush();
        writer.close();
    }
}
