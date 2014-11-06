package retrieval;

import thor.model.geoset.*;
import java.util.List;

public class ShapeDistribution {

	public enum shapeFunctionType {A3, D1, D2, D3, D4};
	
	private float[] _samples;
	
	private float stuff;
	
	public ShapeDistribution(){}
	
	public void Sampling(shapeFunctionType sfp, Mesh m, int N, int B, int V){
		
		
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
	
	public void CreateNewShapeDistribution(){
		
		
	}
	
	public void CreateCharacteristVector(){
		
		
	}
}