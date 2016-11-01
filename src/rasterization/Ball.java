/**
 * A sphere, to be entered into the world
 */

package rasterization;

import java.util.ArrayList;
import rasterization.geometry.Poly;
import rasterization.physics.Contact;
import rasterization.tools.Vector3;

public class Ball extends WorldObject {
    public double r;
    
    /**
     * Constructor
     * 
     * @param pos (x,y,z) Position
     * @param vel (x,y,z) Velocity
     * @param r Radius
     * @param n Number of subdivisions
     * @param s Shininess
     * @param red R color value
     * @param green G color value
     * @param blue B color value
     * @param invMass 1/mass
     */
    public Ball(Vector3 pos, Vector3 vel, double r, int n, int s, int red, int green, int blue, double invMass) {
        polygons = new ArrayList<>();
        makePolygons(r, n, s, red, green, blue);
        
        this.pos = new Vector3(pos);
        this.vel = new Vector3(vel);
        this.r = r;
        this.invMass = invMass;
    }
    
    @Override
    public Contact generateContact(WorldObject wo) {
        if (wo.getClass() == Plane.class) { // Contact with a plane
            Plane p = (Plane) wo;
            
            Vector3 relPos = pos.minus(p.pos);
            double relPosDN = relPos.dot(p.normal);
            Vector3 closestVec = p.normal.times(relPosDN);
            //if (closestVec.magSq() > r*r) return null;
            //System.out.println("contact");
            Vector3 trueN = p.normal;
            if (relPosDN < 0) {
                trueN = p.normal.times(-1);
            }
            //System.out.println(trueN);
            
            return new Contact(trueN, closestVec.mag()-r, p, this);
        } else if (wo.getClass() == Ball.class) { // Contact with another sphere
            Ball b = (Ball) wo;
            
            Vector3 relPos = pos.minus(b.pos);
            double dist = relPos.mag();
            return new Contact(relPos.times(1/dist), dist-r-b.r, b, this);
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    /**
     * Generate the set of polygons that make up the sphere
     * 
     * @param r Radius
     * @param n Number of subdivisions
     * @param s Shininess
     * @param red R color value
     * @param green G color value
     * @param blue B color value
     */
    private void makePolygons(double r, int n, int s, int red, int green, int blue) {
        int x = 0, y = 0, z = 0;
        
        Poly flt = new Poly(new Vector3[]{
            new Vector3(x, y, z+r),
            new Vector3(x, y+r, z),
            new Vector3(x-r, y, z)},
            red, green, blue, s);
        
        Poly frt = new Poly(new Vector3[]{
            new Vector3(x, y, z+r),
            new Vector3(x+r, y, z),
            new Vector3(x, y+r, z)},
            red, green, blue, s);
        
        Poly blt = new Poly(new Vector3[]{
            new Vector3(x, y, z-r),
            new Vector3(x-r, y, z),
            new Vector3(x, y+r, z)},
            red, green, blue, s);
        
        Poly brt = new Poly(new Vector3[]{
            new Vector3(x, y, z-r),
            new Vector3(x, y+r, z),
            new Vector3(x+r, y, z)},
            red, green, blue, s);
        
        Poly flb = new Poly(new Vector3[]{
            new Vector3(x, y, z+r),
            new Vector3(x-r, y, z),
            new Vector3(x, y-r, z)},
            red, green, blue, s);
        
        Poly frb = new Poly(new Vector3[]{
            new Vector3(x, y, z+r),
            new Vector3(x, y-r, z),
            new Vector3(x+r, y, z)},
            red, green, blue, s);
        
        Poly blb = new Poly(new Vector3[]{
            new Vector3(x, y, z-r),
            new Vector3(x, y-r, z),
            new Vector3(x-r, y, z)},
            red, green, blue, s);
        
        Poly brb = new Poly(new Vector3[]{
            new Vector3(x, y, z-r),
            new Vector3(x+r, y, z),
            new Vector3(x, y-r, z)},
            red, green, blue, s);
        
        
        ArrayList<Poly> allOne = new ArrayList<>();
        ArrayList<Poly> allTwo = new ArrayList<>();
        allOne.add(flt);
        allOne.add(frt);
        allOne.add(brt);
        allOne.add(blt);
        allOne.add(flb);
        allOne.add(frb);
        allOne.add(brb);
        allOne.add(blb);
        
        for (int i = 0; i < n; i++) {
            for (Poly p : allOne) {
                allTwo.addAll(p.subdivide());
            }
            allOne.clear();
            allOne.addAll(allTwo);
            allTwo.clear();
        }
        
        makePolygonsSpherical(allOne, r);
        polygons.addAll(allOne);
    }
    
    /**
     * Turn a set of polygons into a sphere
     * 
     * @param polygons The set of polygons. This will be modified on return
     * @param r  The radius of the sphere
     */
    private void makePolygonsSpherical(ArrayList<Poly> polygons, double r) {
        Vector3 center = new Vector3(0, 0, 0);
        
        ArrayList<Poly> input = new ArrayList<>();
        input.addAll(polygons);
        polygons.clear();
        for (Poly p : input) {
            Vector3[] newVerticies = new Vector3[p.v.length];
            for (int i = 0; i < p.v.length; i++) {
                newVerticies[i] = p.v[i].normalize(center, r);
            }
            polygons.add(new Poly(newVerticies, p.getR(), p.getG(), p.getB(), p.shininess));
        }
    }
}
