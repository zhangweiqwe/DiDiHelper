package cn.zr;

import android.content.Context;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 常用单位转换的辅助类
 */
public class DensityUtils {
    private DensityUtils() {
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }


    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    public static void copy(ArrayList src, ArrayList dest) {
        for (int i = 0; i < src.size(); i++) {
            Object obj = src.get(i);
            if (obj instanceof List) {
                dest.add(new ArrayList());
                copy((ArrayList) obj, (ArrayList) ((List) dest).get(i));
            } else {
                dest.add(obj);
            }
        }
    }


    /*public static ArrayList deepCopy(ArrayList src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        ArrayList dest = (ArrayList) in.readObject();
        return dest;
    }*/




}
