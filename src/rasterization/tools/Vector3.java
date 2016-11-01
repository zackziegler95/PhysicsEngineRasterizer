/**
 * 3 Vector
 */

package rasterization.tools;

public class Vector3 {
    public double[] array;
    
    /*public Vector3(double[] array) {
        if (array.length != 3) {
            System.err.println("Error: array passed to Vector3 constructor is the wrong size.");
        }
        
        this.array = new double[3];
        System.arraycopy(array, 0, this.array, 0, 3);
    }*/
    
    public Vector3(double one, double two, double three) {
        array = new double[3];
        array[0] = one;
        array[1] = two;
        array[2] = three;
    }
    
    public Vector3(Vector3 p) {
        array = new double[3];
        System.arraycopy(p.array, 0, this.array, 0, 3);
    }
    
    public Vector3 cross(Vector3 vec) {
        return new Vector3(
           array[1]*vec.array[2] - array[2]*vec.array[1],
           array[2]*vec.array[0] - array[0]*vec.array[2],
           array[0]*vec.array[1] - array[1]*vec.array[0]
        );
    }
    
    public double dot(Vector3 vec) {
        return array[0]*vec.array[0] + array[1]*vec.array[1] + array[2]*vec.array[2];
    }
    
    public Vector3 plus(Vector3 vec) {
        return new Vector3(
            array[0]+vec.array[0], array[1]+vec.array[1], array[2]+vec.array[2]);
    }
    
    public Vector3 minus(Vector3 vec) {
        return new Vector3(
            array[0]-vec.array[0], array[1]-vec.array[1], array[2]-vec.array[2]);
    }
    
    public Vector3 times(double d) {
        return new Vector3(array[0]*d, array[1]*d, array[2]*d);
    }
    
    public Vector3 normalize() { // Make magnitude one
        if (array[0] == 0 && array[1] == 0 && array[2] == 0) {
            System.err.println("Error, can't normalize zero vector");
        }
        return times(1/mag());
    }
    
    public double mag() {
        return Math.sqrt(array[0]*array[0] + array[1]*array[1] + array[2]*array[2]); 
    }
    
    public double magSq() {
        return array[0]*array[0] + array[1]*array[1] + array[2]*array[2]; 
    }
    
    public Vector3 normalize(Vector3 p, double length) { // Get a point length away from p in this direction
        Vector3 dist = minus(p);
        double mag = dist.mag();
        return dist.times(length/mag).plus(p);
    }
    
    public Vector4 augment() {
        return new Vector4(array[0], array[1], array[2], 1);
    }
    
    @Override
    public final String toString() {
        String s = "{";
        for (int i = 0; i < array.length-1; i++) {
            s += array[i]+", ";
        }
        return s + array[array.length-1]+"}";
    }
}
