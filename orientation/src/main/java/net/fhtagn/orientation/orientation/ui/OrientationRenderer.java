package net.fhtagn.orientation.orientation.ui;

import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import net.fhtagn.orientation.orientation.math.Quaternion;
import net.fhtagn.orientation.orientation.math.Vec3;
import net.fhtagn.orientation.orientation.sensors.OrientationProvider;
import net.fhtagn.utils.utils.MathUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// This renders the phone rotation
// The world coordinate system is the same as in :
// http://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-rotate
// The phone coordinate system is described here :
// http://developer.android.com/reference/android/hardware/SensorEvent.html
//
// This means that by the device has a rotation of 0 on all axes when it is lying flat on the
// ground with the top of the screen pointing north
//
// We want to display the phone rotation wrt the world rotation. For the world, the yx plane
// defines the ground and the z axis is the vertical axis.
// If the device is hold in a perfect vertical position, it will still have an arbitrary rotation
// around the z axis (because y points north, but the user isn't, obviously, required to point
// the device north).
// So what we really want to display is the rotation w.r.t the y and x axis (and basically fix
// the rotation around z to 0).
public class OrientationRenderer implements GLSurfaceView.Renderer {
    private final SensorManager sensorManager;
    private PhoneModel phone;
    private Axis axis;

    private final OrientationProvider orientationProvider;

    public OrientationRenderer(Context ctx, OrientationProvider provider) {
        orientationProvider = provider;
        phone = new PhoneModel();
        axis = new Axis();

        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // 1. View setup
        // The OpenGL coordinate system is defined with :
        // - x to the right
        // - y up
        // - z out of the screen
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        // Move the camera back a little
        gl.glTranslatef(0, 0, -5);

        // 2. World Axis
        {
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            gl.glPushMatrix();
            //gl.glTranslatef(0, -0.2f, -5);
            axis.draw(gl);
            gl.glPopMatrix();

            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }

        gl.glPushMatrix();

        // The zero rotation is when the phone is lying down on a flat table, with the top of
        // the screen pointing north and the screen outwards the table
        // We want the zero to be when the phone is held vertically with the camera pointing north
        // (portrait mode)
        // Pre-multiply by the inverse of the landscape -> portrait transformation to define our
        // zero
        Quaternion portraitRot = Quaternion.fromAngleAxis(MathUtils.degToRad(90), new Vec3(1, 0, 0));

        final Quaternion rot = portraitRot.getConjugate().rightMult(
                orientationProvider.getOrientation());
        final Vec3 rotAxis = rot.getAxis();
        final float angle = MathUtils.radToDeg(rot.getAngle());
        gl.glRotatef(angle, rotAxis.x, rotAxis.y, rotAxis.z);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        axis.draw(gl);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        phone.draw(gl);
        gl.glPopMatrix();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        float ratio = (float) width / (float) height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, ratio, 0.1f, 100.0f);

    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        gl.glClearColor(1, 1, 1, 1);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    }


}
