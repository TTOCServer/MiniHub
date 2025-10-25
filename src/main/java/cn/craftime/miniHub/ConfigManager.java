package cn.craftime.miniHub;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class ConfigManager {
    private final Path dataDirectory;
    private final Path configFile;
    private Map<String, Object> config;
    
    public ConfigManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.configFile = dataDirectory.resolve("config.yml");
        this.config = new HashMap<>();
    }
    
    public boolean loadConfig() {
        try {
            // 如果配置文件不存在，从资源中复制默认配置
            if (!Files.exists(configFile)) {
                saveDefaultConfig();
            }
            
            // 加载配置文件
            Yaml yaml = new Yaml();
            try (InputStream input = Files.newInputStream(configFile)) {
                config = yaml.load(input);
                if (config == null) {
                    config = new HashMap<>();
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void saveDefaultConfig() {
        try {
            // 创建默认配置
            Map<String, Object> defaultConfig = new HashMap<>();
            defaultConfig.put("language", "zh_cn");
            
            Map<String, Object> servers = new HashMap<>();
            
            Map<String, Object> lobby = new HashMap<>();
            lobby.put("commands", Arrays.asList("hub", "lobby"));
            servers.put("lobby", lobby);
            
            Map<String, Object> survival = new HashMap<>();
            survival.put("commands", Arrays.asList("survival", "srv"));
            servers.put("survival", survival);
            
            Map<String, Object> creative = new HashMap<>();
            creative.put("commands", Arrays.asList("creative", "crea"));
            servers.put("creative", creative);
            
            defaultConfig.put("servers", servers);
            
            // 保存默认配置
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            
            Yaml yaml = new Yaml(options);
            String yamlString = yaml.dump(defaultConfig);
            
            // 添加文件头注释
            String header = "# MiniHub 插件配置文件\n\n" +
                           "# 使用的语言\n" +
                           "# 语言文件位于 plugins/MiniHub/lang/ 目录下\n\n" +
                           "# 服务器命令配置\n" +
                           "# 格式: 服务器名称 -> 命令列表\n";
            
            Files.write(configFile, (header + yamlString).getBytes());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getLanguage() {
        Object language = config.get("language");
        return language != null ? language.toString() : "zh_cn";
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getServers() {
        Object servers = config.get("servers");
        if (servers instanceof Map) {
            return (Map<String, Object>) servers;
        }
        return new HashMap<>();
    }
    
    public void saveConfig() {
        try {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            
            Yaml yaml = new Yaml(options);
            String yamlString = yaml.dump(config);
            
            Files.write(configFile, yamlString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}