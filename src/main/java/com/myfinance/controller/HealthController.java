package com.myfinance.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/health")
public class HealthController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "ok");
        status.put("message", "My Finance backend is running on Java EE 6");
        return Response.ok(status).build();
    }
}
