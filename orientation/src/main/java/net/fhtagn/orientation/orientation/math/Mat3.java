package net.fhtagn.orientation.orientation.math;

import android.opengl.Matrix;

import net.fhtagn.utils.utils.Check;
import net.fhtagn.utils.utils.MathUtils;

import java.text.DecimalFormat;

public class Mat3 {
  private static final DecimalFormat decimalFormat = new DecimalFormat(" * 0.0000;-#");
  
  // data is stored in column-major order
  float[] data = new float[9];
  
  public final static Mat3 fromColumnMajorArray(float[] mat) {
    Check.Eq(mat.length, 9);
    return new Mat3(mat[0], mat[3], mat[6],
                    mat[1], mat[4], mat[7],
                    mat[2], mat[5], mat[8]);
  }
  
  public final static Mat3 fromRowMajorArray(float[] mat) {
    Check.Eq(mat.length, 9);
    return new Mat3(mat[0], mat[1], mat[2],
                    mat[3], mat[4], mat[5],
                    mat[6], mat[7], mat[8]);
  }
  
  // Angle is in RADIAN
  public final static Mat3 fromAxisAngle(float angle, Vec3 axis) {
    // Android's Matrix utility functions work on column major matrices
    float[] mat = new float[16];
    Matrix.setIdentityM(mat, 0);
    Matrix.rotateM(mat, 0, MathUtils.radToDeg(angle), axis.x, axis.y, axis.z);
    return new Mat3(mat[0], mat[4], mat[8],
                    mat[1], mat[5], mat[9],
                    mat[2], mat[6], mat[10]);
  }
  
  public final static Mat3 identity() {
    return new Mat3(1, 0, 0,
                    0, 1, 0,
                    0, 0, 1);
  }
  
  public Mat3(float m11, float m12, float m13,
              float m21, float m22, float m23,
              float m31, float m32, float m33) {
    data[0] = m11;
    data[1] = m21;
    data[2] = m31;
    
    data[3] = m12;
    data[4] = m22;
    data[5] = m32;
    
    data[6] = m13;
    data[7] = m23;
    data[8] = m33;
  }
  
  public Mat3(Mat3 matrix) {
    System.arraycopy(matrix.data, 0, data, 0, 9);
  }
   
  // matrix can be either 3x3 or 4x4
  public void toColumnMajorArray(float[] matrix) {
    if (matrix.length == 9) {
      System.arraycopy(data, 0, matrix, 0, 9);
    } else if (matrix.length == 16) {
      matrix[15] = 1;
      for (int r = 0; r < 3; ++r) {
        for (int c = 0; c < 3; ++c) {
          matrix[4*c + r] = data[3*c + r];
        }
      }
    } else {
      throw new IllegalArgumentException("matrix must be either 3x3 or 4x4");
    }
  }
  
  // matrix can be either 3x3 or 4x4
  public void toRowMajorArray(float[] matrix) {
    int size;
    if (matrix.length == 9) {
      size = 3;
    } else if (matrix.length == 16) {
      size = 4;
      matrix[15] = 1;
    } else {
      throw new IllegalArgumentException("matrix must be either 3x3 or 4x4");
    }
    
    for (int r = 0; r < 3; ++r) {
      for (int c = 0; c < 3; ++c) {
        matrix[size*r + c] = data[3*c + r];
      }
    }
  }
  
  // Returns this*o
  public Mat3 rightMult(Mat3 o) {
    return new Mat3(
        // first row
        data[0]*o.data[0] + data[3]*o.data[1] + data[6]*o.data[2],
        data[0]*o.data[3] + data[3]*o.data[4] + data[6]*o.data[5],
        data[0]*o.data[6] + data[3]*o.data[7] + data[6]*o.data[8],
        
        // second row
        data[1]*o.data[0] + data[4]*o.data[1] + data[7]*o.data[2],
        data[1]*o.data[3] + data[4]*o.data[4] + data[7]*o.data[5],
        data[1]*o.data[6] + data[4]*o.data[7] + data[7]*o.data[8],
        
        // third row
        data[2]*o.data[0] + data[5]*o.data[1] + data[8]*o.data[2],
        data[2]*o.data[3] + data[5]*o.data[4] + data[8]*o.data[5],
        data[2]*o.data[6] + data[5]*o.data[7] + data[8]*o.data[8]);
  }
  
  public float at(int row, int col) {
    Check.Gt(row, -1); Check.Lt(row, 3);
    Check.Gt(col, -1); Check.Lt(col, 3);
    return data[3*col+row];
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Mat3)) {
      return false;
    }
    final Mat3 m = (Mat3)o;
    for (int i = 0; i < 9; ++i) {
      if (!MathUtils.floatEq(data[i], m.data[i])) {
        return false;
      }
    }
    return true;
  }
  
  @Override
  public String toString() {
    StringBuffer str = new StringBuffer("");
    for (int r = 0; r < 3; ++r) {
      str.append("|");
      for (int c = 0; c < 3; ++c) {
        str.append(decimalFormat.format(data[3*c + r]) + " ");
      }
      str.append("|\n");
    }
    return str.toString();
  }
}
