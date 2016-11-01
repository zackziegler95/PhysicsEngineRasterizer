/**
 * A finite 2D plane
 */

package rasterization;

import java.util.ArrayList;
import rasterization.geometry.Poly;
import rasterization.geometry.Tri;
import rasterization.physics.Contact;
import rasterization.tools.Vector3;

public class Plane extends WorldObject {
    public Vector3 normal;
    
    /**
     * Constructor
     * 
     * @param p1 (x,y,z) corner 1
     * @param p2 (x,y,z) corner 2
     * @param p3 (x,y,z) corner 3
     * @param p4 (x,y,z) corner 4
     * @param n Number of subdivisions
     * @param s Shininess
     * @param red R color value
     * @param green G color value
     * @param blue B color value
     * @param invMass 1/mass
     */
    public Plane(Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, int n, int s, int red, int green, int blue, double invMass) {
        Tri t1 = new Tri(p1, p2, p3, 0, 0, 0);
        normal = t1.getNormal();
        double test = normal.dot(p4.minus(p1));
        if (Math.abs(test) > 0.001) {
            System.err.println("Error: this plane object is not on a plane");
            System.exit(1);
        }
        
        pos = p1.plus(p2).plus(p3).plus(p4).times(0.25);
        vel = new Vector3(0, 0, 0);
        this.invMass = invMass;
        
        polygons = new ArrayList<>();
        makePolygons(p1.minus(pos), p2.minus(pos), p3.minus(pos), p4.minus(pos), n, s, red, green, blue);
    }
    
    @Override
    public Contact generateContact(WorldObject wo) {
        if (wo.getClass() == Ball.class) {
            Ball b = (Ball) wo;
            
            Vector3 relPos = b.pos.minus(pos);
            double relPosDN = relPos.dot(normal);
            Vector3 closestVec = normal.times(relPosDN);
            //if (closestVec.magSq() > b.r*b.r) return null;
            
            Vector3 trueN = normal;
            if (relPosDN < 0) {
                trueN = normal.times(-1);
            }
            
            return new Contact(trueN, closestVec.mag()-b.r, this, b);
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private void makePolygons(Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, int n, int s, int red, int green, int blue) {
        Poly t1 = new Poly(new Vector3[]{
            p4, p1, p2
        }, red, green, blue, s);
        Poly t2 = new Poly(new Vector3[]{
            p2, p3, p4
        }, red, green, blue, s);
        
        ArrayList<Poly> allOne = new ArrayList<>();
        ArrayList<Poly> allTwo = new ArrayList<>();
        allOne.add(t1);
        allOne.add(t2);
        
        for (int i = 0; i < n; i++) {
            for (Poly p : allOne) {
                allTwo.addAll(p.subdivide());
            }
            allOne.clear();
            allOne.addAll(allTwo);
            allTwo.clear();
        }
        
        polygons.addAll(allOne);
    }
}
