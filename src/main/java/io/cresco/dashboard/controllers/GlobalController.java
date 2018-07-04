package io.cresco.dashboard.controllers;


import io.cresco.library.messaging.MsgEvent;
import io.cresco.library.plugin.PluginBuilder;
import io.cresco.library.utilities.CLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;

@Path("global")
public class GlobalController {
    private static PluginBuilder plugin = null;
    private static CLogger logger = null;

    public static void connectPlugin(PluginBuilder inPlugin) {
        plugin = inPlugin;
        logger = plugin.getLogger(GlobalController.class.getName(),CLogger.Level.Info);
        //logger = new CLogger(RegionsController.class, plugin.getMsgOutQueue(), plugin.getRegion(),
        //        plugin.getAgent(), plugin.getPluginID(), CLogger.Level.Trace);
    }

    @GET
    @Path("resources")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resources() {
        logger.trace("Call to resources()");
        try {
            if (plugin == null)
                return Response.ok("{\"regions\":[]}", MediaType.APPLICATION_JSON_TYPE).build();
            MsgEvent request = new MsgEvent(MsgEvent.Type.EXEC, plugin.getRegion(), plugin.getAgent(),
                    plugin.getPluginID(), "Region List Request");
            request.setParam("src_region", plugin.getRegion());
            request.setParam("src_agent", plugin.getAgent());
            request.setParam("src_plugin", plugin.getPluginID());
            request.setParam("dst_region", plugin.getRegion());
            request.setParam("dst_agent", plugin.getAgent());
            request.setParam("dst_plugin", "plugin/0");
            request.setParam("is_regional", Boolean.TRUE.toString());
            request.setParam("is_global", Boolean.TRUE.toString());
            request.setParam("globalcmd", Boolean.TRUE.toString());
            request.setParam("action", "resourceinfo");
            MsgEvent response = plugin.sendRPC(request);
            if (response == null)
                return Response.ok("{\"error\":\"Cresco rpc response was null\"}",
                        MediaType.APPLICATION_JSON_TYPE).build();
            String regions = "[]";
            if (response.getParam("resourceinfo") != null)
                regions = response.getCompressedParam("resourceinfo");
            return Response.ok(regions, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            if (plugin != null)
                logger.error("resources() : {}", sw.toString());
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}",
                    MediaType.APPLICATION_JSON_TYPE).build();
        }
    }


    /*
    String dbexport = plugin.getGDB().gdb.getDBExportAll();
            logger.error(plugin.getGDB().gdb.stringUncompress(dbexport));
     */

}
