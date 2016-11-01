/**
 * Ambient light that illuminates everything
 */

package rasterization.light;

import rasterization.geometry.Poly;
import rasterization.tools.Vector3;

public class AmbientLight extends Light {
    public AmbientLight(double Ia) {
        super(new Vector3(Ia, Ia, Ia));
    }

    @Override
    public Vector3 getColors(Poly p, Vector3 cameraPos) {
        return new Vector3(color.array[0]*p.color.array[0],
                color.array[1]*p.color.array[1],
                color.array[2]*p.color.array[2]);
    }
}
