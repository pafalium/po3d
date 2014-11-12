// Copyright 2013 Pedro B. Pascoal
package thor.model.geoset; 

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import quickhull3d.QuickHull3D;
import thor.graphics.Vector3D;
import thor.graphics.Point3D;

/**
 * The abstract class Mesh is the superclass of all classes that represent polygon mesh.
 * The mesh must be obtained in a platform-specific manner.
 * @author Pedro B. Pascoal
 */
public abstract class Mesh extends Object {
	protected List<Vertex> _vertices = new ArrayList<Vertex>();		// list of vertices
	protected List<Point2D> _textCoord = new ArrayList<Point2D>();	// list of texture coordinates
	protected List<Vector3D> _normals = new ArrayList<Vector3D>();	// list of normals
	protected List<Face> _faces = new ArrayList<Face>(); 			// list of faces (triangles)

	protected Point3D _maxVertex;
	protected Point3D _minVertex;
	
	public Mesh() {
		_maxVertex = new Point3D.Float(-9999, -9999, -9999);
		_minVertex = new Point3D.Float(9999, 9999, 9999);
	}
	/**
	 * @return
	 * A list of Vertex with all the vertices of the mesh.
	 */
	public List<Vertex> getVertices() { return _vertices; }
	/**
	 * @return
	 * A list of Point2D with the texture coordinates for each of the vertices of the mesh.
	 */
	public List<Point2D> getTextCoord() { return _textCoord; }
	/**
	 * @return
	 * A list of Vector3D that represent the normals of each of the vertices of the mesh.
	 */
	public List<Vector3D> getNormals() { return _normals; }
	/**
	 * @return
	 * A list of Face with all the faces of the mesh.
	 */
	public List<Face> getFaces() { return _faces; }
	/**
	 * @return
	 * Return the total number of vertices in the mesh.
	 */
	public int countVertices() { return _vertices.size(); }
	/**
	 * @return
	 * Return the total number of faces in the mesh.
	 */
	public int countFaces() { return _faces.size(); }

	/**
	 * Moves every point of the mesh a (x, y, z) distance.
	 * @param x - the X coordinate to add.
	 * @param y - the Y coordinate to add.
	 * @param z - the Z coordinate to add.
	 */
	public void translate(double x, double y, double z) {
		for (Vertex vertex : _vertices) {
			vertex.add(new Point3D.Double(x, y, z));
		}
	}
	/**
	 * Change the dimension of object by a scaling factor, i.e. enlarging or shrinking. 
	 * @param value - the scale factor
	 */
	public void scale(double value) {
		for (Vertex vertex : _vertices) {
			vertex.mul(value);
		}
	}
	public void rotateX(float angle) {
		for (Vertex vertex : _vertices) {
			double rangle = Math.atan(vertex.getY() / vertex.getX()) * (float) (Math.PI /180);
			vertex.setLocation( (float) Math.cos(angle + rangle),
								(float) Math.asin(angle + rangle),
								vertex.getZ());
		}
	}
	public void rotateZ(float angle) {
		for (Vertex vertex : _vertices) {
			double rangle = Math.atan(vertex.getY()/vertex.getX()) * (float) (Math.PI /180);
			vertex.setLocation( (float) Math.cos(angle + rangle),
								(float) Math.asin(angle + rangle),
								vertex.getZ());
		}
	}
	
	public void calculateNormals() {
		//MyTODO: ok... kinda of tired, so calculate only for 3 vertices, screw when there are 4...
		// i'll do it later, maybe calculate each face normal
		for(Face face : _faces) {
			Point3D p1 = _vertices.get(face.Vertices.get(0));
			Point3D p2 = _vertices.get(face.Vertices.get(1));
			Point3D p3 = _vertices.get(face.Vertices.get(2));

			Vector3D v1 = Vector3D.sub(p2, p1);
			Vector3D v2 = Vector3D.sub(p3, p1);
			
			face.Normal = Vector3D.product(v1, v2);
			face.Normal.normalize();
			
			for(int i : face.Vertices) {
				Vertex v = _vertices.get(face.Vertices.get(i));
				v.Normal.add(face.Normal);
			}
		}
		for(Vertex v : _vertices) {
			v.Normal.normalize();
		}
	}
	
	public Point3D getMaxVertex() {
		return _maxVertex;
	}
	public Point3D getMinVertex() {
		return _minVertex;
	}
	

	public Point3D getFaceCenter(Face face) {
		Point3D center = new Point3D.Double();
		
		for(int i : face.Vertices) {
			Vertex v = _vertices.get(i);
			center.add(v);
		}
		center.div(face.Vertices.size());
		return center;
	}
	
	public void draw(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
        for (Face face : _faces) {
        	if(face.Vertices.size() == 4) {
        		gl.glBegin(GL2.GL_QUADS);
        	} else { // default = 3
        		gl.glBegin(GL2.GL_TRIANGLES);
        	}
        	for(int i = 0; i < face.Vertices.size(); i++) {
        		gl.glVertex3d(	_vertices.get(face.Vertices.get(i)).getX(),
        						_vertices.get(face.Vertices.get(i)).getY(),
        						_vertices.get(face.Vertices.get(i)).getZ());
        	}
            gl.glEnd();
		}
	}
	
	public Point3D getBarycenter() {
		/* : PO3D Pratica 1 - calculate the barycenter of the mesh
		 * * Point3D.Double(x, y, z) -> creates a new Point3D with doubles
		 * * _maxVertex -> contains the max values of each coordinate
		 * * _minVertex -> contains the min values of each coordinate
		 */
	    /*
	     * In this barycenter implementation an approximate uniform point sample
	     * is made in the mesh's surface. The barycenter is then the mean
	     * point of the sample.
	     */
	    final double totalArea = getSurfaceArea();
	    final int SAMPLES_MULTIPLIER = 10;
	    final int maxSamplesNum = _faces.size() * SAMPLES_MULTIPLIER;
	    Vector3D meanVertex = new Vector3D(0.0,0.0,0.0);
	    int ignoredFaces = 0; //number of non triangular faces
	    int sampleCount = 0; //actual number of samples used (some are dropped)
	    for (Face f : _faces) {
	        //debug information (we only work with triangles)
	        if (f.Vertices.size() != 3) {
	            ignoredFaces++;
	            continue;
	        }
	        /*
	         * Compute the number of samples for the current face based
	         * in the ratio between it's area and the area of the whole
	         * mesh.
	         */
	        double faceArea = 0.0;
            faceArea = triangleArea(f);
            int triSamples = (int) Math.floor((faceArea / totalArea) * maxSamplesNum);
            /* Compute vectors u and v that start in the faces first vertex
             * and end on one of the other two vertices of the face.
             * These vectors are going to be used as a basis for the plane 
             * that contains the triangle. With this one can get random
             * points in the plane by using two random number as coordinates.
             * */
            Vector3D u = Vector3D.sub(_vertices.get(f.Vertices.get(1)), _vertices.get(f.Vertices.get(0)));
            Vector3D v = Vector3D.sub(_vertices.get(f.Vertices.get(2)), _vertices.get(f.Vertices.get(0)));
            // The "origin" of the plane in which the samples will be taken.
            Vertex origin = _vertices.get(f.Vertices.get(0));
            for (int i=0; i<triSamples; ++i) {
                /* Taking two random numbers as coordinates of the new
                 * sample.
                 */
                double sample_u = Math.random();
                double sample_v = Math.random();
                /* Check if the sample is inside the triangle.
                 * Since Math.random() returns numbers inside the interval
                 * [0.0 1.0[ it is only necessary to check if the sample
                 * is below the hypotenuse of the triangle.
                 *  v
                 *  1x\
                 *   | \
                 *   |  \
                 *  0x---x
                 *  0    1 u
                 */
                boolean inside = (1 - sample_u - sample_v) > 0;
                if (inside) {
                    sampleCount++;
                    // Compute the sample's position in the plane.
                    Vector3D sample = Vector3D.add(new Vector3D(origin.getX(),origin.getY(),origin.getZ()), 
                                                    new Vector3D(
                                                        u.getX()*sample_u+v.getX()*sample_v,
                                                        u.getY()*sample_u+v.getY()*sample_v,
                                                        u.getZ()*sample_u+v.getZ()*sample_v));
                    // Add the sample's position to the meanVertex accumulator.
                    meanVertex.add(sample);
                }
            }
	        
	    }
	    /* Compute the mean point of the sample by dividing the
	     * accumulator by the sample size.
	     */
	    double factor = sampleCount != 0 ? 1.0 / sampleCount : 1.0;//if no sample was taken revert barycenter to origin.
	    meanVertex.mul(factor);
	    // Print debug information.
	    System.out.println("Sample Size: "+sampleCount+", Max Sample Size: "+maxSamplesNum+".");
	    System.out.println("Barycenter ignored "+ignoredFaces+" faces.");
		return meanVertex;
	}
	
	public boolean isManifold() {
		/* : PO3D Pratica 1 - determine if the mesh is manifold
		 * *
		 * * _vertices -> contains a list of all vertices of the mesh
		 * * _faces -> contains the list of the id of the vertices that from it
		 */
	    /* In this implementation a mesh for deciding if a mesh is manifold
	     * two checks are made:
	     *  - All edges must belong to at most two faces, otherwise that would be 
	     *  singular edges;
	     *  - All faces incident in each vertex must form one and only one
	     *  open or closed fan. 
	     */
		// Check if each edge has at most two incident faces.
		// Count how many faces have each edge. (the vertex order of the edge is ignored)
	    Map<Edge, Integer> edgeFaceNumber = new TreeMap<Edge, Integer>();
		for (Face face : _faces) {//for all faces
			for (int edge = 0, size = face.Vertices.size(); edge < size; ++edge) {//for all edges
				int v1 = face.Vertices.get(edge);
				int v2 = face.Vertices.get((edge+1)%size);
				Edge e = new Edge(v1, v2);
				if (!edgeFaceNumber.containsKey(e))
					edgeFaceNumber.put(e, 0);
				edgeFaceNumber.put(e, edgeFaceNumber.get(e)+1);//add one to the edge's faces
			}
		}
		boolean edgesBelongToAtMostTwoFaces = true;
		for (Integer i : edgeFaceNumber.values()) {
			if (i > 2)
				edgesBelongToAtMostTwoFaces = false;
		}
		// For each vertex check if adjacent faces for a open or closed fan
		/*
		 * This check is done in two phases:
		 *  - The incident faces on each vertex are collected with the adjacent vertices stored;
		 *  - It is checked that all adjacent faces of each vertex form a fan.
		 */
		@SuppressWarnings("unchecked")
        List<VertexAdjacenciesInFace>[] vertsAdjs = new List[_vertices.size()];
		for (int i=vertsAdjs.length-1;i>=0;i--) {//init vertex adjacencies
			vertsAdjs[i] = new ArrayList<VertexAdjacenciesInFace>();
		}
		for (int faceInd=_faces.size()-1;faceInd>=0;faceInd--) {//fill vertex adjacencies
		    Face f = _faces.get(faceInd);
			final int faceVertexNum = _faces.get(faceInd).Vertices.size();
			for (int vertInd=0; vertInd<faceVertexNum; vertInd++) {
				/* The face is incident on it's vertices and 
				 * for each of them the adjacent vertices are 
				 * the previous and the next.
			     */
			    int vert = f.Vertices.get(vertInd);
				vertsAdjs[vert].add(new VertexAdjacenciesInFace(faceInd, 
																f.Vertices.get((vertInd+1)%faceVertexNum), 
																f.Vertices.get((vertInd+faceVertexNum-1)%faceVertexNum)));
			}
		}
		boolean facesAroundVerticesFormOneFan = true;
		for (List<VertexAdjacenciesInFace> vertAdjsList : vertsAdjs) {
			// Each set in the DisjointSets represents one fan.
		    DisjointSets faceConnectivity = new DisjointSets(vertAdjsList.size());
			for (int f1=0; f1<vertAdjsList.size(); f1++) {
				for (int f2=0; f2<vertAdjsList.size(); f2++) {
					if (f1 == f2) continue;
					/* If two faces are adjacent join the sets they belong
					 * to if not already joined.
					*/
					if (adjacentFacesOnVertex(vertAdjsList.get(f1), vertAdjsList.get(f2))) {
						// get the set for each face
					    int f1Root = faceConnectivity.find(f1);
						int f2Root = faceConnectivity.find(f2);
						// join then if not the same
						if (f1Root == f2Root) continue; //faces already connected
						faceConnectivity.union(f1Root, f2Root);
					}
				}
			}
			// Check if there is indeed only one fan for the vertex.
			if (faceConnectivity.countSets() > 1) {
			    facesAroundVerticesFormOneFan = false;
			}
		}
		
		return edgesBelongToAtMostTwoFaces && facesAroundVerticesFormOneFan;
	}

	/**
	 * This method decides if two faces are adjacent through edges that share
	 * the current vertex and vertices connected to it by an edge.
	 * @param f1 current vertex adjacency information for one face
	 * @param f2 current vertex adjacency information for other face
	 */
	private boolean adjacentFacesOnVertex(VertexAdjacenciesInFace f1, VertexAdjacenciesInFace f2) {
		return 
				f1.adj1 == f2.adj1 || f1.adj1 == f2.adj2 ||
				f1.adj2 == f2.adj1 || f1.adj2 == f2.adj2;
	}
	
	public double getSurfaceArea() {
		/* : PO3D Pratica 1 - calculate and return the surface area 
		 * *
		 * * _vertices -> contains a list of all vertices of the mesh
		 * * _faces -> contains the list of the id of the vertices that from it
		 */
	    /*
	     * This implementation uses Heron's Formula to compute the area each triangle
	     * of the mesh. It also computes the area of convex quads which can be 
	     * safely decomposed in two triangles of which the areas are summed.
	     */
		double totalArea = 0.0;
		int quads = 0; int tris = 0; int other = 0;
		for (Face face : _faces) {
			switch (face.Vertices.size()) {
			case 3:
				++tris;
				totalArea += triangleArea(face);
				break;
			case 4:
				++quads;
				totalArea += convexQuadArea(face);
				break;
			default:
				++other;
				break;
			}
		}
		System.out.println("Quads: "+quads+"\tTris: "+tris+"\tOther: "+other);
		return totalArea;
	}
	
	public double triangleArea(final Face face) {
		final Vertex v1 = _vertices.get(face.Vertices.get(0));
		final Vertex v2 = _vertices.get(face.Vertices.get(1));
		final Vertex v3 = _vertices.get(face.Vertices.get(2));
		return heronFormula(v1, v2, v3);
	}

	public double convexQuadArea(final Face face) {
		final Vertex v1 = _vertices.get(face.Vertices.get(0));
		final Vertex v2 = _vertices.get(face.Vertices.get(1));
		final Vertex v3 = _vertices.get(face.Vertices.get(2));
		final Vertex v4 = _vertices.get(face.Vertices.get(3));
		return heronFormula(v1, v2, v3) + heronFormula(v3, v4, v1);
	}
	
	public static double heronFormula(final Vertex v1, final Vertex v2, final Vertex v3) {
		final double a =  Vector3D.distance(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
		final double b =  Vector3D.distance(v2.x, v2.y, v2.z, v3.x, v3.y, v3.z);
		final double c =  Vector3D.distance(v3.x, v3.y, v3.z, v1.x, v1.y, v1.z);
		final double s = (a + b + c) * 0.5; // semiperimeter
		final double area = Math.sqrt(s*(s-a)*(s-b)*(s-c));
		return area;
	}
	
	public Mesh getConvexHull() {
		/* : PO3D Pratica 1 - create and return the convex hull
		 * return a new mesh that is the convex hull of the original
		 */
	    /*
	     * Using QuickHull3D implementation.
	     */
	    try {
    	    quickhull3d.Point3d[] points = new quickhull3d.Point3d[_vertices.size()];
    	    for(int i = 0; i<_vertices.size(); ++i) {
    	        Vertex vert = _vertices.get(i);
    	        points[i] = new quickhull3d.Point3d(vert.x,vert.y,vert.z);
    	    }
    	    quickhull3d.QuickHull3D qhull = new QuickHull3D();
    	    qhull.build(points);
    	    quickhull3d.Point3d[] vertices = qhull.getVertices();
    	    int[][] faces = qhull.getFaces();
    	    BufferedMesh convexHull = new BufferedMesh();
    	    for (quickhull3d.Point3d point : vertices) {
    	        convexHull.addVertex(point.x, point.y, point.z);
    	    }
    	    for (int face=0; face<faces.length; ++face) {
    	        List<Integer> v = new ArrayList<Integer>(); // vertices id's
                List<Integer> vt = new ArrayList<Integer>();// texture coordinates id's
                List<Float> vn = new ArrayList<Float>();// normal id's
                for(int vertId : faces[face]) {
                    v.add(vertId);
                }
                convexHull.addFace(v, vt, vn);
    	    }
    	    
    		return convexHull;
	    } catch (IllegalArgumentException ex) {
	        System.out.println("QuickHull3D failed. Message: "+ex.getMessage());
	        return null;
	    }
	}
}

// isManifold() auxiliary classes
/**
 * An Edge represents an edge of a mesh.
 * It is assumed that the edge (first,last) is the same as (last,first).
 */
class Edge implements Comparable<Edge>{
	public final int first;
	public final int last;
	public Edge(int v1, int v2) {
		if (v1 < v2) {
			this.first = v1;
			this.last = v2;
		} else {
			this.first = v2;
			this.last = v1;
		}
	}
	public boolean equals(Edge other) {
		return this.first == other.first && this.last == other.last;
	}
	@Override
	public int compareTo(Edge o) {
		int firstDiff = first - o.first;
		int lastDiff = last - o.last;
		if (firstDiff != 0)
			return firstDiff;
		else 
			return lastDiff;
	}
	@Override
	public String toString() {
		return "[Edge "+first+" "+last+"]";
	}
}

/**
 * Container for a vertex's adjacent vertices in a face.
 * A vertex is adjacent to other if they are connected by an edge.
 * A vertex has exactly two adjacent vertices within a face since
 * it only appears once in it.
 */
class VertexAdjacenciesInFace {
    /**Vertex index of one adjacent vertex.*/
	public final int adj1;
	/**Vertex index of other adjacent vertex.*/
	public final int adj2;
	/**Face index of the face the edges formed by the vertices belong.*/
	public final int face;
	public VertexAdjacenciesInFace(int face, int adj1, int adj2) {
		this.face = face;
		this.adj1 = adj1;
		this.adj2 = adj2;
	}
	@Override
    public String toString() {
        return "[VAdj f="+face+" ("+adj1+","+adj2+")]";
    }
}

/**
 *  A disjoint sets ADT.  Performs union-by-rank and path compression.
 *  Implemented using arrays.  There is no error checking whatsoever.
 *  By adding your own error-checking, you might save yourself a lot of time
 *  finding bugs in your application code for Project 3 and Homework 9.
 *  Without error-checking, expect bad things to happen if you try to unite
 *  two elements that are not roots of their respective sets, or are not
 *  distinct.
 *
 *  Elements are represented by ints, numbered from zero.
 *
 *  Source: http://www.cs.berkeley.edu/~jrs/61bs02/hw/hw9/set/DisjointSets.java
 *  @author Mark Allen Weiss
 **/
class DisjointSets {

  private int[] array;

  /**
   *  Construct a disjoint sets object.
   *
   *  @param numElements the initial number of elements--also the initial
   *  number of disjoint sets, since every element is initially in its own set.
   **/
  public DisjointSets(int numElements) {
    array = new int [numElements];
    for (int i = 0; i < array.length; i++) {
      array[i] = -1;
    }
  }

  /**
   *  union() unites two disjoint sets into a single set.  A union-by-rank
   *  heuristic is used to choose the new root.  This method will corrupt
   *  the data structure if root1 and root2 are not roots of their respective
   *  sets, or if they're identical.
   *
   *  @param root1 the root of the first set.
   *  @param root2 the root of the other set.
   **/
  public void union(int root1, int root2) {
    if (array[root2] < array[root1]) {
      array[root1] = root2;             // root2 is taller; make root2 new root
    } else {
      if (array[root1] == array[root2]) {
        array[root1]--;            // Both trees same height; new one is taller
      }
      array[root2] = root1;       // root1 equal or taller; make root1 new root
    }
  }

  /**
   *  find() finds the (int) name of the set containing a given element.
   *  Performs path compression along the way.
   *
   *  @param x the element sought.
   *  @return the set containing x.
   **/
  public int find(int x) {
    if (array[x] < 0) {
      return x;                         // x is the root of the tree; return it
    } else {
      // Find out who the root is; compress path by making the root x's parent.
      array[x] = find(array[x]);
      return array[x];                                       // Return the root
    }
  }
  
  public int countSets() {
      int setCount = 0;
      for (int i = 0; i < array.length; i++) {
          if (array[i] < 0)
              setCount++;
      }
      return setCount;
  }
}
// end of isManifold() auxiliary classes