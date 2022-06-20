import hotload.PluginManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Administrator
 */
public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String cmd = br.readLine();
        PluginManager pluginManager = PluginManager.getInstance("e:/IdeaProjects/HotLoad/plugins/");

        while(!"exit".equals(cmd)){
            String[] argv = cmd.split(" ");

            if (argv.length <= 1)  throw new RuntimeException("参数错误");
            String pluginPackageName = argv[1];
            if (pluginPackageName == null) throw new RuntimeException("请输入加载插件包名");

            if(cmd.startsWith("exec")){
                if (argv.length <= 2)  throw new RuntimeException("参数错误");
                String functionName = argv[2];
                if (functionName == null) throw new RuntimeException("请输入函数名");
                List<String> result = (List<String>) pluginManager.exec(pluginPackageName, functionName, new LinkedHashMap(){{
                    put("testKey", "testValue");
                }});
                System.out.println(Arrays.toString(result.toArray()));
            }
            if(cmd.startsWith("load")){
                pluginManager.load(pluginPackageName);
            }
            if(cmd.startsWith("unload")){
                pluginManager.unload(pluginPackageName);
            }
            cmd = br.readLine();
        }
    }
}