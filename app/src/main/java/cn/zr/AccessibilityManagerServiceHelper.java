package cn.zr;

import android.accessibilityservice.AccessibilityServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityManagerServiceHelper {


    public final List<AccessibilityServiceInfo> prepareHandle(List<AccessibilityServiceInfo> services) {
        if (services == null || services.isEmpty() || !(services instanceof ArrayList)) {
            return services;
        }

        List<AccessibilityServiceInfo> infos = deepCopy(services);
        for (int i = 0; i < services.size(); i++) {
            AccessibilityServiceInfo info = services.get(i);
            if (info.packageNames != null && info.packageNames.length > 1) {
                if (info.packageNames[0].equals("zzr") && info.packageNames[1].equals("com.sdu.didi.gui") && info.getSettingsActivityName().equals("cn.zr.MainActivity")) {
                    info.packageNames[1] = "kankan";
                    return infos;
                }
            }
        }
        return services;
    }

    private final List<AccessibilityServiceInfo> deepCopy(List<AccessibilityServiceInfo> src) {
        if (src == null || src.isEmpty()) {
            return src;
        }
        List<AccessibilityServiceInfo> students = new ArrayList<>();
        for (int i = 0; i < src.size(); i++) {
            students.add((AccessibilityServiceInfo) copy(src.get(i)));
        }
        return students;
    }


    private final <T> T copy(android.os.Parcelable input) {
        android.os.Parcel parcel = null;
        try {
            parcel = android.os.Parcel.obtain();
            parcel.writeParcelable(input, 0);
            parcel.setDataPosition(0);
            return parcel.readParcelable(input.getClass().getClassLoader());
        } finally {
            parcel.recycle();
        }
    }
}
