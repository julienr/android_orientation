package net.fhtagn.orientation.orientation.ui;

import javax.microedition.khronos.opengles.GL10;

// An abstract representation of a phone (a rectangular box with a back-facing
// camera)
public class PhoneModel {
    Cube cube;

    public PhoneModel() {
        cube = new Cube();
    }

    public void draw(GL10 gl) {
        // Draw camera objective (draw first because of blending)
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.6f, -0.15f);// 0.15 = 0.05 (phone depth) + 0.1f (objective depth)
        gl.glScalef(0.1f, 0.1f, 0.1f);
        gl.glColor4f(1, 0, 0, 0.5f);
        cube.draw(gl);
        gl.glPopMatrix();

        // Draw base
        gl.glPushMatrix();
        gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
        gl.glScalef(0.4f, 0.8f, 0.05f);
        cube.draw(gl);
        gl.glPopMatrix();

        gl.glColor4f(1, 1, 1, 1);
    }

}
