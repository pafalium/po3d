package retrieval;

import java.util.ArrayList;
import java.util.List;

import quickhull3d.Point3d;
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
    
    private int searchFace(double selector) {
        //TODO
        int low = 0;
        int high = _cumulativeFaceAreas.length-1;
        while (high >= low) {
            
        }
        return -1;
    }
    
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
    
    public Point3D getPoint() {
        double faceSelectorNumber = Math.random() * _meshTotalArea;
        int selectedFace = searchFace(faceSelectorNumber);
        return uniformTrianglePoint(_mesh.getFaces().get(selectedFace),Math.random(),Math.random());
    }
    
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
}