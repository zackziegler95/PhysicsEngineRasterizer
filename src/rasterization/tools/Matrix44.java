/**
 * 4x4 Matrix
 */

package rasterization.tools;

public class Matrix44 {
    public double[][] array;
    
    public Matrix44(double[][] array) {
        if (array.length != 4 || array[0].length != 4) {
            System.err.println("Error: array passed to Matrix44 constructor is the wrong size.");
        }
        
        this.array = new double[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(array[i], 0, this.array[i], 0, 4);
        }
    }
    
    public Matrix44() {
        this(new double[][]{
            new double[]{1, 0, 0, 0}, new double[]{0, 1, 0, 0},
            new double[]{0, 0, 1, 0}, new double[]{0, 0, 0, 1}
        });
    }
    
    public Vector4 getC(int n) {
        return new Vector4(array[0][n], array[1][n], array[2][n], array[3][n]);
    }
    
    public Vector4 getR(int n) {
        return new Vector4(array[n][0], array[n][1], array[n][2], array[n][3]);
    }
    
    public Vector4 times(Vector4 v) {
        return new Vector4(getR(0).dot(v), getR(1).dot(v), getR(2).dot(v), getR(3).dot(v));
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
