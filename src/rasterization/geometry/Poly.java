/**
 * Any arbitrary polygon
 */

package rasterization.geometry;

import java.util.ArrayList;
import rasterization.tools.Utils;
import rasterization.tools.Vector3;

public class Poly {
    public Vector3[] v;
    public ArrayList<Edge> edges = new ArrayList<>();
    public Vector3 color; //RGB
    public int shininess;
    public Vector3 normal;
    public Vector3 center;
    
    /**
     * Generates a polygon from a list of points
     * @param v List of points
     * @param r R color value
     * @param g G color value
     * @param b B color value
     * @param shininess 
     */
    public Poly(Vector3[] v, int r, int g, int b, int shininess) {
        this(v, r, g, b, shininess, null);
    }
    
    /**
     * Generates a polygon from a list of points, with the normal direction
     * already included
     * @param v List of points
     * @param r R color value
     * @param g G color value
     * @param b B color value
     * @param shininess 
     * @param normal Normal vector in 3-space
     */
    public Poly(Vector3[] v, int r, int g, int b, int shininess, Vector3 normal) {
        this.v = new Vector3[v.length];
        System.arraycopy(v, 0, this.v, 0, v.length);
        
        if (normal == null) {
            Tri t1 = new Tri(v[0], v[1], v[2], r, g, b);
            this.normal = t1.getNormal();
        
            for (int i = 3; i < v.length; i++) {
                double test = this.normal.dot(v[i].minus(v[0]));
                if (Math.abs(test) > 0.001) {
                    System.err.println("Error: this polygon is not on a plane");
                    System.exit(1);
                }
            }
        } else {
            this.normal = new Vector3(normal);
        }
        
        for (int i = 1; i < v.length; i++) {
            edges.add(new Edge(v[i-1], v[i]));
        }
        edges.add(new Edge(v[v.length-1], v[0]));
        
        color = new Vector3(Utils.clamp01(r/255.0), Utils.clamp01(g/255.0), Utils.clamp01(b/255.0));
        
        center = new Vector3(0, 0, 0);
        for (int i = 0; i < v.length; i++) {
            center = center.plus(v[i]);
        }
        center = center.times(1.0/v.length);
        
        this.shininess = shininess;
    }
    
    public ArrayList<Poly> subdivide() {
        if (v.length == 3) {
            ArrayList<Poly> ret = new ArrayList<>();

            Vector3 mp1 = v[0].plus(v[1]).times(0.5);
            Vector3 mp2 = v[1].plus(v[2]).times(0.5);
            Vector3 mp3 = v[2].plus(v[0]).times(0.5);

            ret.add(new Poly(new Vector3[]{mp3, v[0], mp1}, getR(), getG(), getB(), shininess));
            ret.add(new Poly(new Vector3[]{mp1, v[1], mp2}, getR(), getG(), getB(), shininess));
            ret.add(new Poly(new Vector3[]{mp2, v[2], mp3}, getR(), getG(), getB(), shininess));
            ret.add(new Poly(new Vector3[]{mp3, mp1, mp2}, getR(), getG(), getB(), shininess));

            return ret;
        } else if (v.length == 4) {
            ArrayList<Poly> ret = new ArrayList<>();

            Vector3 mp1 = v[0].plus(v[1]).times(0.5);
            Vector3 mp2 = v[1].plus(v[2]).times(0.5);
            Vector3 mp3 = v[2].plus(v[3]).times(0.5);
            Vector3 mp4 = v[3].plus(v[0]).times(0.5);
            Vector3 c = v[0].plus(v[2]).times(0.5);

            ret.add(new Poly(new Vector3[]{mp4, v[0], mp1, c}, getR(), getG(), getB(), shininess));
            ret.add(new Poly(new Vector3[]{mp1, v[1], mp2, c}, getR(), getG(), getB(), shininess));
            ret.add(new Poly(new Vector3[]{mp2, v[2], mp3, c}, getR(), getG(), getB(), shininess));
            ret.add(new Poly(new Vector3[]{mp3, v[3], mp4, c}, getR(), getG(), getB(), shininess));

            return ret;
        } else {
            System.err.println("Error, tring to subdivide something that isn't a triangle or a rectangle.");
            System.exit(1);
            return null;
        }
        
    }
    
    public int getR() {return (int)(color.array[0]*255);}
    public int getG() {return (int)(color.array[1]*255);}
    public int getB() {return (int)(color.array[2]*255);}
}
