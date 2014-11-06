package retrieval;

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
		
		for(int i = 0; i < N; i++){
			// Get N samples
			List<Vertex> samplesToProcess = new ArrayList<Vertex>();
			switch(sfp){
				case D1: break;

				case D2: break;
				
				case A3:
					
				case D3: break;

				case D4: break;
			}
			resultSamples[i] = ShapeFunction(sfp, samplesToProcess);
		}
		
		return resultSamples;
	}
	
	public float ShapeFunction(shapeFunctionType sfp, List<Vertex> vertices){
		
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