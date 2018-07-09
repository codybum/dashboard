package io.cresco.dashboard;


import io.cresco.dashboard.controllers.*;
import io.cresco.dashboard.filters.AuthenticationFilter;


import io.cresco.library.agent.AgentService;
import io.cresco.library.messaging.MsgEvent;
import io.cresco.library.plugin.Executor;
import io.cresco.library.plugin.PluginBuilder;
import io.cresco.library.plugin.PluginService;



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
        reference= { @Reference(name="io.cresco.library.agent.AgentService", service=AgentService.class),
                @Reference(name="com.eclipsesource.jaxrs.publisher.internal.JAXRSConnector") }
)

public class Plugin implements PluginService {

    public BundleContext context;
    private PluginBuilder pluginBuilder;
    private Executor executor;
    //private CLogger logger;
    //private HttpService server;
    public String repoPath = null;
    private ConfigurationAdmin configurationAdmin;


    @Activate
    void activate(BundleContext context, Map<String,Object> map) {


        this.context = context;

        System.out.println("Started PluginID:" + (String) map.get("pluginID"));

        try {
            pluginBuilder = new PluginBuilder(this.getClass().getName(), context, map);
            //this.logger = pluginBuilder.getLogger(Plugin.class.getName(),CLogger.Level.Info);
            this.executor = new PluginExecutor(pluginBuilder);
            pluginBuilder.setExecutor(executor);

            while(!pluginBuilder.getAgentService().getAgentState().isActive()) {
                //logger.info("Plugin " + pluginBuilder.getPluginID() + " waiting on Agent Init");
                System.out.println("Plugin " + pluginBuilder.getPluginID() + " waiting on Agent Init");
                Thread.sleep(1000);
            }

            AuthenticationFilter.connectPlugin(pluginBuilder);
            RootController.connectPlugin(pluginBuilder);
            AlertsController.connectPlugin(pluginBuilder);
            AgentsController.connectPlugin(pluginBuilder);
            PluginsController.connectPlugin(pluginBuilder);
            RegionsController.connectPlugin(pluginBuilder);
            GlobalController.connectPlugin(pluginBuilder);
            ApplicationsController.connectPlugin(pluginBuilder);


            /*
            try {
                repoPath = getRepoPath();
                server = startServer("http://[::]:" + pluginBuilder.getConfig().getStringParam("port", "3445") + "/");
            } catch(Exception ex) {
                ex.printStackTrace();
                server = startServer("http://0.0.0.0:" + pluginBuilder.getConfig().getStringParam("port", "3445") + "/");
                //logger.error("failed : " + ex.getMessage());
            }
            */

            //set plugin active
            pluginBuilder.setIsActive(true);


        } catch(Exception ex) {
            ex.printStackTrace();
        }

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


}