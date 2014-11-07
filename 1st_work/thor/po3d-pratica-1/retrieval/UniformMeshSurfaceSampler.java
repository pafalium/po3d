package retrieval;

import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;
import thor.model.geoset.Face;
import thor.model.geoset.Mesh;
import thor.model.geoset.Vertex;

/**
 * Class for objects that uniformly sample points of a mesh's surface.
 * It is assumed that the mesh's surface is only composed by triangle faces.
 * 
 * @author Pedro-170
 *
 */
public class UniformMeshSurfaceSampler {
    /** The total area of the mesh */
    private final double _meshTotalArea;
    /** Array with the cumulative area for all the faces of the mesh. */
    private final double[] _cumulativeFaceAreas;
    /** The mesh from which the samples are taken. */
    private final Mesh _mesh;
    
    private double getFaceArea(int face) {
        return _mesh.triangleArea(_mesh.getFaces().get(face));
    }
    
    /**
     * Constructs an {@link UniformMeshSurfaceSampler} for the
     * given {@link Mesh}.
     * @param mesh the mesh whose surface will be sampled
     */
    public UniformMeshSurfaceSampler(Mesh mesh) {
        // This assumes that mesh will not explode. :)
        _mesh = mesh;
        _cumulativeFaceAreas = new double[_mesh.countFaces()];
        
        _cumulativeFaceAreas[0] = getFaceArea(0);
        for (int face = 1; face < _cumulativeFaceAreas.length; face++) {
            _cumulativeFaceAreas[face] = _cumulativeFaceAreas[face-1] + getFaceArea(face);
        }

        _meshTotalArea = _cumulativeFaceAreas[_cumulativeFaceAreas.length-1];
    }

    
    @SuppressWarnings("unused")
    private int searchFaceLinear(double selector) {
        /* This method does a linear search for the interval in which
         *  selector belongs to.
         * The intervals follow the pattern [left-bound right-bound[.
         * The search is done on a ordered number array. Each element
         *  of the array stores the right-bound of the interval with
         *  the same index.
         */
        int i = 0;
        while (i < _cumulativeFaceAreas.length
                && _cumulativeFaceAreas[i] < selector) {
            i++;
        }
        if (i == _cumulativeFaceAreas.length)
            return i - 1;
        else
            return i;
    }
    
    private int searchFace(double selector) {
        /* This method does a binary search for the interval in which
         *  selector belongs to.
         * The intervals follow the pattern [left-bound right-bound[.
         * The search is done on a ordered number array. Each element
         *  of the array stores the right-bound of the interval with
         *  the same index.
         */
        // the cumulative areas array has the right-bounds for the face interval with the same index
        // interval = [left-bound right-bound[
        // selector E interval
        int low = 0;
        int high = _cumulativeFaceAreas.length;
        while (high > low) {
            int mid = low + (high-low)/2;
            if ( selector >= _cumulativeFaceAreas[mid]) {//selector >= right-bound
                //search to the right of mid.
                low = mid+1;
            } else if ( selector < (mid>0?_cumulativeFaceAreas[mid-1]:0.0)) {//selector < left-bound
                //search to the left of mid.
                high = mid-1;
            } else {
                //selector is within the face's interval.
                return mid;
            }
        }
        return low;
    }
    
    /**
     * This method get's the point from the given triangle face with
     *  coordinates r1 and r2.
     * The triangle's vertices are referred to as ABC/1st,2nd,3rd.
     * The point lies on a line parallel to the AB segment of the triangle.
     * @param face the triangle face
     * @param r1 the distance of the line to the AB segment
     * @param r2 the position on the line (0 -> closer to A, 1 -> closer to B)
     * @return the point on the triangle with the given coordinates
     */
    private Point3D uniformTrianglePoint(Face face, double r1, double r2) {
        final Vertex v0 = _mesh.getVertices().get(face.Vertices.get(0));
        final Vertex v1 = _mesh.getVertices().get(face.Vertices.get(1));
        final Vertex v2 = _mesh.getVertices().get(face.Vertices.get(2));
        final double r1_sqrt = Math.sqrt(r1);
        final double a = 1-r1_sqrt;
        final double b = r1_sqrt*(1-r2);
        final double c = r1_sqrt*r2;
        return  new Vertex(
                v0.getX()*a+v1.getX()*b+v2.getX()*c,
                v0.getY()*a+v1.getY()*b+v2.getY()*c,
                v0.getZ()*a+v1.getZ()*b+v2.getZ()*c);
    }
    
    /**
     * Get a random point from the surface of the mesh.
     * @return a new random point on the mesh's surface
     */
    public Point3D getPoint() {
        double faceSelectorNumber = Math.random() * _meshTotalArea;
        int selectedFace = searchFace(faceSelectorNumber);
        return uniformTrianglePoint(_mesh.getFaces().get(selectedFace),Math.random(),Math.random());
    }
    
    /**
     * Get a list with the specified number of random 
     *  points from the mesh's surface.
     * @param numSamples the number of points to be gathered
     * @return the points
     */
    public List<Point3D> getPoints(int numSamples) {
        ArrayList<Point3D> samples = new ArrayList<Point3D>(numSamples);
        for(int face=0; face<_mesh.countFaces(); face++) {
            final Face f = _mesh.getFaces().get(face);
            final double triArea = _mesh.triangleArea(_mesh.getFaces().get(face));
            final int triSamples = (int)((triArea/_meshTotalArea) * numSamples + 0.5);
            for(int s=0; s<triSamples; ++s) {
                samples.add(uniformTrianglePoint(f, Math.random(), Math.random()));
            }
        }
        return samples;
    }
    
    /**
     * Program that performs some tests on the UniformMeshSurfaceSampler's
     * functionality.
     */
    public static void main(String[] args) {
        MeshMock mock = new MeshMock();
        UniformMeshSurfaceSampler sampler = new UniformMeshSurfaceSampler(mock);
        for (int i=0; i<sampler._cumulativeFaceAreas.length; ++i) {
            System.out.print(sampler._cumulativeFaceAreas[i]+", ");
        }
        System.out.println();
        System.out.println(sampler.searchFace(0));
        System.out.println(sampler.searchFace(0.25));
        System.out.println(sampler.searchFace(0.5));
        System.out.println(sampler.searchFace(1));
        System.out.println(sampler.searchFace(1.25));
        System.out.println(sampler.searchFace(1.5));
        System.out.println(sampler.searchFace(1.6));
        System.out.println(sampler.searchFace(1.9));
        System.out.println(sampler.searchFace(2.0));
        System.out.println(sampler.searchFace(-0.5));
        System.out.println(sampler.searchFace(2.1));
    }
}

/**
 * A mesh mock class to test the sampler's functionality.
 * @author Pedro-170
 *
 */
class MeshMock extends Mesh {
    @Override
    public double triangleArea(Face f) {
        return 0.5;
    }
    @Override
    public List<Face> getFaces() {
        ArrayList<Face> fs = new ArrayList<Face>();
        fs.add(null);
        fs.add(null);
        fs.add(null);
        fs.add(null);
        return fs;
    }
    @Override
    public int countFaces() {
        return 4;
    }
}
