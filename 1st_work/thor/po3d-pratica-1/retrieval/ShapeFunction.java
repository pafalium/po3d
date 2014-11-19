package retrieval;

import thor.graphics.Point3D;
import thor.graphics.Vector3D;
import thor.model.geoset.Mesh;
import thor.model.geoset.Vertex;

public enum ShapeFunction {
    A3 {
        
        @Override
        public double[] computeValues(int numValues, Point3D[] points) {
            // Calculate the cosine of the angle between 0->1 and 0->2.
            // The returned value is contained in [-1, 1].
            assert points.length >= numValues * 3;
            double[] results = new double[numValues];
            for (int i=0; i<numValues; i++) {
                Vector3D v0_1 = Vector3D.sub(points[i*3+1], points[i*3]);
                Vector3D v0_2 = Vector3D.sub(points[i*3+2], points[i*3]);
                v0_1.normalize();
                v0_2.normalize();
                results[i] = v0_1.DotProduct3(v0_2);
            }
            return results;
        }

        @Override
        public double getMinBoundary() { return -1.0; }

        @Override
        public double getMaxBoundary() { return 1.0; }

        @Override
        public int getPointsPerValue() {
            return 3;
        }
    },
    
    D2 {
        @Override
        public double[] computeValues(int numValues, Point3D[] points) {
            // Calculate (the line root of) the length of the line.
            assert points.length >= numValues * 2;
            double[] results = new double[numValues];
            for(int i=0; i<numValues; i++) {
               results[i] = Point3D.distance(points[i*2].getX(), points[i*2].getY(), points[i*2].getZ(),
                       points[i*2+1].getX(), points[i*2+1].getY(), points[i*2+1].getZ());
            }
            return results;
        }
        
        @Override
        public double getMinBoundary() { return 0.0; }

        @Override
        public double getMaxBoundary() { return Math.sqrt(3); }
        
        @Override
        public int getPointsPerValue() {
            return 2;
        }
    },
    
    D3 {
        @Override
        public double[] computeValues(int numValues, Point3D[] points) {
            // Calculate the square root of the area of the triangle.
            assert points.length >= numValues * 3;
            double[] results = new double[numValues];
            for (int i=0; i<numValues; i++) {
                Vertex v1 = new Vertex(points[i*3+0].getX(), points[i*3+0].getY(), points[i*3+0].getZ()); 
                Vertex v2 = new Vertex(points[i*3+1].getX(), points[i*3+1].getY(), points[i*3+1].getZ()); 
                Vertex v3 = new Vertex(points[i*3+2].getX(), points[i*3+2].getY(), points[i*3+2].getZ());
                results[i] = Math.sqrt(Mesh.heronFormula(v1, v2, v3));
            }
            return results;
        }
        
        @Override
        public double getMinBoundary() { return 0.0; }

        @Override
        public double getMaxBoundary() { return Math.sqrt(0.5*Math.sqrt(2)); }
        
        @Override
        public int getPointsPerValue() {
            return 3;
        }
    }, 
    
    D4 {
        @Override
        public double[] computeValues(int numValues, Point3D[] points) {
            // Calculate the cube root of the volume of the tetrahedron.
            assert points.length >= numValues * 4;
            double[] results = new double[numValues];
            for (int i=0; i<numValues; i++) {
                Vector3D va_d = Vector3D.sub(points[i*4+0],points[i*4+3]);
                Vector3D vb_d = Vector3D.sub(points[i*4+1],points[i*4+3]);
                Vector3D vc_d = Vector3D.sub(points[i*4+2],points[i*4+3]);
                double vol = Math.abs((va_d.DotProduct3(Vector3D.product(vb_d, vc_d))))/6.0;
                results[i] = Math.cbrt(vol);
            }
            return results;
        }
        
        @Override
        public double getMinBoundary() { return 0.0; }

        @Override
        public double getMaxBoundary() { return Math.cbrt(1.0/3.0); /*XXX: Not Sure...*/}
        
        @Override
        public int getPointsPerValue() {
            return 4;
        }
    };

    public abstract double[] computeValues(int numValues, Point3D[] points);
    
    public abstract double getMaxBoundary();
    
    public abstract double getMinBoundary();
    
    public abstract int getPointsPerValue();
    
}