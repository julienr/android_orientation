package net.fhtagn.orientation.orientation.math;

import net.fhtagn.utils.utils.MathUtils;

// Immutable vector 3
public class Vec3 {
  public final static Vec3 X_AXIS = new Vec3(1,0,0);
  public final static Vec3 Y_AXIS = new Vec3(0,1,0);
  public final static Vec3 Z_AXIS = new Vec3(0,0,1);
  
  public final float x, y, z;
  
  public Vec3() {
    x = y = z = 0;
  }
  
  public Vec3(float[] arr) {
    if (arr.length != 3) {
      throw new IllegalArgumentException("Expect an array of length 3");
    }
    x = arr[0];
    y = arr[1];
    z = arr[2];
  }
  
  public Vec3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Vec3 add(Vec3 o) {
    return new Vec3(x+o.x, y+o.y, z+o.z);
  }

  public Vec3 subtract(Vec3 o) { return new Vec3(x-o.x, y-o.y, z-o.z); }
  public Vec3 times(float f) {
    return new Vec3(f*x, f*y, f*z);
  }
  
  public float norm() {
    return (float)Math.sqrt(x*x + y*y + z*z);
  }
  
  public Vec3 getNormalized() {
    final float n = norm();
    if (MathUtils.floatEq(n, 0)) {
        return new Vec3(0, 0, 0);
    } else {
        return new Vec3(x / n, y / n, z / n);
    }
  }
  
  /**
   * Cross product
   */
  public Vec3 cross(Vec3 v) {
    return new Vec3(y*v.z - z*v.y,
                    z*v.x - x*v.z,
                    x*v.y - y*v.x);
  }

  // Returns a vector that is orthogonal to this one
  public Vec3 orthogonalUnitVector() {
    // This is the quick'n'dirty solution from :
    // http://blog.selfshadow.com/2011/10/17/perp-vectors/
    // This takes the cross product with a fixed vector (here, UP = y axis) and then normalize.
    //
    final Vec3 unitThis = this.getNormalized();

    final Vec3 o;
    if (Math.abs(unitThis.y) < 0.99) { // abs(dot(this, UP)) # check that we are not collinear
        o = new Vec3(-unitThis.z, 0, unitThis.x); // cross(u, UP)
    } else {
        o = new Vec3(0, unitThis.z, -unitThis.y); // cross(u, RIGHT)
    }
    return o.getNormalized();
  }

  public float dot(Vec3 v) {
    return x*v.x + y*v.y + z*v.z;
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Vec3)) {
      return false;
    }
    final Vec3 v = (Vec3)o;
    return MathUtils.floatEq(x, v.x) && MathUtils.floatEq(y, v.y)
        && MathUtils.floatEq(z, v.z);
  }

  public boolean almostEquals(Vec3 o, float d) {
      return MathUtils.floatEq(x, o.x, d) &&
             MathUtils.floatEq(y, o.y, d) &&
             MathUtils.floatEq(z, o.z, d);
  }
  
  @Override
  public String toString() {
    return IO.vectorToString(new float[]{x, y, z});
  }
}
