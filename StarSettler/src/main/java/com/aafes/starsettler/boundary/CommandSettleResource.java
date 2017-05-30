package com.aafes.starsettler.boundary;

import com.aafes.starsettler.control.Settler;
import com.aafes.starsettler.entity.CommandMessage;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Path("/commandsettle")
public class CommandSettleResource {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(CommandSettleResource.class.
                    getSimpleName());

    @EJB
    private Settler settler;

    /**
     * Process a new settle request.
     *
     * @param requestXML
     * @return the request XML document with added response data
     */
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String postXml(CommandMessage commandMessage) {

        String responseMessageXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Success</Error>"
                + "</ErrorInformation>";

        try {
            LOG.info("Calling ondemand...");

            LOG.info("ProcessDate : " + commandMessage.getProcessDate() + ", SettlerType :" + commandMessage.getSettlerType());

            String response = settler.commandSettle(commandMessage);

            LOG.info("Ondemand completed...." + response);

            responseMessageXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><OnDemandInformation><Detail>" + response + "</Detail>"
                    + "</OnDemandInformation>";
        } catch (Exception e) {

            responseMessageXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>" + e.getMessage() + "</Error>"
                    + "</ErrorInformation>";
            LOG.error(e.toString());
        }

        return responseMessageXml;

    }

    /**
     * @param settler the settler to set
     */
    public void setSettler(Settler settler) {
        this.settler = settler;
    }

}
