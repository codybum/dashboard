package io.cresco.dashboard;


import io.cresco.dashboard.controllers.*;
import io.cresco.dashboard.filters.AuthenticationFilter;


import io.cresco.library.agent.AgentService;
import io.cresco.library.messaging.MsgEvent;
import io.cresco.library.plugin.Executor;
import io.cresco.library.plugin.PluginBuilder;
import io.cresco.library.plugin.PluginService;


import io.cresco.library.utilities.CLogger;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.*;
import org.osgi.service.http.HttpService;

import java.io.File;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

@Component(
        service = { PluginService.class },
        scope=ServiceScope.PROTOTYPE,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        servicefactory = true,
        property="dashboard=core",
        reference= { @Reference(name="io.cresco.library.agent.AgentService", service=AgentService.class)}
)

public class Plugin implements PluginService {

    //public PluginBuilder getPluginBuilder() { return  pluginBuilder; }

    public BundleContext context;
    public static PluginBuilder pluginBuilder;
    private Executor executor;
    private CLogger logger;
    //private HttpService server;
    public String repoPath = null;
    private ConfigurationAdmin configurationAdmin;
    private Map<String,Object> map;

    @Activate
    void activate(BundleContext context, Map<String,Object> map) {

        this.context = context;
        this.map = map;

    }

    @Reference
    protected void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    protected void unsetConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = null;
    }


    @Modified
    void modified(BundleContext context, Map<String,Object> map) {
        System.out.println("Modified Config Map PluginID:" + (String) map.get("pluginID"));
    }

    @Override
    public boolean inMsg(MsgEvent incoming) {
        pluginBuilder.msgIn(incoming);
        return true;
    }

    private Dictionary<String, String> getJerseyServletParams() {
        Dictionary<String, String> jerseyServletParams = new Hashtable<>();
        jerseyServletParams.put("javax.ws.rs.Application", Plugin.class.getName());
        return jerseyServletParams;
    }

    private String getRepoPath() {
        String path = null;
        try {
            //todo create seperate director for repo
            path = new File(Plugin.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();

        } catch(Exception ex) {
            //logger.error(ex.getMessage());
            ex.printStackTrace();
        }
        return path;
    }

    @Override
    public boolean isStarted() {
        try {
            //this will be called twice due to JAX-RS-Connector
            if(pluginBuilder == null) {
                pluginBuilder = new PluginBuilder(this.getClass().getName(), context, map);
                this.logger = pluginBuilder.getLogger(Plugin.class.getName(), CLogger.Level.Info);
                this.executor = new PluginExecutor(pluginBuilder);
                pluginBuilder.setExecutor(executor);

                while (!pluginBuilder.getAgentService().getAgentState().isActive()) {
                    logger.info("Plugin " + pluginBuilder.getPluginID() + " waiting on Agent Init");
                    //System.out.println("Plugin " + pluginBuilder.getPluginID() + " waiting on Agent Init");
                    Thread.sleep(1000);
                }

                pluginBuilder.setIsActive(true);

            }
            return true;

        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}