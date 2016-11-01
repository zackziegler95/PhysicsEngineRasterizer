/**
 * 3x3 Matrix
 */

package rasterization.tools;

public class Matrix33 {
    public double[][] array;
    
    public Matrix33(double[][] array) {
        if (array.length != 3 || array[0].length != 3) {
            System.err.println("Error: array passed to Matrix44 constructor is the wrong size.");
        }
        
        this.array = new double[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(array[i], 0, this.array[i], 0, 3);
        }
    }
    
    public Matrix33() {
        this(new double[][]{new double[]{1, 0, 0}, new double[]{0, 1, 0}, new double[]{0, 0, 1}});
    }
    
    public Vector3 getC(int n) {
        return new Vector3(array[0][n], array[1][n], array[2][n]);
    }
    
    public Vector3 getR(int n) {
        return new Vector3(array[n][0], array[n][1], array[n][2]);
    }
    
    public Matrix33 times(Matrix33 m) {
        return new Matrix33(new double[][]{
            new double[]{getR(0).dot(m.getC(0)), getR(0).dot(m.getC(1)), getR(0).dot(m.getC(2))},
            new double[]{getR(1).dot(m.getC(0)), getR(1).dot(m.getC(1)), getR(1).dot(m.getC(2))},
            new double[]{getR(2).dot(m.getC(0)), getR(2).dot(m.getC(1)), getR(2).dot(m.getC(2))}
        });
    }
    
    public Vector3 times(Vector3 v) {
        return new Vector3(getR(0).dot(v), getR(1).dot(v), getR(2).dot(v));
    }
    
    public Matrix33 transpose() {
        return new Matrix33(new double[][]{getC(0).array, getC(1).array, getC(2).array});
    }
    
    public final void printMatrix() {
        System.out.print("{");
        for (int i = 0; i < array.length-1; i++) {
            System.out.print("{");
            for (int j = 0; j < array[i].length-1; j++) {
                System.out.print(array[i][j]+", ");
            }
            System.out.println(array[i][array[i].length-1]+"}");
        }
        System.out.print("{");
        for (int j = 0; j < array[array.length-1].length-1; j++) {
            System.out.print(array[array.length-1][j]+", ");
        }
        System.out.println(array[array.length-1][array[array.length-1].length-1]+"}}");
    }
}
