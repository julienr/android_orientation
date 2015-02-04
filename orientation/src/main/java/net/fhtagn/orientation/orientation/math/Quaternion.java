package net.fhtagn.orientation.orientation.math;

// See http://content.gpwiki.org/index.php/OpenGL:Tutorials:Using_Quaternions_to_represent_rotation

import net.fhtagn.utils.utils.MathUtils;

// Immutable NORMALIZED quaternion
public class Quaternion {
  private static final String TAG = "Quaternion";

  public final static Quaternion IDENTITY = fromAngleAxis(0, new Vec3(1,0,0));
  
  // We ONLY store normalized quaternion
  public final float r, x, y, z;
  
  /**
   * Create a new quaternion representing a rotation of a given angle around
   * an axis
   * @param angle the angle in radian
   * @param axis
   * @return
   */
  public final static Quaternion fromAngleAxis(float angle, Vec3 axis) {
    final Vec3 tmp = axis.getNormalized();
    final float sin_a = (float)Math.sin(angle/2.0);
    final float cos_a = (float)Math.cos(angle/2.0);
    return new Quaternion(cos_a, tmp.x*sin_a, tmp.y*sin_a, tmp.z*sin_a);
  }
  
  // http://code.google.com/p/libgdx/source/browse/trunk/gdx/src/com/badlogic/gdx/math/Quaternion.java
  public final static Quaternion fromAxes(float xx, float xy, float xz,
                                          float yx, float yy, float yz,
                                          float zx, float zy, float zz) {
    // the trace is the sum of the diagonal elements; see
    // http://mathworld.wolfram.com/MatrixTrace.html
    final float m00 = xx, m01 = yx, m02 = zx;
    final float m10 = xy, m11 = yy, m12 = zy;
    final float m20 = xz, m21 = yz, m22 = zz;
    final float t = m00 + m11 + m22;

    // we protect the division by s by ensuring that s>=1
    double x, y, z, w;
    if (t >= 0) { // |w| >= .5
            double s = Math.sqrt(t + 1); // |s|>=1 ...
            w = 0.5 * s;
            s = 0.5 / s; // so this division isn't bad
            x = (m21 - m12) * s;
            y = (m02 - m20) * s;
            z = (m10 - m01) * s;
    } else if ((m00 > m11) && (m00 > m22)) {
            double s = Math.sqrt(1.0 + m00 - m11 - m22); // |s|>=1
            x = s * 0.5; // |x| >= .5
            s = 0.5 / s;
            y = (m10 + m01) * s;
            z = (m02 + m20) * s;
            w = (m21 - m12) * s;
    } else if (m11 > m22) {
            double s = Math.sqrt(1.0 + m11 - m00 - m22); // |s|>=1
            y = s * 0.5; // |y| >= .5
            s = 0.5 / s;
            x = (m10 + m01) * s;
            z = (m21 + m12) * s;
            w = (m02 - m20) * s;
    } else {
            double s = Math.sqrt(1.0 + m22 - m00 - m11); // |s|>=1
            z = s * 0.5; // |z| >= .5
            s = 0.5 / s;
            x = (m02 + m20) * s;
            y = (m21 + m12) * s;
            w = (m10 - m01) * s;
    }

    return new Quaternion((float)w, (float)x, (float)y, (float)z);
  }
  
  /**
   * Set quaternion from rotation matrix
   */
  public final static Quaternion fromMatrix(Mat3 mat) {
    return fromAxes(mat.at(0,0), mat.at(1,0), mat.at(2,0),
                    mat.at(0,1), mat.at(1,1), mat.at(2,1),
                    mat.at(0,2), mat.at(1,2), mat.at(2,2));
  }
  
  public Quaternion(float r, float x, float y, float z) {
    final float magnitude = (float)Math.sqrt(r*r + x*x + y*y + z*z);
    this.r = r/magnitude;
    this.x = x/magnitude;
    this.y = y/magnitude;
    this.z = z/magnitude;
  }
  
  public Quaternion(Quaternion q) {
    this.r = q.r;
    this.x = q.x;
    this.y = q.y;
    this.z = q.z;
  }

  // Identity quaternion
  public Quaternion() {
    this.r = 1;
    this.x = this.y = this.z = 0;
  }
  
  public Quaternion getConjugate() {
    return new Quaternion(r, -x, -y, -z);
  }
  
  public Quaternion rightMult(Quaternion q) {
    return new Quaternion(r*q.r - x*q.x - y*q.y - z*q.z, 
                          r*q.x + x*q.r + y*q.z - z*q.y,
                          r*q.y + y*q.r + z*q.x - x*q.z,
                          r*q.z + z*q.r + x*q.y - y*q.x);
  }
  
  /**
   * Rotate a vector by this quaternion
   * @param v the vector to rotate
   * @return the rotated vector
   */
  public Vec3 rotate(Vec3 v) {
    final Vec3 qvec = new Vec3(x, y, z);
    Vec3 uv = qvec.cross(v);
    Vec3 uuv = qvec.cross(uv);
    uv = uv.times(2.0f*r);
    uuv = uuv.times(2.0f);
    return v.add(uv).add(uuv);
  }
  
  public float getAngle() {
    return ((float)Math.acos(r))*2.0f;
  }
  
  public Vec3 getAxis() {
    final float cos_a = r;
    final float sin_a = (float)Math.sqrt(1.0-cos_a*cos_a);
    if (Math.abs(sin_a) < MathUtils.EPSILON) {
      return new Vec3(x, y, z);
    } else {
      return new Vec3(x/sin_a, y/sin_a, z/sin_a);
    }
  }

  public boolean almostEquals(Quaternion o, float delta) {
      return MathUtils.floatEq(r, o.r, delta) &&
             MathUtils.floatEq(x, o.x, delta) &&
             MathUtils.floatEq(y, o.y, delta) &&
             MathUtils.floatEq(z, o.z, delta);
  }
  
  /**
   * Convert the quaternion to a rotation matrix.
   */
  public Mat3 toMatrix() {
    return new Mat3(1.0f - 2.0f*(y*y+z*z), 2.0f*(x*y-z*r), 2.0f*(x*z+y*r),
                    2.0f*(x*y+z*r), 1.0f - 2.0f*(x*x+z*z), 2.0f*(y*z-x*r),
                    2.0f*(x*z-y*r), 2.0f*(z*y+x*r), 1.0f - 2.0f*(x*x+y*y));
  }

  public String toString() {
    return "quat(r=" + r + ", xyz=" + x + ", " + y + ", " + z + ")";
  }
}
