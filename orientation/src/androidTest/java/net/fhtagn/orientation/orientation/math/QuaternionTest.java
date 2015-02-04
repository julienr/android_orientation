package net.fhtagn.orientation.orientation.math;

import junit.framework.Assert;
import junit.framework.TestCase;

public class QuaternionTest extends TestCase {
    private final static String TAG = "QuaternionTest";

    public void testFromAngleAxis() {
        Vec3 axis = new Vec3(0.5f, 0.6f, 0.7f).getNormalized();
        float angle = (float)(Math.PI/2.0);
        Quaternion q = Quaternion.fromAngleAxis(angle, axis);
        Assert.assertEquals(q.getAngle(), angle, 1e-5);
        Assert.assertTrue(q.getAxis().toString() + "!=" + axis.toString(),
                          axis.almostEquals(q.getAxis(), 1e-5f));
    }

    public void testFromMat3() {
        Vec3 axis = new Vec3(0.5f, 0.6f, 0.7f).getNormalized();
        float angle = (float)(Math.PI/2.0);
        Mat3 m = Mat3.fromAxisAngle(angle, axis);
        Quaternion q = Quaternion.fromMatrix(m);
        Assert.assertEquals(q.getAngle(), angle, 1e-5);
        Assert.assertTrue(q.getAxis().toString() + "!=" + axis.toString(),
                axis.almostEquals(q.getAxis(), 1e-5f));
    }

    public void testRotate() {
        Vec3 v = Vec3.X_AXIS;
        Quaternion q = Quaternion.fromAngleAxis((float)(Math.PI/2.0), Vec3.Z_AXIS);
        Vec3 vp = q.rotate(v);
        Assert.assertTrue(v.toString() + "!=" + vp.toString(),
                Vec3.Y_AXIS.almostEquals(vp, 1e-5f));
    }
}
