import hotload.PluginManager;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Administrator
 */
public class Main {

    private static final String PLUGIN_DIR = "e:/IdeaProjects/HotLoad/plugins/";

    public static void watchPluginDir(PluginManager pluginManager) throws InterruptedException, IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();

        Paths.get(PLUGIN_DIR).register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        while (true) {
            WatchKey key=watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(
                        "Event kind:" + event.kind()
                                + ". Plugin affected: " + event.context() + ".");

                if (!event.context().toString().endsWith(".jar")) continue;
                if (event.kind().name() == "ENTRY_CREATE") {
                    pluginManager.load(event.context().toString().substring(0, event.context().toString().indexOf(".jar")));
                }
                if (event.kind().name() == "ENTRY_MODIFY") {
                    pluginManager.reload(event.context().toString().substring(0, event.context().toString().indexOf(".jar")));
                }
                if (event.kind().name() == "ENTRY_DELETE") {
                    pluginManager.unload(event.context().toString().substring(0, event.context().toString().indexOf(".jar")));
                }
            }
            key.reset();
        }
    }

    private static void watchCmdLine(PluginManager pluginManager) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String cmd = br.readLine();

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

    public static void main(String[] args) {
        PluginManager pluginManager = PluginManager.getInstance(PLUGIN_DIR);
        new Thread(()->{
            try {
                watchPluginDir(pluginManager);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(()->{
            try {
                watchCmdLine(pluginManager);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
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