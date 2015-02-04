package net.fhtagn.orientation.orientation.math;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class IO {
    private static final DecimalFormat decimalFormat = new DecimalFormat(" * 0.0000;-#");

    // Row-major matrix to string
    public static String matrixToString(float[] matrix) {
        int n;
        if (matrix.length == 16) {
            n = 4;
        } else if (matrix.length == 9) {
            n = 3;
        } else {
            return "Unhandled matrix length : " + matrix.length;
        }

        StringBuffer str = new StringBuffer("");
        for (int i = 0; i < n; ++i) {
            str.append("|");
            for (int j = 0; j < n; ++j) {
                str.append(decimalFormat.format(matrix[i * n + j]) + " ");
            }
            str.append("|\n");
        }
        return str.toString();
    }

    public static String vectorToString(float[] vector) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < vector.length; ++i) {
            str.append(decimalFormat.format(vector[i]));
            if (i < vector.length - 1) {
                str.append(", ");
            }
        }
        str.append("]");
        return str.toString();
    }

    // Returns a row-major representation of the given row-major matrix
    public static JSONObject matrixToJSON(float[] matrix) throws JSONException {
        int n;
        if (matrix.length == 16) {
            n = 4;
        } else if (matrix.length == 9) {
            n = 3;
        } else {
            throw new IllegalArgumentException("Unhandled matrix length : "
                    + matrix.length);
        }
        JSONObject obj = new JSONObject();
        obj.put("n", n);
        JSONArray mat = new JSONArray();
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                mat.put(matrix[i * n + j]);
            }
        }
        obj.put("values", mat);
        return obj;
    }

    public static JSONObject matrixToJSON(Mat3 matrix) throws JSONException {
        float[] _m = new float[9];
        matrix.toRowMajorArray(_m);
        return matrixToJSON(_m);
    }
}
