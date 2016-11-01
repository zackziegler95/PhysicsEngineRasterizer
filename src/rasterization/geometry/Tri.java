/**
 * A simple triangle
 */

package rasterization.geometry;

import java.util.ArrayList;
import rasterization.tools.Vector3;

public class Tri {
    public Vector3[] verticies = new Vector3[3];
    public ArrayList<Edge> edges = new ArrayList<>();
    
    public double[] color = new double[3]; //RGB
    
    public Tri(Vector3 v1, Vector3 v2, Vector3 v3, int r, int g, int b) {
        verticies[0] = v1;
        verticies[1] = v2;
        verticies[2] = v3;
        edges.add(new Edge(v1, v2));
        edges.add(new Edge(v2, v3));
        edges.add(new Edge(v3, v1));
        
        color[0] = r/255.0;
        color[1] = g/255.0;
        color[2] = b/255.0;
    }
    
    public Vector3 getNormal() {
        Vector3 ab = verticies[1].minus(verticies[0]);
        Vector3 ac = verticies[2].minus(verticies[0]);
        return ab.cross(ac).normalize();
    }
    
    public int getR() {return (int)(color[0]*255);}
    public int getG() {return (int)(color[1]*255);}
    public int getB() {return (int)(color[2]*255);}
}
