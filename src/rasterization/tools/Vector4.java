/**
 * 4 Vector
 */

package rasterization.tools;

public class Vector4 {
    public double[] array;
    
    /*public Vector4(double[] array) {
        if (array.length != 4) {
            System.err.println("Error: array passed to Vector4 constructor is the wrong size.");
        }
        
        this.array = new double[4];
        System.arraycopy(array, 0, this.array, 0, 4);
    }*/
    
    public Vector4(double one, double two, double three, double four) {
        array = new double[4];
        array[0] = one;
        array[1] = two;
        array[2] = three;
        array[3] = four;
    }
    
    public double dot(Vector4 vec) {
        return array[0]*vec.array[0] + array[1]*vec.array[1] + array[2]*vec.array[2] + array[3]*vec.array[3];
    }
    
    public Vector4 makeHomogenous() {
        if (array[3] != 1) {
            return times(1.0/array[3]);
        }
        return this;
    }
    
    public Vector3 decrement() {
        return new Vector3(array[0], array[1], array[2]);
    }
    
    public Vector4 times(double d) {
        return new Vector4(array[0]*d, array[1]*d, array[2]*d, array[3]*d);
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