/**
 * A directional light, like a spotlight
 */

package rasterization.light;

import rasterization.geometry.Poly;
import rasterization.tools.Utils;
import rasterization.tools.Vector3;

public class DirectionalLight extends Light {
    public Vector3 dir;
    public Vector3 Is;
    
    /**
     * @param Id Diffuse color intensity (RGB)
     * @param Is Specular color intensity (RGB)
     * @param dir Direction of the light
     */
    public DirectionalLight(Vector3 Id, Vector3 Is, Vector3 dir) {
        super(Id);
        double l1 = Utils.clamp01(Is.array[0]);
        double l2 = Utils.clamp01(Is.array[1]);
        double l3 = Utils.clamp01(Is.array[2]);
        this.Is = new Vector3(l1, l2, l3);
        this.dir = dir.normalize();
    }
    
    @Override
    public Vector3 getColors(Poly p, Vector3 cameraPos) {
        double ldn = dir.dot(p.normal);
        Vector3 diffuse = new Vector3(Utils.clamp01(ldn*color.array[0]*p.color.array[0]),
                Utils.clamp01(ldn*color.array[1]*p.color.array[1]),
                Utils.clamp01(ldn*color.array[2]*p.color.array[2]));
        
        if (p.shininess != 0) {
            Vector3 reflection = dir.times(-1).plus(p.normal.times(2*ldn));
            Vector3 v = cameraPos.minus(p.center).normalize();
            double rdv = reflection.dot(v);
            
            if (rdv > 0) {
                double rdvPow = rdv;
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
