package io.cresco.dashboard.controllers;


import io.cresco.dashboard.Plugin;
import io.cresco.library.plugin.PluginService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


@Path("/static")
public class StaticContent {


    /*
    server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Plugin.class.getClassLoader(), "includes/"), "/includes");
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Plugin.class.getClassLoader(), "css/"), "/css");
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Plugin.class.getClassLoader(), "img/"), "/img");
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Plugin.class.getClassLoader(), "js/"), "/js");
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Plugin.class.getClassLoader(), "vendors/"), "/vendors");

     */

    @GET
    @Path("{subResources:.*}")
    //@Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.TEXT_HTML)
    public Response getfile(@PathParam("subResources") String subResources)
    {
        System.out.println("Requesting file: " + subResources);

        subResources = "/" + subResources;

        InputStream in = null;
        try{



            in = getClass().getResourceAsStream(subResources);

            //in = Plugin.class.getResourceAsStream(subResources);

            if(in == null)
            {

                System.out.println("NOT FOUND!");
                //in = getClass().getResourceAsStream("/404.html");
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
            //in = getClass().getResourceAsStream("/500.html");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }

        if(subResources.endsWith(".jar"))
        {
            return Response.ok(in, "application/java-archive").build();
        }
        else
        {
            return Response.ok(in, MediaType.TEXT_HTML).build();
            /*
            return Response.ok(in, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + "somefile" + "\"" ) //optional
                    .build();
                    */
        }

    }


}