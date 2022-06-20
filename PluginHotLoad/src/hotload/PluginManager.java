package hotload;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */
public class PluginManager {
    private final Map<String, PluginClassLoader> pluginMap = new HashMap<String, PluginClassLoader>();

    private static String BASE_PATH = "";
    private static PluginManager pluginManager;

    private PluginManager() {

    }

    private PluginManager(String basePath) {
        BASE_PATH = basePath;
    }

    public static PluginManager getInstance(String basePath) {
        if (pluginManager == null) {
            pluginManager = new PluginManager(basePath);
        }
        return pluginManager;
    }

    private void addLoader(String pluginName, PluginClassLoader loader) {
        this.pluginMap.put(pluginName, loader);
    }

    private PluginClassLoader getLoader(String pluginName) {
        return this.pluginMap.get(pluginName);
    }

    public void load(String pluginPackageName) {
        this.pluginMap.remove(pluginPackageName);
        PluginClassLoader loader = new PluginClassLoader();
        String pluginUrl = "jar:file:/" + BASE_PATH + pluginPackageName + ".jar!/";
        URL url = null;
        try {
            url = new URL(pluginUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        loader.addURLFile(url);
        addLoader(pluginPackageName, loader);
        onPluginLoad(pluginPackageName);
        System.out.println("load " + pluginPackageName + "  success");
    }

    public void onPluginLoad(String pluginPackageName) {
        try {
            Class<?> forName = Class.forName(pluginPackageName + ".Main", true, getLoader(pluginPackageName));
            Plugin ins = (Plugin) (forName.newInstance());
            ins.main();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object exec(String pluginPackageName, String function, Map argv) {
        if (getLoader(pluginPackageName) == null) this.load(pluginPackageName);
        try {
            Class<?> forName = Class.forName(pluginPackageName + ".Main", true, getLoader(pluginPackageName));
            Object ins = (forName.newInstance());
            Method method;
            try {
                method = forName.getMethod(function, Map.class);
            }catch (NoSuchMethodException noSuchMethodException) {
                method = null;
            }
            if (method == null) throw new RuntimeException("方法: " + function + " 不存在");
            return method.invoke(ins, argv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void unload(String pluginName) {
        this.pluginMap.get(pluginName).unloadJarFiles();
        this.pluginMap.remove(pluginName);
    }

    public void reload(String pluginName) {
        this.unload(pluginName);
        this.load(pluginName);
    }
}