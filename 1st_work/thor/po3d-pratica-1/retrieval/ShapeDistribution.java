package retrieval;

import thor.graphics.Point3D;
import thor.graphics.Vector3D;
import thor.model.geoset.*;

import java.util.List;
import java.util.ArrayList;

public class ShapeDistribution {

	public enum shapeFunctionType {A3, D1, D2, D3, D4};
	
	private double[] _samples;
	
	private double stuff;
	
	public ShapeDistribution(){}
	
	public double[] CreateShapeFunctionSamples(shapeFunctionType sfp, Mesh m, int N){
			
		double[] resultSamples = new double[N];
		
		UniformMeshSurfaceSampler pointSampler = new UniformMeshSurfaceSampler(m);
		
		for(int i = 0; i < N; i++){
			// Get N samples
			List<Point3D> samplesToProcess = new ArrayList<Point3D>();
			switch(sfp){
				case D1:
				    samplesToProcess.add(m.getBarycenter());
				    samplesToProcess.add(pointSampler.getPoint());
					break;

				case D2: 
				    samplesToProcess.add(pointSampler.getPoint());
				    samplesToProcess.add(pointSampler.getPoint());
					break;
				
				case A3:
					
				case D3: 
				    samplesToProcess.add(pointSampler.getPoint());
				    samplesToProcess.add(pointSampler.getPoint());
				    samplesToProcess.add(pointSampler.getPoint());
				    break;

				case D4: 
				    samplesToProcess.add(pointSampler.getPoint());
					samplesToProcess.add(pointSampler.getPoint());
				    samplesToProcess.add(pointSampler.getPoint());
				    samplesToProcess.add(pointSampler.getPoint());
				    break;
			}
			resultSamples[i] = ShapeFunction(sfp, samplesToProcess, m);
		}
		
		return resultSamples;
	}
	
	public double ShapeFunction(shapeFunctionType sfp, List<Point3D> vertices, Mesh m){
		
		switch(sfp){
			case A3: 
			    // Calculate the cosine of the angle between 0->1 and 0->2.
			    // The returned value is contained in [-1, 1].
			    Vector3D v0_1 = Vector3D.sub(vertices.get(1), vertices.get(0));
			    Vector3D v0_2 = Vector3D.sub(vertices.get(2), vertices.get(0));
			    v0_1.normalize();
			    v0_2.normalize();
			    return v0_1.DotProduct3(v0_1);

			case D1:

			case D2:
			    // Calculate (the line root of) the length of the line.
				return Point3D.distance(vertices.get(0).getX(), vertices.get(0).getY(), vertices.get(0).getZ(),
								vertices.get(1).getX(), vertices.get(1).getY(), vertices.get(1).getZ());

			case D3: 
				// Calculate the square root of the area of the triangle.
			    Vertex v1 = new Vertex(vertices.get(0).getX(), vertices.get(0).getY(), vertices.get(0).getZ()); 
				Vertex v2 = new Vertex(vertices.get(1).getX(), vertices.get(1).getY(), vertices.get(1).getZ()); 
				Vertex v3 = new Vertex(vertices.get(2).getX(), vertices.get(2).getY(), vertices.get(2).getZ());
				return Math.sqrt(m.heronFormula(v1, v2, v3));

			case D4:
				// Calculate the cube root of the volume of the tetrahedron.
			    Vector3D va_d = Vector3D.sub(vertices.get(0),vertices.get(3));
			    Vector3D vb_d = Vector3D.sub(vertices.get(1),vertices.get(3));
			    Vector3D vc_d = Vector3D.sub(vertices.get(2),vertices.get(3));
			    double vol = Math.abs((va_d.DotProduct3(Vector3D.product(vb_d, vc_d))))/6.0;
			    return Math.cbrt(vol);
			    
//			    List<Point3D> auxPoints = new ArrayList<Point3D>();
//				for(int i = 0; i < vertices.size() - 1; i++){
//					Point3D p = new Vertex(vertices.get(i).getX() - vertices.get(vertices.size()).getX(),
//							vertices.get(i).getY() - vertices.get(vertices.size()).getY(),
//							vertices.get(i).getZ() - vertices.get(vertices.size()).getZ());
//					auxPoints.add(p);
//				}
//				return auxPoints.get(0).getX()*auxPoints.get(1).getY()*auxPoints.get(2).getZ() +
//						auxPoints.get(0).getY()*auxPoints.get(1).getZ()*auxPoints.get(2).getX() +
//						auxPoints.get(0).getZ()*auxPoints.get(1).getX()*auxPoints.get(2).getY() -
//						auxPoints.get(0).getZ()*auxPoints.get(1).getY()*auxPoints.get(2).getX() -
//						auxPoints.get(0).getY()*auxPoints.get(1).getX()*auxPoints.get(2).getZ() -
//						auxPoints.get(0).getX()*auxPoints.get(1).getZ()*auxPoints.get(2).getY();
		}
		
		return 0;
	}
	
	public void ShowSampleCloud(){}
	
	public void CreateNewShapeDistribution(shapeFunctionType sfp, Mesh m, int N, int B, int V){

		double[] samples = CreateShapeFunctionSamples(sfp, m, N);
		
		for(int j = 0; j < B; j++){
			// Count samples for B bins
		}
		for(int k = 0; k < V; k++){
			// Obtain V vertex necessary for shape distribution
		}		
	}
	
	public void ShowShapeDistribution(){}
}

