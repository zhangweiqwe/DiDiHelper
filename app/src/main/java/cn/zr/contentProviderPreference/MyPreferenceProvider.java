package cn.zr.contentProviderPreference;

public class MyPreferenceProvider extends RemotePreferenceProvider {
    public MyPreferenceProvider() {
        super("cn.zr.preferences", new String[]{"main_preferences"});
    }
}
