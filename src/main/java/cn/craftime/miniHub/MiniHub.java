package cn.craftime.miniHub;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.io.InputStream;
import java.nio.file.Files;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Plugin(id = "minihub", name = "MiniHub", version = "1.0-SNAPSHOT", description = "简单的命令到服务器的实现。", url = "https://github.com/", authors = {"OutHimic"})
public class MiniHub {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private Map<String, List<String>> serverCommands;

    @Inject
    public MiniHub(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // 创建数据目录
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                logger.error("无法创建插件数据目录", e);
                return;
            }
        }

        // 初始化配置管理器
        configManager = new ConfigManager(dataDirectory);
        if (!configManager.loadConfig()) {
            logger.error("无法加载配置文件，插件将禁用");
            return;
        }

        // 初始化语言管理器
        languageManager = new LanguageManager(dataDirectory, configManager.getLanguage());
        if (!languageManager.loadLanguage()) {
            logger.error("无法加载语言文件，插件将禁用");
            return;
        }

        // 加载服务器命令配置
        loadServerCommands();

        // 注册命令
        registerCommands();

        logger.info("MiniHub plugin enabled");
    }

    private void loadServerCommands() {
        serverCommands = new HashMap<>();
        Map<String, Object> servers = configManager.getServers();
        
        for (Map.Entry<String, Object> entry : servers.entrySet()) {
            String serverName = entry.getKey();
            Object serverData = entry.getValue();
            
            if (serverData instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> serverMap = (Map<String, Object>) serverData;
                Object commandsObj = serverMap.get("commands");
                
                if (commandsObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> commands = (List<String>) commandsObj;
                    for (String command : commands) {
                        serverCommands.put(command.toLowerCase(), Arrays.asList(serverName, command));
                    }
                }
            }
        }
        
        logger.info("已加载 " + serverCommands.size() + " 个命令映射");
    }

    private void registerCommands() {
        for (String command : serverCommands.keySet()) {
            server.getCommandManager().register(command, new HubCommand(command));
            logger.info("注册命令: /" + command);
        }
    }

    private class HubCommand implements SimpleCommand {
        private final String command;

        public HubCommand(String command) {
            this.command = command;
        }

        @Override
        public void execute(Invocation invocation) {
            CommandSource source = invocation.source();
            String[] args = invocation.arguments();

            if (!(source instanceof Player)) {
                source.sendMessage(languageManager.getMessage("command.player_only"));
                return;
            }

            Player player = (Player) source;
            List<String> commandData = serverCommands.get(command);
            
            if (commandData == null || commandData.size() < 2) {
                player.sendMessage(languageManager.getMessage("command.server_not_found"));
                return;
            }

            String targetServerName = commandData.get(0);
            Optional<RegisteredServer> targetServer = server.getServer(targetServerName);
            
            if (!targetServer.isPresent()) {
                player.sendMessage(languageManager.getMessage("command.server_offline"));
                return;
            }

            player.createConnectionRequest(targetServer.get()).fireAndForget();
            Map<String, String> replacements = new HashMap<>();
            replacements.put("server", targetServerName);
            player.sendMessage(languageManager.getMessage("command.teleporting", replacements));
        }

        @Override
        public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        @Override
        public boolean hasPermission(Invocation invocation) {
            return true; // 所有玩家都可以使用
        }
    }
}
