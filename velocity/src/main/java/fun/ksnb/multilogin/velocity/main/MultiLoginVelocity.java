package fun.ksnb.multilogin.velocity.main;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fun.ksnb.multilogin.velocity.impl.VelocityServer;
import lombok.Getter;
import moe.caa.multilogin.api.MultiLoginAPI;
import moe.caa.multilogin.api.plugin.IPlugin;
import moe.caa.multilogin.loader.main.PluginLoader;
import moe.caa.multilogin.logger.MultiLoginLogger;
import moe.caa.multilogin.logger.bridges.Slf4JLoggerBridge;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MultiLoginVelocity implements IPlugin {
    @Getter
    private final ProxyServer server;

    @Getter
    private final Path dataDirectory;

    @Getter
    private final VelocityServer runServer;

    @Getter
    private final MultiLoginAPI multiLoginAPI;
    private final PluginLoader pluginLoader;


    @Inject
    public MultiLoginVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) throws Throwable {
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.runServer = new VelocityServer(server);

        // 初始化 Logger
        final Slf4JLoggerBridge slf4JLoggerBridge = new Slf4JLoggerBridge(logger);
        moe.caa.multilogin.logger.Logger.LoggerProvider.setLogger(slf4JLoggerBridge);
        final MultiLoginLogger instance = MultiLoginLogger.getInstance();
        instance.setLoggerBridge(slf4JLoggerBridge);
        instance.setLoggerFolder(dataDirectory.toFile());
        if (instance.canInit()) {
            instance.init();
        }

        // 加载插件依赖
        pluginLoader = new PluginLoader(new File(dataDirectory.toFile(), "libraries"),
                new File(dataDirectory.toFile(), "temp"));
        multiLoginAPI = pluginLoader.load(this);
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) throws Throwable {
        multiLoginAPI.init();
//        multiLoginAPI.init
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) throws IOException {
//        multiLoginAPI.disable
        pluginLoader.close();
    }

    @Override
    public File getDataFolder() {
        return dataDirectory.toFile();
    }

    @Override
    public String getPluginVersion() {
        return getServer().getPluginManager().getPlugin("multilogin").get()
                .getDescription().getVersion().get();
    }
}
