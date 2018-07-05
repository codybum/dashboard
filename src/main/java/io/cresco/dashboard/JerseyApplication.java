package io.cresco.dashboard;

import io.cresco.dashboard.controllers.*;
import io.cresco.dashboard.filters.NotFoundExceptionHandler;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class JerseyApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> result = new HashSet<Class<?>>();
        result.add(NotFoundExceptionHandler.class);
        result.add(RootController.class);
        result.add(AlertsController.class);
        result.add(AgentsController.class);
        result.add(PluginsController.class);
        result.add(RegionsController.class);
        result.add(GlobalController.class);
        result.add(ApplicationsController.class);
        result.add(MultiPartFeature.class);



        return result;
    }

}