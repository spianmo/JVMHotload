import hotload.PluginManager;

import java.io.*;
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

        while (!"exit".equals(cmd)) {
            String[] argv = cmd.split(" ");

            if (argv.length <= 1) throw new RuntimeException("参数错误");
            String pluginPackageName = argv[1];
            if (pluginPackageName == null) throw new RuntimeException("请输入加载插件包名");

            if (cmd.startsWith("exec")) {
                if (argv.length <= 2) throw new RuntimeException("参数错误");
                String functionName = argv[2];
                if (functionName == null) throw new RuntimeException("请输入函数名");
                List<String> result = (List<String>) pluginManager.exec(pluginPackageName, functionName, new LinkedHashMap() {{
                    put("testKey", "testValue");
                }});
                System.out.println(Arrays.toString(result.toArray()));
            }
            if (cmd.startsWith("res")) {
                if (argv.length <= 2) throw new RuntimeException("参数错误");
                String resourceName = argv[2];
                System.out.println(readStream(pluginManager.getResource(pluginPackageName, resourceName)));
            }
            if (cmd.startsWith("load")) {
                pluginManager.load(pluginPackageName);
            }
            if (cmd.startsWith("unload")) {
                pluginManager.unload(pluginPackageName);
            }
            cmd = br.readLine();
        }
    }

    /**
     * 读取 InputStream 到 String字符串中
     */
    private static String readStream(InputStream in) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            String content = baos.toString();
            in.close();
            baos.close();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}