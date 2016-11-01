/**
 * The most basic element, everything is made up of sets of edges
 */

package rasterization.geometry;

import java.util.Comparator;
import rasterization.tools.Vector3;

public class Edge {
    public static GETSort GET = new GETSort();
    public static AETSort AET = new AETSort();
    
    public double ymin;
    public double ymax;
    public double xval;
    public double invm;
    public double zvalMin;
    public double zvalMax;

    /**
     * Generates an edge between two points
     * @param v1 Point 1
     * @param v2 Point 2
     */
    public Edge(Vector3 v1, Vector3 v2) {
        if (v1.array[1] > v2.array[1]) {
            ymax = v1.array[1];
            ymin = v2.array[1];
            xval = v2.array[0];
            zvalMin = v2.array[2];
            zvalMax = v1.array[2];
        } else {
            ymax = v2.array[1];
            ymin = v1.array[1];
            xval = v1.array[0];
            zvalMin = v1.array[2];
            zvalMax = v2.array[2];
        }
        invm = (v2.array[0]-v1.array[0])/(v2.array[1]-v1.array[1]);
    }
    
    public static class GETSort implements Comparator<Edge> {
        @Override
        public int compare(Edge e1, Edge e2) {
            if (e1.ymin < e2.ymin) return -1;
            if (e1.ymin > e2.ymin) return 1;
            if (e1.xval < e2.xval) return -1;
            return 1;
        }
    }
    
    public static class AETSort implements Comparator<Edge> {
        @Override
        public int compare(Edge e1, Edge e2) {
            if (e1.xval < e2.xval) return -1;
            return 1;
        }
    }
    
    @Override
    public String toString() {
        return "ymin: "+ymin+", ymax:"+ymax+", xval: "+xval+", 1/m:"+invm;
    }
}