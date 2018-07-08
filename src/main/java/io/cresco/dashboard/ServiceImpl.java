package io.cresco.dashboard;


import io.cresco.library.agent.AgentService;
import io.cresco.library.agent.AgentState;
import io.cresco.library.agent.ControllerState;
import io.cresco.library.messaging.MsgEvent;
import io.cresco.library.plugin.PluginBuilder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

import java.io.File;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


@Component(
        immediate = true,
        //reference=@Reference(name="ConfigurationAdmin", service=ConfigurationAdmin.class)
        reference={ @Reference(name="ConfigurationAdmin", service=ConfigurationAdmin.class) , @Reference(name="LogService", service=LogService.class) }
)


/*
@Component(
        //name = "cody",
        service = { AgentService.class },
        //scope=ServiceScope.PROTOTYPE,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        servicefactory = true,
        reference=@Reference(name="io.cresco.library.agent.LoaderService", service=LoaderService.class)
)
*/

public class ServiceImpl  {

    private ServiceRegistration registration;

    public ServiceImpl() {

    }

    @Activate
    //void activate(BundleContext context, Map<String,Object> map) {
    void activate(BundleContext context) {


    try {

        /*
        configureHttp(context);


        ServiceReference sRef = context.getServiceReference(HttpService.class.getName());
        if (sRef != null)
        {
            HttpService service = (HttpService) context.getService(sRef);
            service.registerServlet("/hello", new HelloWorld(), null, null);
        }

        Hashtable props = new Hashtable();
        props.put("osgi.http.whiteboard.servlet.pattern", "/hello");
        props.put("servlet.init.message", "Hello World!");

        this.registration = context.registerService(Servlet.class.getName(), new HelloWorld(), props);
        */
        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }

    @Modified
    void modified(BundleContext context, Map<String,Object> map) {
        System.out.println("Modified Config Map PluginID:" + (String) map.get("pluginID"));
        //this.registration.unregister();
    }

    public void configureHttp(BundleContext context) {

        try {
            ConfigurationAdmin confAdmin = null;

            ServiceReference configurationAdminReference = null;

            configurationAdminReference = context.getServiceReference(ConfigurationAdmin.class.getName());

            boolean isDone = false;
            while(!isDone)

                if (configurationAdminReference != null) {

                    boolean assign = configurationAdminReference.isAssignableTo(context.getBundle(), ConfigurationAdmin.class.getName());

                    if (assign) {
                        confAdmin = (ConfigurationAdmin) context.getService(configurationAdminReference);

                        Configuration configuration = confAdmin.createFactoryConfiguration("org.apache.felix.http", null);
                        Dictionary properties = new Hashtable();

                        properties.put("org.apache.felix.http.debug","true");
                        //properties.put("org.osgi.service.http.port", "8000:");
                        properties.put("org.osgi.service.http.port","9081");
                        properties.put("org.apache.felix.http.enable","true");
                        properties.put("org.apache.felix.http.jettyEnabled","true");
                        properties.put("felix.log.level","99");
                        properties.put("org.apache.felix.http.whiteboardEnabled","true");
                        //properties.put("org.apache.felix.http.host","127.0.0.1");
                        configuration.update(properties);
                        isDone = true;

                    } else {
                        System.out.println("Could not Assign Configuration Admin!");
                    }

                } else {
                    System.out.println("Admin Does Not Exist!");
                }
        } catch(Exception ex) {
            ex.printStackTrace();
        }



    }


}