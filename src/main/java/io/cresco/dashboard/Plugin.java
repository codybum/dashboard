package io.cresco.dashboard;


import io.cresco.dashboard.controllers.*;
import io.cresco.dashboard.filters.AuthenticationFilter;
import io.cresco.dashboard.filters.NotFoundExceptionHandler;


import io.cresco.library.agent.AgentService;
import io.cresco.library.messaging.MsgEvent;
import io.cresco.library.plugin.Executor;
import io.cresco.library.plugin.PluginBuilder;
import io.cresco.library.plugin.PluginService;



import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
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
        reference=@Reference(name="io.cresco.library.agent.AgentService", service=AgentService.class)
)

public class Plugin implements PluginService {

    public BundleContext context;
    private PluginBuilder pluginBuilder;
    private Executor executor;
    //private CLogger logger;
    private HttpService server;
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

    private HttpService startServer(String baseURI) {
        final OutputStream nullOutputStream = new OutputStream() { @Override public void write(int b) { } };
        Logger.getLogger("").addHandler(new ConsoleHandler() {{ setOutputStream(nullOutputStream); }});
        /*
        final ResourceConfig rc = new ResourceConfig()
                .register(AuthenticationFilter.class)
                .register(NotFoundExceptionHandler.class)
                .register(RootController.class)
                .register(AlertsController.class)
                .register(AgentsController.class)
                .register(PluginsController.class)
                .register(RegionsController.class)
                .register(GlobalController.class)
                .register(ApplicationsController.class)
                .register(MultiPartFeature.class);
        ;
*/
        AuthenticationFilter.connectPlugin(pluginBuilder);
        RootController.connectPlugin(pluginBuilder);
        AlertsController.connectPlugin(pluginBuilder);
        AgentsController.connectPlugin(pluginBuilder);
        PluginsController.connectPlugin(pluginBuilder);
        RegionsController.connectPlugin(pluginBuilder);
        GlobalController.connectPlugin(pluginBuilder);
        ApplicationsController.connectPlugin(pluginBuilder);



        ClassLoader myClassLoader = getClass().getClassLoader();
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(myClassLoader);
            //server.registerServlet("/jersey-http-service", new ServletContainer(), getJerseyServletParams(), null);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }

        /*
        HttpServer server =  GrizzlyHttpServerFactory.createHttpServer(URI.create(baseURI), rc);
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Plugin.class.getClassLoader(), "includes/"), "/includes");
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Plugin.class.getClassLoader(), "css/"), "/css");
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Plugin.class.getClassLoader(), "img/"), "/img");
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Plugin.class.getClassLoader(), "js/"), "/js");
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Plugin.class.getClassLoader(), "vendors/"), "/vendors");
        //allow downloads in repo dir
        if(repoPath != null) {
            StaticHttpHandler staticHttpHandler = new StaticHttpHandler(repoPath);
            server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/repository");
        }
        */

        return server;
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