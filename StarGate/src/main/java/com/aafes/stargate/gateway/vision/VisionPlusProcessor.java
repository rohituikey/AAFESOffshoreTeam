/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.vision;

import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.MQGatewayException;
//import com.aafes.stargate.gateway.vision.entity.CICSTranId;
//import com.aafes.stargate.gateway.vision.entity.CICSHandlerBean;
import com.aafes.stargate.util.DeviceType;
import com.aafes.stargate.util.ResponseType;
import com.solab.iso8583.IsoMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nguyentul
 */
//
@Stateless
public class VisionPlusProcessor {

//    private static final Logger LOG = LoggerFactory.getLogger(
//            VisionPlusProcessor.class.getSimpleName());
//    @EJB
//    private CICSHandlerBean mqhandler;
//
//    @EJB
//    private VisionPlusFormatter vpFormatter;
//
//    @EJB
//    private Configurator configurator;

    public Transaction authorize(Transaction t) {
        try {
            Thread.sleep(40000);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(VisionPlusProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return t;
    }
//        LOG.debug("Pushing data into MQ CID and AuthNumber :", t.getTraceId() + ", " + t.getAuthNumber());
//
//        String correlationId = null;
//        // Sending MQ Message.
//        try {
//            // VisionPlusFormatter vpFormatter = new VisionPlusFormatter();
//            IsoMessage isoMsg = vpFormatter.toISO8583(t);
//            LOG.debug("Request to Vision: " + isoMsg.debugString());
//            switch (t.getDeviceType()) {
////                case DeviceType.CGES:
////                    mqhandler.setTransId(CICSTranId.CGES);
////                    break;
////                case DeviceType.IVR:
////                    mqhandler.setTransId(CICSTranId.IVR);
////                    break;
////                case DeviceType.MCSS:
////                    mqhandler.setTransId(CICSTranId.MCSS);
////                    break;
////                case DeviceType.NEXCOM:
////                    mqhandler.setTransId(CICSTranId.NEXCOM);
////                    break;
////                case DeviceType.RPOS:
////                    mqhandler.setTransId(CICSTranId.RPOS);
////                    break;
////                case DeviceType.VERIPHONE:
////                    mqhandler.setTransId(CICSTranId.VERIFONE);
////                    break;
//                case DeviceType.WEB:
//                    mqhandler.setTransId(CICSTranId.INTERNET);
//                    break;
//                case DeviceType.ICS:
//                    mqhandler.setTransId(CICSTranId.INTERNET);
//                    break;
//                default:
//                    mqhandler.setTransId(CICSTranId.DEFAULT);
//            }
//            /**
//             * Uncomment below line for actual vision call
//             */
//            t.setRequestAuthDateTime(this.getSystemDateTime());
//            correlationId = mqhandler.sendMessage(isoMsg.writeData());
//            /**
//             * comment below line for actual vision call
//             */
//            //correlationId = "123dummy";
//
//            // Reading MQ Message.ii
//            getResponse(correlationId, t);
//        } catch (IOException ioex) {
//            correlationId = null;
//
//            t.setResponseType(ResponseType.DECLINED);
//            t.setReasonCode("ERR");
//            t.setDescriptionField("ERR");
//            LOG.error(com.aafes.stargate.gateway.vision.Common.
//                    convertStackTraceToString(ioex));
//        } catch (GatewayException cex) {
//            correlationId = null;
//
//            t.setResponseType(ResponseType.DECLINED);
//            t.setReasonCode("ERR");
//            t.setDescriptionField("ERR");
//            LOG.error(com.aafes.stargate.gateway.vision.Common.
//                    convertStackTraceToString(cex));
//        } catch (MQGatewayException cex) {
//            correlationId = null;
//            t.setResponseType(ResponseType.DECLINED);
//            t.setReasonCode(configurator.get("MQ_SERVICE_ERROR"));
//            t.setDescriptionField("MQ_SERVICE_ERROR");
//            LOG.error(com.aafes.stargate.gateway.vision.Common.
//                    convertStackTraceToString(cex));
//        } catch (Exception e) {
//
//            if (e.getMessage().equalsIgnoreCase("unable to contact mq startup.")
//                    || e.getMessage().equalsIgnoreCase("WFLyeE0042: failed to construct component instance")) {
//                t.setResponseType(ResponseType.DECLINED);
//                t.setReasonCode(configurator.get("MQ_SERVICE_ERROR"));
//                t.setDescriptionField("MQ_SERVICE_ERROR");
//                LOG.error(com.aafes.stargate.gateway.vision.Common.
//                        convertStackTraceToString(e));
//            } else {
//                t.setReasonCode(configurator.get("INTERNAL_SERVER_ERROR"));
//                t.setResponseType(ResponseType.DECLINED);
//                t.setDescriptionField("INTERNAL_SERVER_ERROR");
//            }
//        }
//
//        return t;
//    }
//
//    private void getResponse(String correlationId, Transaction t) {
//        byte[] rsp = null;
//        if (correlationId != null) {
//            try {
//                // Catching may have to reverse the transaction.
//                /**
//                 * Uncomment below line for actual vision call
//                 */
//                rsp = mqhandler.getMessage(correlationId);
//                t.setResponseAuthDateTime(this.getSystemDateTime());
//                vpFormatter.parseISO8583(rsp, t);
//                /**
//                 * comment below line for actual vision call
//                 */
//                //CICSMockBean.getMockdata(t);
//                LOG.debug("Pulling data from MQ CID and AuthNumber :", t.getTraceId() + ", " + t.getAuthNumber());
//            } catch (JMSException ex) {
//                t.setResponseType(ResponseType.TIMEOUT);
//                t.setReasonCode(configurator.get("TIME_OUT"));
//                LOG.error("JMS exception: " + ex);
//            } catch (UnsupportedEncodingException ex) {
//                t.setResponseType(ResponseType.TIMEOUT);
//                t.setReasonCode(configurator.get("TIME_OUT"));
//                LOG.error("UnsupportedEncodingException exception: " + ex);
//            } catch (ParseException ex) {
//                t.setResponseType(ResponseType.TIMEOUT);
//                t.setReasonCode(configurator.get("TIME_OUT"));
//                LOG.error("ParseException exception: " + ex);
//            } catch (GatewayException cex) {
//                t.setResponseType(ResponseType.TIMEOUT);
//                t.setReasonCode(configurator.get("TIME_OUT"));
//                LOG.error("Credit Exception: " + cex);
//            } catch (IOException ex) {
//                t.setResponseType(ResponseType.TIMEOUT);
//                t.setReasonCode("ERR");
//                LOG.error("IOException: " + ex);
//            } catch (Exception ex) {
//                t.setResponseType(ResponseType.TIMEOUT);
//                t.setReasonCode("ERR");
//                LOG.error("Exception: " + ex);
//            }
//        }
//    }
//
//    public CICSHandlerBean getMqhandler() {
//        return mqhandler;
//    }
//
//    public void setMqhandler(CICSHandlerBean mqhandler) {
//        this.mqhandler = mqhandler;
//    }
//
//    private String getSystemDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        Date date = new Date();
//        String ts = dateFormat.format(date);
//        return ts;
//    }
//
//    /**
//     * @param configurator the configurator to set
//     */
//    public void setConfigurator(Configurator configurator) {
//        this.configurator = configurator;
//    }
//
//    /**
//     * @param vpFormatter the vpFormatter to set
//     */
//    public void setVpFormatter(VisionPlusFormatter vpFormatter) {
//        this.vpFormatter = vpFormatter;
//    }
}
