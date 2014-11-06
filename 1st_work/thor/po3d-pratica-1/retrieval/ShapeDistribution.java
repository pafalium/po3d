package retrieval;

import thor.graphics.Point3D;
import thor.model.geoset.*;

import java.util.List;
import java.util.ArrayList;

public class ShapeDistribution {

	public enum shapeFunctionType {A3, D1, D2, D3, D4};
	
	private float[] _samples;
	
	private float stuff;
	
	public ShapeDistribution(){}
	
	public float[] CreateShapeFunctionSamples(shapeFunctionType sfp, Mesh m, int N){
			
		float[] resultSamples = new float[N];
		
		UniformMeshSurfaceSampler pointSampler = new UniformMeshSurfaceSampler(m);
		
		for(int i = 0; i < N; i++){
			// Get N samples
			List<Point3D> samplesToProcess = new ArrayList<Point3D>();
			switch(sfp){
				case D1: break;

				case D2: break;
				
				case A3:
				    //Example
				    samplesToProcess.add(pointSampler.getPoint());
				    samplesToProcess.add(pointSampler.getPoint());
				    samplesToProcess.add(pointSampler.getPoint());
					
				case D3: break;

				case D4: break;
			}
			resultSamples[i] = ShapeFunction(sfp, samplesToProcess);
		}
		
		return resultSamples;
	}
	
	public float ShapeFunction(shapeFunctionType sfp, List<Point3D> vertices){
		
		switch(sfp){
			case A3: break;

			case D1: break;

			case D2: break;

			case D3: break;

			case D4: break;
		}
		return 0;
	}
	
	public void ShowSampleCloud(){}
	
	public void CreateNewShapeDistribution(shapeFunctionType sfp, Mesh m, int N, int B, int V){

		float[] samples = CreateShapeFunctionSamples(sfp, m, N);
		
		for(int j = 0; j < B; j++){
			// Count samples for B bins
		}
		for(int k = 0; k < V; k++){
			// Obtain V vertex necessary for shape distribution
		}		
	}
	
	public void ShowShapeDistribution(){}
	
	public void CreateCharacteristVector(){
		
		
	}
}

