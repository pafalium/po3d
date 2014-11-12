package retrieval;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import thor.Model;
import thor.graphics.Point3D;
import thor.graphics.Vector3D;
import thor.modelanalysis.utils.Scene;

public enum ShapeFunctionDrawer {
    
    /*
     * In order to draw an image from a set of results of a shape function
     * we are using meshes made of only line segments. 
     */
    
    
    A3 {
        @Override
        public BufferedImage draw(Point3D[] points, double[] values) {
            //Draw lines between each value's "source" points.
            //Each value has three source points so two line segments are needed.
            //The color computation assumes that the values are in [-1, 1].
            int vertexNum = values.length * 2;
            ArrayList<Vector3D> vertexColors = new ArrayList<Vector3D>(vertexNum);
            ArrayList<Point3D> vertices = new ArrayList<Point3D>(vertexNum);
            for (int valIdx = 0; valIdx < values.length; valIdx++) {
                //Fill the vertex colors array
                Vector3D color = new Vector3D(0.5*(values[valIdx]+1), 0.0, -0.5*values[valIdx]+1);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                //Fill the vertices array
                vertices.add(points[valIdx*3]);
                vertices.add(points[valIdx*3+1]);
                vertices.add(points[valIdx*3+1]);
                vertices.add(points[valIdx*3+2]);
            }
            LineModel model = new LineModel(vertices, vertexColors);
            
            return drawModel(model);
        }
    },
    
    D1 {
        @Override
        public BufferedImage draw(Point3D[] points, double[] values) {
            //Draw a line from the fixed point to each of the other values' points.
            //So we have to draw one line per value.
            //The color computation assumes that the values are in [0, sqrt(3)].
            //   sqrt(3) -> length of the diagonal of the unit cube.
            //It is assumed that D2 was used to generate the values of D1 and from that
            //    it is also assumed that the first point of each value is the fixed point.
            int vertexNum = values.length;
            ArrayList<Vector3D> vertexColors = new ArrayList<Vector3D>(vertexNum);
            ArrayList<Point3D> vertices = new ArrayList<Point3D>(vertexNum);
            final double sqrt_3_i = 1 / Math.sqrt(3);
            final Point3D fixedPoint = points[0];
            final Vector3D fixedPointColor = new Vector3D();
            for (int valIdx = 0; valIdx < values.length; valIdx++) {
                //Fill colors
                Vector3D color = new Vector3D(sqrt_3_i*values[valIdx], 0.0, 1-sqrt_3_i*values[valIdx]);
                vertexColors.add(fixedPointColor);
                vertexColors.add(color);
                //Fill vertices
                vertices.add(fixedPoint);
                vertices.add(points[valIdx*2+1]);
            }
            LineModel model = new LineModel(vertices, vertexColors);
            
            return drawModel(model);
        }
    },
    
    D2 {
        @Override
        public BufferedImage draw(Point3D[] points, double[] values) {
            //Draw a line between each pair of points of each value.
            // So we have to draw one line per value.
            // The color computation assumes that the values are in [0, sqrt(3)].
            //   sqrt(3) -> length of the diagonal of the unit cube.
            int vertexNum = values.length;
            ArrayList<Vector3D> vertexColors = new ArrayList<Vector3D>(
                    vertexNum);
            ArrayList<Point3D> vertices = new ArrayList<Point3D>(vertexNum);
            final double maxLength_i = 1 / Math.sqrt(3);
            for (int valIdx = 0; valIdx < values.length; valIdx++) {
                //Fill colors
                Vector3D color = new Vector3D(maxLength_i*values[valIdx], 0.0, 1-maxLength_i*values[valIdx]);
                vertexColors.add(color);
                vertexColors.add(color);
                //Fill vertices
                vertices.add(points[valIdx*2+0]);
                vertices.add(points[valIdx*2+1]);
            }
            LineModel model = new LineModel(vertices, vertexColors);

            return drawModel(model);
        }
    },
    
    D3 {
        @Override
        public BufferedImage draw(Point3D[] points, double[] values) {
            //Draw a wire triangle between the points of each value.
            //The color computation assumes that the values are in [0, sqrt(2)/2].
            int vertexNum = values.length * 3;
            ArrayList<Vector3D> vertexColors = new ArrayList<Vector3D>(
                    vertexNum);
            ArrayList<Point3D> vertices = new ArrayList<Point3D>(vertexNum);
            final double maxVal_i = 1 / Math.sqrt(0.5*Math.sqrt(2));
            for (int valIdx = 0; valIdx < values.length; valIdx++) {
                //Fill colors
                Vector3D color = new Vector3D(maxVal_i*values[valIdx], 0.0, 1-maxVal_i*values[valIdx]);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                //Fill vertices
                vertices.add(points[valIdx*3+0]);
                vertices.add(points[valIdx*3+1]);
                vertices.add(points[valIdx*3+1]);
                vertices.add(points[valIdx*3+2]);
                vertices.add(points[valIdx*3+2]);
                vertices.add(points[valIdx*3+0]);
            }
            LineModel model = new LineModel(vertices, vertexColors);

            return drawModel(model);
        }
    },
    
    D4 {
        @Override
        public BufferedImage draw(Point3D[] points, double[] values) {
            //Draw a wire tetrahedron between the points of each value.
            //The color is computed relative the maximum number of the values array.
            int vertexNum = values.length * 6;
            ArrayList<Vector3D> vertexColors = new ArrayList<Vector3D>(
                    vertexNum);
            ArrayList<Point3D> vertices = new ArrayList<Point3D>(vertexNum);
            double maxVal = Double.MIN_VALUE;
            for(double val: values) {
                if (maxVal < val)
                    maxVal = val;
            }
            double maxVal_i = 1 / maxVal;
            for (int valIdx = 0; valIdx < values.length; valIdx++) {
                //Fill colors
                Vector3D color = new Vector3D(maxVal_i*values[valIdx], 0.0, 1-maxVal_i*values[valIdx]);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                vertexColors.add(color);
                //Fill vertices
                vertices.add(points[valIdx*4+0]);
                vertices.add(points[valIdx*4+1]);
                vertices.add(points[valIdx*4+1]);
                vertices.add(points[valIdx*4+2]);
                vertices.add(points[valIdx*4+2]);
                vertices.add(points[valIdx*4+0]);
               
                vertices.add(points[valIdx*4+3]);
                vertices.add(points[valIdx*4+0]);
                vertices.add(points[valIdx*4+3]);
                vertices.add(points[valIdx*4+1]);
                vertices.add(points[valIdx*4+3]);
                vertices.add(points[valIdx*4+2]);
            }
            LineModel model = new LineModel(vertices, vertexColors);

            return drawModel(model);
        }
    };
    
    public abstract BufferedImage draw(Point3D[] points, double[] values);
    
    private static BufferedImage drawModel(Model model) {
        Scene scene = new Scene();
        scene.setDimensions(2000, 2000);
        scene.addDrawable(model);
        BufferedImage img = scene.extractView();
        return img;
    }
}

/**
 * Model made up of a mesh of line segments. Each line segment's vertex
 * has a color.
 */
class LineModel extends Model {

    private List<Point3D> _vertices;
    private List<Vector3D> _vertexColors;
    
    public LineModel(List<Point3D> vertices, List<Vector3D> vertexColors) {
        super("LineModel", ".lines");
        _vertices = vertices;
        _vertexColors = vertexColors;
    }
    
    @Override
    public void draw(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glLineWidth(1);
        gl.glBegin(GL.GL_LINES);
        for (int i=0; i<_vertices.size(); i++) {
            gl.glColor3d(_vertexColors.get(i).getX(),_vertexColors.get(i).getZ(),_vertexColors.get(i).getZ());
            gl.glVertex3d(_vertices.get(i).getX(),_vertices.get(i).getY(),_vertices.get(i).getZ());
        }
        gl.glEnd();
    }
}