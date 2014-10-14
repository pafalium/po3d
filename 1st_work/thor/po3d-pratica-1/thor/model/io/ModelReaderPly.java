// Copyright 2012 Pedro B. Pascoal
package thor.model.io; 

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import thor.model.BufferedModel;
import thor.model.geoset.BufferedMesh;

// Polygon File Format
class ModelReaderPly extends ModelReader {
	
	public ModelReaderPly(String name, String extension) {
		super(name, extension);
	}
	
	public BufferedModel read(String filename) throws IOException {
		/*
		 * ================================================================ 
		 * TODO: PO3D Pratica 1 - Polygon File Format (PLY)
		 * Add here the code to read a PLY file
		 * The file "thor.model.io.ModelReaderPly.java" serves as example of file reading
		 *  
		 * * BufferedModel model = new BufferedModel("model-name", "model-extension");
		 * * 
		 * * BufferedMesh mesh = new BufferedMesh();
		 * * 
		 * * for each vertex:
		 * * * mesh.addVertex(x, y, z);
		 * * 
		 * * for each face:
		 * * * mesh.addFace("list-of-vertices");
		 * * 
		 * * model.addMesh(mesh);
		 * * return model;
		 * ================================================================
		 */
		
		BufferedModel model = new BufferedModel(_name, _extension);
		BufferedMesh mesh = new BufferedMesh();

		Scanner root = new Scanner(new FileReader(filename));
		if(!root.hasNext()) {
			root.close();
			throw new IOException("ModelReader: " + filename + " is empty");
		}
		
		boolean endHeader = false;
		int nVertices = 0;
		int nFaces = 0;
		
		while(root.hasNext()) {
			Scanner line = new Scanner(root.nextLine());
			line.useLocale(Locale.US);
			
			String type = line.next();
			
			if(type.compareToIgnoreCase("element") == 0) {
				type = line.next();
				// Obtains the total number of Vertices
				if(type.compareToIgnoreCase("vertex") == 0) {
					nVertices = line.nextInt();
				}
				// Obtains the total number of Faces
				else if(type.compareToIgnoreCase("face") == 0) {
					nFaces = line.nextInt();
				}
			}
			if(type.compareToIgnoreCase("property") == 0) {

			}
			if(type.compareToIgnoreCase("end_header") == 0) {
				endHeader = true;
			}
			
			if (endHeader) {
				// Add vertices to mesh
				for(int i = 0; i < nVertices; i++) {
					Scanner vertexLine = new Scanner(root.nextLine());
					vertexLine.useLocale(Locale.US);
					
					double x = vertexLine.nextDouble();
					double y = vertexLine.nextDouble();
					double z = vertexLine.nextDouble();
					mesh.addVertex(x, y, z);
					
					vertexLine.close();
				}
				// Add faces to mesh
				for(int j = 0; j < nFaces; j++) {
					Scanner faceLine = new Scanner(root.nextLine());
					faceLine.useLocale(Locale.US);
					
					List<Integer> v = new ArrayList<Integer>(); // vertices id's
					List<Integer> vt = new ArrayList<Integer>();// texture coordinates id's
					List<Float> vn = new ArrayList<Float>();// normal id's
					
					int nVertex = faceLine.nextInt();
					
					for(int k = 0; k < nVertex; k++) {
						v.add(faceLine.nextInt());
					}
					
					mesh.addFace(v, vt, vn);
					faceLine.close();
				}
			}
			line.close();
		}
		model.addMesh(mesh);
		root.close();
		
		System.out.println("Model loaded");
		return model;
	}
}