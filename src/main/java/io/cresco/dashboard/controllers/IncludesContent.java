package io.cresco.dashboard.controllers;


import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/*
@Component(service = Object.class,
        property="dashboard=cody",
        reference = @Reference(
                name="java.lang.Object",
                service=Object.class,
                target="(dashboard=root)"
        )
)
*/
@Path("cody")
public class IncludesContent {


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
    //@Produces(MediaType.TEXT_HTML)
    public Response getfile(@PathParam("subResources") String subResources)
    {
        System.out.println("Requesting file: " + subResources);

        subResources = "/includes/" + subResources;

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


            return Response.ok(in, mediaType(subResources)).build();


    }


    public String mediaType(String file) {
        try {
            int dot = file.lastIndexOf(".");
            if (dot == -1) return MediaType.TEXT_PLAIN;
            String ext = file.substring(dot + 1).toLowerCase();
            System.out.println("EXTENSION!! " + ext);
            switch (ext) {
                case "png":
                    return "image/png";
                case "gif":
                    return "image/gif";
                case "json":
                    return MediaType.APPLICATION_JSON;
                case "js":
                    return "text/javascript";
                case "css":
                    return "text/css";
                case "svg":
                    return MediaType.APPLICATION_SVG_XML;
                case "html":
                    return MediaType.TEXT_HTML;
                case "txt":
                    return MediaType.TEXT_PLAIN;
                case "jpg":
                case "jpeg":
                    return "image/jpg";
                default:
                    return MediaType.TEXT_PLAIN;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return MediaType.TEXT_PLAIN;
    }

}