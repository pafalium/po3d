package retrieval;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;
import thor.graphics.Vector3D;
import thor.model.geoset.Vertex;
import thor.modelanalysis.utils.Scene;

public class ShapeDistributionDrawer {
    /**
     * Draw a line representation of a {@link ShapeDistribution}.
     * The graph is contained within the unit square on XY. 
     * @param distr the distribution to be drawn
     * @return an image with the result of the drawing
     */
    public static BufferedImage drawShapeDistribution(ShapeDistribution distr) {
        
        /*
         * Draw a line representation of a ShapeDistribution.
         * 
         */
        
        List<Integer> bins = distr.getBins();
        int maxBinValue = 0;
        for (Integer binVal : bins) {
            if (binVal > maxBinValue)
                maxBinValue = binVal;
        }
        
        int vertexNum = bins.size()*2;
        
        //Fill the colors list.
        Vector3D color = new Vector3D();
        ArrayList<Vector3D> colors = new ArrayList<Vector3D>(vertexNum+2);
        colors.add(color);
        for (int i = 0; i < vertexNum; i++) {
            colors.add(color);
        }
        colors.add(color);
        
        //Fill the vertices list according to the values of the bins.
        ArrayList<Point3D> vertices = new ArrayList<Point3D>(vertexNum+2);
        final double binValueScale = 1.0 / maxBinValue;
        final double binXPosScale = 1.0 / bins.size();
        vertices.add(new Vertex(0.0,0.0,0.0));
        for (int bin=0; bin<bins.size(); bin++) {
            Vertex binPoint = new Vertex((bin+0.5)*binXPosScale,bins.get(bin)*binValueScale,0.0); 
            vertices.add(binPoint);
            vertices.add(binPoint);
        }
        vertices.add(new Vertex((bins.size()+1)*binXPosScale,0.0,0.0));
        
        //Render the distribution.
        LineModel model = new LineModel(vertices, colors);
        Scene scene = new Scene();
        scene.setDimensions(2000, 2000);
        scene.setCameraPosition(0.0, 0.0, 0.5);
        scene.setCameraUp(0.0, 1.0, 0.0);
        scene.addDrawable(model);
        return scene.extractView();
    }
}
