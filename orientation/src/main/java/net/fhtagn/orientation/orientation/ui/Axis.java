package net.fhtagn.orientation.orientation.ui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Axis {
  private IntBuffer vertexBuffer;
  private IntBuffer colorBuffer;
  private ByteBuffer indexBuffer;
  
  public Axis() {
    final int one = 0x10000;
    final int vertices[] = {
        0, 0, 0,
        one, 0, 0,
        0, 0, 0,
        0, one, 0,
        0, 0, 0,
        0, 0, one
    };
    
    int colors[] = {
        one, 0, 0, one,
        one, 0, 0, one,
        0, one, 0, one,
        0, one, 0, one,
        0, 0, one, one,
        0, 0, one, one
    };
    
    final byte indices[] = {
        0, 1,
        2, 3,
        4, 5
    };
    
    final ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
    vbb.order(ByteOrder.nativeOrder());
    vertexBuffer = vbb.asIntBuffer();
    vertexBuffer.put(vertices);
    vertexBuffer.position(0);
    
    final ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
    cbb.order(ByteOrder.nativeOrder());
    colorBuffer = cbb.asIntBuffer();
    colorBuffer.put(colors);
    colorBuffer.position(0);

    indexBuffer = ByteBuffer.allocateDirect(indices.length);
    indexBuffer.put(indices);
    indexBuffer.position(0);
  }
  
  public void draw(GL10 gl) {
    gl.glFrontFace(GL10.GL_CW);
    gl.glVertexPointer(3, GL10.GL_FIXED, 0, vertexBuffer);
    gl.glColorPointer(4, GL10.GL_FIXED, 0, colorBuffer);
    gl.glDrawElements(GL10.GL_LINES, 6, GL10.GL_UNSIGNED_BYTE, indexBuffer);
  }
}
