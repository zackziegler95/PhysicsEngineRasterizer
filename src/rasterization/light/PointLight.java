/**
 * A spherical point source of light
 */

package rasterization.light;

import rasterization.geometry.Poly;
import rasterization.tools.Utils;
import rasterization.tools.Vector3;

public class PointLight extends Light {
    private Vector3 pos;
    private Vector3 Is;
    
    /**
     * @param Id Diffuse color intensity (RGB)
     * @param Is Specular color intentisy (RBG)
     * @param pos Position of the source
     */
    public PointLight(Vector3 Id, Vector3 Is, Vector3 pos) {
        super(Id);
        double l1 = Utils.clamp01(Is.array[0]);
        double l2 = Utils.clamp01(Is.array[1]);
        double l3 = Utils.clamp01(Is.array[2]);
        this.Is = new Vector3(l1, l2, l3);
        this.pos = new Vector3(pos);
    }
    
    @Override
    public Vector3 getColors(Poly p, Vector3 cameraPos) {
        Vector3 dir = pos.minus(p.center);
        double distSq = dir.dot(dir)*.1;
        if (distSq == 0) return new Vector3(0, 0, 0);
        dir = dir.normalize();
        
        double ldn = dir.dot(p.normal);
        Vector3 diffuse = new Vector3(Utils.clamp01(ldn*color.array[0]*p.color.array[0]/distSq),
                Utils.clamp01(ldn*color.array[1]*p.color.array[1]/distSq),
                Utils.clamp01(ldn*color.array[2]*p.color.array[2]/distSq));
        
        if (p.shininess != 0) {
            Vector3 reflection = dir.times(-1).plus(p.normal.times(2*ldn));
            Vector3 v = cameraPos.minus(p.center).normalize();
            double rdv = reflection.dot(v);
            
            if (rdv > 0) {
                double rdvPow = rdv/distSq;
                for (int i = 1; i < p.shininess; i++) {
                    rdvPow *= rdv;
                }

                diffuse.array[0] = Utils.clamp01(diffuse.array[0]+Is.array[0]*rdvPow);
                diffuse.array[1] = Utils.clamp01(diffuse.array[1]+Is.array[1]*rdvPow);
                diffuse.array[2] = Utils.clamp01(diffuse.array[2]+Is.array[2]*rdvPow);
            }
        }
        
        return diffuse;
    }
    
}
