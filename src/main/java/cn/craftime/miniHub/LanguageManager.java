package cn.craftime.miniHub;

import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.yaml.snakeyaml.Yaml;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class LanguageManager {
    private final Path dataDirectory;
    private final String language;
    private final Path langDirectory;
    private Map<String, Object> messages;
    
    public LanguageManager(Path dataDirectory, String language) {
        this.dataDirectory = dataDirectory;
        this.language = language;
        this.langDirectory = dataDirectory.resolve("lang");
        this.messages = new HashMap<>();
    }
    
    public boolean loadLanguage() {
        try {
            // 创建语言目录
            if (!Files.exists(langDirectory)) {
                Files.createDirectories(langDirectory);
            }
            
            // 检查并复制缺失的语言文件
            checkAndCopyLanguageFiles();
            
            Path languageFile = langDirectory.resolve(language + ".yml");
            
            // 如果指定的语言文件不存在，使用默认语言
            if (!Files.exists(languageFile)) {
                // 尝试使用默认语言文件
                Path defaultLanguageFile = langDirectory.resolve("zh_cn.yml");
                if (Files.exists(defaultLanguageFile)) {
                    languageFile = defaultLanguageFile;
                } else {
                    // 如果默认语言文件也不存在，使用内置的默认消息
                    loadDefaultMessages();
                    return true;
                }
            }
            
            // 加载语言文件
            Yaml yaml = new Yaml();
            try (InputStream input = Files.newInputStream(languageFile)) {
                messages = yaml.load(input);
                if (messages == null) {
                    messages = new HashMap<>();
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 加载失败时使用内置默认消息
            loadDefaultMessages();
            return false;
        }
    }
    
    private void checkAndCopyLanguageFiles() {
        try {
            // 获取jar包中所有的语言文件
            List<String> resourceLangFiles = getResourceLanguageFiles();
            
            for (String resourceFile : resourceLangFiles) {
                String fileName = resourceFile.substring(resourceFile.lastIndexOf('/') + 1);
                Path targetFile = langDirectory.resolve(fileName);
                
                // 如果目标文件不存在，从资源中复制
                if (!Files.exists(targetFile)) {
                    try (InputStream input = getClass().getResourceAsStream("/" + resourceFile)) {
                        if (input != null) {
                            Files.copy(input, targetFile, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("已复制语言文件: " + fileName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private List<String> getResourceLanguageFiles() {
        List<String> langFiles = new ArrayList<>();
        try {
            // 获取类加载器
            ClassLoader classLoader = getClass().getClassLoader();
            
            // 尝试从jar包中获取资源
            Enumeration<URL> resources = classLoader.getResources("lang");
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("jar")) {
                    // 在jar包中
                    String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                    try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8.name()))) {
                        jar.stream().forEach(entry -> {
                            String name = entry.getName();
                            if (name.startsWith("lang/") && name.endsWith(".yml") && !entry.isDirectory()) {
                                langFiles.add(name);
                            }
                        });
                    }
                } else {
                    // 在文件系统中（开发环境）
                    Path langDir = Path.of(resource.toURI());
                    if (Files.exists(langDir)) {
                        Files.list(langDir)
                            .filter(path -> path.toString().endsWith(".yml"))
                            .forEach(path -> langFiles.add("lang/" + path.getFileName().toString()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 如果找不到资源文件，添加默认的语言文件
        if (langFiles.isEmpty()) {
            langFiles.add("lang/zh_cn.yml");
        }
        
        return langFiles;
    }
    
    private void loadDefaultMessages() {
        messages = new HashMap<>();
        messages.put("plugin.enabled", "§aMiniHub 插件已启用");
        messages.put("plugin.disabled", "§cMiniHub 插件已禁用");
        messages.put("command.player_only", "§c只有玩家才能执行此命令");
        messages.put("command.server_not_found", "§c服务器未找到");
        messages.put("command.server_offline", "§c目标服务器当前不可用");
        messages.put("command.teleporting", "§a正在将您传送到 §e{server} §a服务器...");
        messages.put("command.no_permission", "§c您没有权限使用此命令");
    }
    
    public Component getMessage(String key) {
        String message = getMessageString(key);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
    
    public Component getMessage(String key, Map<String, String> replacements) {
        String message = getMessageString(key, replacements);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
    
    private String getMessageString(String key) {
        Object message = messages.get(key);
        if (message != null) {
            return message.toString();
        }
        
        // 如果找不到消息，返回默认值
        switch (key) {
            case "plugin.enabled":
                return "§aMiniHub plugin enabled";
            case "plugin.disabled":
                return "§cMiniHub plugin disabled";
            case "command.player_only":
                return "§cOnly players can execute this command";
            case "command.server_not_found":
                return "§cServer not found";
            case "command.server_offline":
                return "§cTarget server is currently unavailable";
            case "command.teleporting":
                return "§aTeleporting you to §e{server} §aserver...";
            case "command.no_permission":
                return "§cYou don't have permission to use this command";
            default:
                return "§cMessage not found: " + key;
        }
    }
    
    private String getMessageString(String key, Map<String, String> replacements) {
        String message = getMessageString(key);
        
        if (replacements != null) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        
        return message;
    }
}