package retrieval;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
    
    public static void main(String[] args) throws IllegalArgumentException, IOException {

        /*
         * Parameters setup.
         */
        //Get input directory path and output directory path.
        String inputDirectory;
        String outputDirectory;
        switch (args.length) {
        case 2:
            inputDirectory = args[0];
            outputDirectory = args[1];
            break;
        default:
            inputDirectory = "model-samples";
            outputDirectory = "results";
            break;
        }
        inputDirectory = inputDirectory+"/";
        outputDirectory = outputDirectory+"/";
        
        //Setup sampling and descriptor parameters.
        // - Setup the number of shape function values.
        final int shapeFunctionSamples = 1000000;
        // - Setup the number of bins in the shape distributions.
        final int shapeDistrBins = 64;
        // - Setup normalization method to align shape distributions.
        NormalizationMethod normMethod = NormalizationMethod.MEAN;
        
        //Create output directory if it doesn't exist.
        Path outDirPath = Paths.get(outputDirectory);
        Files.createDirectories(outDirPath);
        final String outTextFilename = outputDirectory+"featureVectors"+".txt";
        
        /*
         * Model processing.
         */
        
        BufferedWriter outTextWriter = new BufferedWriter(new FileWriter(outTextFilename));
        
        //Get paths every PLY file in the input directory.
        Path modelDir = Paths.get(inputDirectory);
        DirectoryStream<Path> dirStream = Files.newDirectoryStream(modelDir,"*.{ply}");
        for (Path modelPath : dirStream) {
            //Write model header in text output.
            outTextWriter.write("Model: "+modelPath.toString()+"\n");
            
            //Load model.
            Model model = ModelIO.read(modelPath.toFile());
            //Normalize model.
            Normalize.translation(model);
            Normalize.scale(model);
            
            //Compute point cloud. (with enough points for all shape functions)
            final int requiredPointNum = shapeFunctionSamples * 4;
            UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(model.getMeshes().get(0));
            ArrayList<Point3D> points = new ArrayList<Point3D>(requiredPointNum);
            for (int p = 0; p < requiredPointNum; p++) {
                points.add(sampler.getPoint());
            }
            
            
            //Compute and output A3 histogram.
            double[] a3Values = ShapeFunction.A3.computeValues(shapeFunctionSamples, points.toArray(new Point3D[0]));
            ShapeDistribution a3Distr = normMethod.createShapeDistribution(ShapeFunction.A3, shapeDistrBins, a3Values);
            outTextWriter.write("A3-"+normMethod.name()+":");
            dumpShapeDistrToWriterWithSamples(a3Distr,outTextWriter,shapeFunctionSamples);
            outTextWriter.write("\n");
            BufferedImage img = ShapeDistributionDrawer.drawShapeDistribution(a3Distr);
            ImageIO.write(img, "png", new File(outputDirectory+modelPath.getFileName().toString()+"-A3-"+normMethod.name()+"-"+".png"));
            
            //Compute and output D1 histogram.
            Point3D barycenter = model.getBarycenter();
            Point3D[] d1Points = new Point3D[shapeFunctionSamples*2];
            for (int i = 0; i < shapeFunctionSamples; i++) {
                d1Points[i*2] = barycenter;
                d1Points[i*2+1] = points.get(i);
            }
            double[] d1Values = ShapeFunction.D2.computeValues(shapeFunctionSamples, d1Points);
            ShapeDistribution d1Distr = normMethod.createShapeDistribution(ShapeFunction.D2, shapeDistrBins, d1Values);
            outTextWriter.write("D1-"+normMethod.name()+":");
            dumpShapeDistrToWriterWithSamples(d1Distr,outTextWriter,shapeFunctionSamples);
            outTextWriter.write("\n");
             img = ShapeDistributionDrawer.drawShapeDistribution(d1Distr);
            ImageIO.write(img, "png", new File(outputDirectory+modelPath.getFileName().toString()+"-D1-"+normMethod.name()+"-"+".png"));
            
            //Compute and output D2 histogram.
            double[] d2Values = ShapeFunction.D2.computeValues(shapeFunctionSamples, points.toArray(new Point3D[0]));
            ShapeDistribution d2Distr = normMethod.createShapeDistribution(ShapeFunction.D2, shapeDistrBins, d2Values);
            outTextWriter.write("D2-"+normMethod.name()+":");
            dumpShapeDistrToWriterWithSamples(d2Distr,outTextWriter,shapeFunctionSamples);
            outTextWriter.write("\n");
             img = ShapeDistributionDrawer.drawShapeDistribution(d2Distr);
            ImageIO.write(img, "png", new File(outputDirectory+modelPath.getFileName().toString()+"-D2-"+normMethod.name()+"-"+".png"));
            
            //Compute and output D3 histogram.
            double[] d3Values = ShapeFunction.D3.computeValues(shapeFunctionSamples, points.toArray(new Point3D[0]));
            ShapeDistribution d3Distr = normMethod.createShapeDistribution(ShapeFunction.D3, shapeDistrBins, d3Values);
            outTextWriter.write("D3-"+normMethod.name()+":");
            dumpShapeDistrToWriterWithSamples(d3Distr,outTextWriter,shapeFunctionSamples);
            outTextWriter.write("\n");
             img = ShapeDistributionDrawer.drawShapeDistribution(d3Distr);
            ImageIO.write(img, "png", new File(outputDirectory+modelPath.getFileName().toString()+"-D3-"+normMethod.name()+"-"+".png"));
            
            //Compute and output D4 histogram.
            double[] d4Values = ShapeFunction.D4.computeValues(shapeFunctionSamples, points.toArray(new Point3D[0]));
            ShapeDistribution d4Distr = normMethod.createShapeDistribution(ShapeFunction.D4, shapeDistrBins, d4Values);
            outTextWriter.write("D4-"+normMethod.name()+":");
            dumpShapeDistrToWriterWithSamples(d4Distr,outTextWriter,shapeFunctionSamples);
            outTextWriter.write("\n");
             img = ShapeDistributionDrawer.drawShapeDistribution(d4Distr);
            ImageIO.write(img, "png", new File(outputDirectory+modelPath.getFileName().toString()+"-D4-"+normMethod.name()+"-"+".png"));
            
            
        }
        
        outTextWriter.flush();
        outTextWriter.close();
    }
    
    private static void dumpShapeDistrToWriterWithSamples(ShapeDistribution shapeDistr, Writer writer, int samples) throws IOException {
        double samples_i = 1.0 / samples;
        for(Integer i: shapeDistr.getBins()) {
            writer.write(" "+i*samples_i);
        }
    }
}
