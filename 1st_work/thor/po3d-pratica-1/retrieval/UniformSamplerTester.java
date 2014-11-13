package retrieval;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import thor.Model;
import thor.graphics.Point3D;
import thor.model.io.*;
import thor.modelanalysis.utils.Normalize;
import thor.modelanalysis.utils.Scene;

public class UniformSamplerTester {
    public static void main(String[] args) throws IllegalArgumentException, IOException {
        String modelFilename = args[0];
        
        String outputImageFile = modelFilename + ".png";
        String outputPointCloudFile = modelFilename + ".cloud" + ".png";
        
        Model model = ModelIO.read(new File(modelFilename));
        Normalize.translation(model);
        Normalize.scale(model);
        Normalize.rotation(model);
        
        //Render model view.
        Scene scene = new Scene();
        scene.addDrawable(model);
        BufferedImage view = scene.extractView();
        ImageIO.write(view, "png", new File(outputImageFile));
        
        //Generate surface point cloud.
        UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(model.getMeshes().get(0));
        final int numPoints = 10000;
        List<Point3D> points = new ArrayList<Point3D>(numPoints);
        for (int i=0; i<numPoints; ++i) {
            points.add(sampler.getPoint());
        }

        //Render the point cloud
        Model cloudModel = new PointCloudModel(points);
        Scene scenePointCloud = new Scene();
        scenePointCloud.setDimensions(2000, 2000);
        scenePointCloud.addDrawable(cloudModel);
        BufferedImage cloudView = scenePointCloud.extractView();
        ImageIO.write(cloudView, "png", new File(outputPointCloudFile));
    }
}

class PointCloudModel extends Model {

    private List<Point3D> _pointCloud;
    
    public PointCloudModel(List<Point3D> points) {
        super("superName", "pointcloud");
        _pointCloud = points;
    }
    
    @Override
    public void draw(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        //Hack: Try fix the view and projection of the scene.
        int[] currMode = new int[1];
        gl.glGetIntegerv(GL2.GL_MATRIX_MODE, currMode, 0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glScalef(2f, 2f, 2f);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glScalef(0.5f, 0.5f, 0.5f);
        
        //Actually render the point cloud.
        //gl.glColor3f(0.5f, 0.9f, 0.2f);
        gl.glBegin(GL2.GL_POINTS);
        for(Point3D p : _pointCloud) {
            gl.glVertex3d(p.getX(), p.getY(), p.getZ());
        }
        gl.glEnd();
        
        //Hack: Restore the state the matrix stacks.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(currMode[0]);
    }
}
