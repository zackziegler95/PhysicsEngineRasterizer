/**
 * Base class for all objects
 * So far translation is included but rotation is not
 * 
 */

package rasterization;

import java.util.ArrayList;
import rasterization.geometry.Poly;
import rasterization.physics.Contact;
import rasterization.tools.Matrix33;
import rasterization.tools.Vector3;

public abstract class WorldObject {
    protected ArrayList<Poly> polygons;
    public Vector3 pos;
    public Vector3 vel;
    public double invMass;
    
    // Constants here, for now
    public double staticF = 0.1;
    public double dynamicF = 0.1;
    
    public ArrayList<Poly> getPolys(Vector3 camPos, Matrix33 camRotMatrix) {
        ArrayList<Poly> ret = new ArrayList<>();
        
        for (Poly p : polygons) {
            ArrayList<Vector3> newVerts = new ArrayList<>();
            for (Vector3 vert : p.v) {
                //newVerts.add(camRotMatrix.times(vert.plus(pos).minus(camPos)));
                newVerts.add(vert.plus(pos));
            }
            ret.add(new Poly(newVerts.toArray(new Vector3[0]), p.getR(), p.getG(), p.getB(), p.shininess, p.normal));
        }
        
        return ret;
    }
    
    // All objects need to have a way of defining contacts
    public abstract Contact generateContact(WorldObject wo);
    
    // Take a step
    public void integrate(double dt) {
        pos = pos.plus(vel.times(dt));
    }
}
