package cn.zr.contentProviderPreference;

//Outgoing transactions from this process must be FLAG_ONEWAY
public class MyPreferenceProvider extends RemotePreferenceProvider {
    public MyPreferenceProvider() {

        super("cn.zr.preferences", new String[]{"main_prefs"});
        /*super("cn.zr.preferences", new RemotePreferenceFile[] {
                new RemotePreferenceFile("main_prefs", true)
        });*/
    }
}
