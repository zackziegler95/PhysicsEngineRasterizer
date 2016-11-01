/**
 * Contacts are generated when two objects meet. Solving them amounts to resolving
 * the contact
 */

package rasterization.physics;

import rasterization.WorldObject;
import rasterization.tools.Vector3;

public class Contact {
    private Vector3 normal;
    private double dist;
    
    public WorldObject a, b;
    
    /**
     * Generate a contact
     * @param normal Normal vector of the collision
     * @param dist Distance between the two objects
     * @param a Object1
     * @param b Object2
     */
    public Contact(Vector3 normal, double dist, WorldObject a, WorldObject b) {
        this.normal = normal;
        this.a = a;
        this.b = b;
        this.dist = dist;
    }
    
    private void applyImpulse(Vector3 impulse) {
        a.vel = a.vel.plus(impulse.times(a.invMass));
        b.vel = b.vel.minus(impulse.times(b.invMass));
    }
    
    public void solve(double dt) {
        double relNV = b.vel.minus(a.vel).dot(normal);
        double remove = relNV + dist/dt;
        //System.out.println(remove);
        
        if (remove > 0) return;
        //System.out.println(relNV);
        //if (relNV > 0) return;
        
        //double e = 0.6;//relNV > -.2 ? 0 : Math.min(a.e, b.e);
        //double impScal = -(1 + e) * relNV / (a.invMass + b.invMass);
        double impScal = remove / (a.invMass + b.invMass);
        
        Vector3 impulse = normal.times(impScal);
        applyImpulse(impulse);
        
        Vector3 rv = b.vel.minus(a.vel);
        //System.out.println(normal.scalerMult(rv.dot(normal)));
        
        Vector3 tangentV = rv.minus(normal.times(rv.dot(normal)));
        
        double tMag = tangentV.mag();
        if (tMag != 0) {
            tangentV = tangentV.times(1/tMag);
            
            double jt = -rv.dot(tangentV) / (a.invMass + b.invMass);
            double mu = Math.sqrt(a.staticF*a.staticF + b.staticF*b.staticF);

            Vector3 frictionImp;
            if (Math.abs(jt) < impScal * mu) {
                frictionImp = tangentV.times(jt);
            } else {
                mu = Math.sqrt(a.dynamicF*a.dynamicF + b.dynamicF*b.dynamicF);
                frictionImp = tangentV.times(-impScal * mu);
            }
            applyImpulse(frictionImp);
        }
    }
    
    /*public void positionalCorrection() {
        double percent = .8;
        double slop = .001;
        
        Vector2D correction = normal.scalerMult(
                percent * Math.max(penetration - slop, 0)/ (a.invMass + b.invMass));
        a.pos = a.pos.minus(correction.scalerMult(a.invMass));
        b.pos = b.pos.plus(correction.scalerMult(b.invMass));
    }*/
}
