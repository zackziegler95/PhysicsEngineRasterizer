/**
 * Lighting base class
 */

package rasterization.light;

import rasterization.geometry.Poly;
import rasterization.tools.Utils;
import rasterization.tools.Vector3;

public abstract class Light {
    Vector3 color; // 0 to 1
    
    public Light(Vector3 color) {
        double l1 = Utils.clamp01(color.array[0]);
        double l2 = Utils.clamp01(color.array[1]);
        double l3 = Utils.clamp01(color.array[2]);
        this.color = new Vector3(l1, l2, l3);
    }
    
    public abstract Vector3 getColors(Poly p, Vector3 cameraPos);
}
