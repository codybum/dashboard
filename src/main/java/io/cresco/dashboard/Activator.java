package io.cresco.dashboard;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.util.Dictionary;
import java.util.Hashtable;

public class Activator implements BundleActivator {


    public void start(BundleContext context) throws Exception {

        //set root for jersey
        //setHttpConfig(context);
    }

    public void stop(BundleContext context) throws Exception {

    }


    private void setHttpConfig(BundleContext context) {
        try {

            ConfigurationAdmin configurationAdmin;

            ServiceReference configurationAdminReference = null;

            configurationAdminReference = context.getServiceReference(ConfigurationAdmin.class.getName());

            if (configurationAdminReference != null) {

                boolean assign = configurationAdminReference.isAssignableTo(context.getBundle(), ConfigurationAdmin.class.getName());

                if (assign) {
                    configurationAdmin = (ConfigurationAdmin) context.getService(configurationAdminReference);

                    Configuration configuration = configurationAdmin.getConfiguration("com.eclipsesource.jaxrs.connector", null);
                    Dictionary props = configuration.getProperties();
                    if (props == null) {
                        props = new Hashtable();
                    }
                    props.put("root", "/");
                    configuration.update(props);


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