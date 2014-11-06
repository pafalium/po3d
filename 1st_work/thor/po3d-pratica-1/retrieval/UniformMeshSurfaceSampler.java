package retrieval;

import thor.graphics.Point3D;
import thor.model.geoset.Mesh;

/**
 * Class for objects that uniformly sample points of a mesh's surface.
 * It is assumed that the mesh's surface is only composed by triangle faces.
 * 
 * @author Pedro-170
 *
 */
public class UniformMeshSurfaceSampler {
    /** The total area of the mesh */
    private float _meshTotalArea;
    /** Array with the cumulative area for all the faces of the mesh. */
    private float[] _cumulativeFaceAreas;
    /** The mesh from which the samples are taken. */
    private Mesh _mesh;
    
    public UniformMeshSurfaceSampler(Mesh mesh) {
        //TODO
        throw new RuntimeException("IMPLEMENT ME!");
    }
    
    public Point3D getPoint() {
        //TODO
        return null;
    }
}