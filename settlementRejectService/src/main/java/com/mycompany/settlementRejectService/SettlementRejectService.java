package com.mycompany.settlementRejectService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class SettlementRejectService {

    @POST
    @Path("/name")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMsg(InputStream input) {
        StringBuilder response = new StringBuilder();
        try {

            BufferedReader readInput = new BufferedReader(new InputStreamReader(input));
            String line = null;
            while ((line = readInput.readLine()) != null) {
                response.append(line);
            }

        } catch (IOException ex) {
            Logger.getLogger(SettlementRejectService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(200).build();
    }

}
