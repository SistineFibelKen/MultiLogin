package moe.caa.multilogin.core.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 插件配置文件
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PluginConfig {
    private List<YggdrasilService> yggdrasilServices;
    private int servicesTimeOut;
    private boolean globalWhitelist;
    private boolean strictMode;

    private SQLBackendType sqlBackend;
    private String sqlIp;
    private int sqlPort;
    private String sqlUsername;
    private String sqlPassword;
    private String sqlDatabase;
    private String sqlTablePrefix;

    /**
     * 插件配置读取
     */
    public static PluginConfig reload(File file) throws ConfigurateException {
        PluginConfig config = new PluginConfig();
        CommentedConfigurationNode conf = HoconConfigurationLoader.builder()
                .file(file).build().load();

        // 读 Yggdrasil 列表
        final CommentedConfigurationNode services = conf.node("yggdrasilServices");
        config.yggdrasilServices = new ArrayList<>();
        for (Map.Entry<Object, CommentedConfigurationNode> entry : services.childrenMap().entrySet()) {
            config.yggdrasilServices.add(YggdrasilService.parseConfig(entry.getKey().toString(), entry.getValue()));
        }
        config.yggdrasilServices = Collections.unmodifiableList(config.yggdrasilServices);

        // 读其他配置
        config.servicesTimeOut = conf.node("servicesTimeOut").getInt(10000);
        config.globalWhitelist = conf.node("globalWhitelist").getBoolean(true);
        config.globalWhitelist = conf.node("strictMode").getBoolean(true);

        // 读数据库配置
        final CommentedConfigurationNode sql = conf.node("sql");
        config.sqlBackend = sql.node("backend").get(SQLBackendType.class, SQLBackendType.H2);
        config.sqlIp = sql.node("ip").getString("127.0.0.1");
        config.sqlPort = sql.node("port").getInt(3306);
        config.sqlUsername = sql.node("username").getString("root");
        config.sqlPassword = sql.node("password").getString("12345");
        config.sqlDatabase = sql.node("database").getString("multilogin");
        config.sqlTablePrefix = sql.node("tablePrefix").getString("multilogin");
        return config;
    }
}
