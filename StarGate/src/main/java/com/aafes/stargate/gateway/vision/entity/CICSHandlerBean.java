/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.vision.entity;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.gateway.MQGatewayException;
import com.aafes.stargate.util.Encryptor;
import static com.ibm.mq.constants.CMQC.MQAT_JAVA;
import static com.ibm.mq.constants.CMQC.MQCIH_PASS_EXPIRATION;
import static com.ibm.mq.constants.CMQC.MQCIH_REPLY_WITHOUT_NULLS;
import static com.ibm.mq.constants.CMQC.MQCIH_SYNC_ON_RETURN;
import static com.ibm.mq.constants.CMQC.MQCUOWC_ONLY;
import static com.ibm.mq.constants.CMQC.MQFMT_CICS;
import static com.ibm.mq.constants.CMQC.MQFMT_NONE;
import static com.ibm.mq.constants.CMQC.MQPER_NOT_PERSISTENT;
import static com.ibm.mq.constants.CMQC.MQRO_COPY_MSG_ID_TO_CORREL_ID;
import com.ibm.mq.headers.MQCIH;
import com.ibm.mq.headers.MQHeader;
import static com.ibm.mq.jms.JMSC.MQJMS_CLIENT_NONJMS_MQ;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueSender;
import com.ibm.mq.jms.MQQueueSession;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.v6.base.internal.MQC;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.BytesMessage;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nguyentul
 */
@Stateless
public class CICSHandlerBean {

    private static final Logger LOG
            = LoggerFactory.getLogger(CICSHandlerBean.class.getSimpleName());
    static final DecimalFormat TIME_FORMAT = new DecimalFormat("0000;0000");
    private MQQueueConnectionFactory cf = null;
    private MQQueueConnection connection = null;

    @Inject
    private String cicsMQProgName;

    @Inject
    private String cicsUserName;

    @Inject
    private String cicsPassword;

    @Inject
    private String cicsMQFunc;

    @Inject
    private int cicsMQExpiry;

    @Inject
    private String cicsMQManagerName;

    @Inject
    private String mqHostName;

    @Inject
    private String qManagerName;

    @Inject
    private String qChannelName;

    @Inject
    private int qPort;

    @Inject
    private String qUser;

    @Inject
    private String qPassword;

    @Inject
    private String requestQ;

    @Inject
    private String responseQ;

    @Inject
    private int mqReadTimeOut;

    // Default TransID
    private String transId = "";

    @PostConstruct
    public void PostContruct() {
        try {
            LOG.info("Decrypting passwords");
            decryptValues();
            LOG.info("Initiating MQ connection.");
            cf = new MQQueueConnectionFactory();
            cf.setHostName(mqHostName);
            cf.setQueueManager(qManagerName);
            cf.setChannel(qChannelName);
            cf.setPort(qPort);
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE,
                    WMQConstants.WMQ_CM_CLIENT);

            connection = (MQQueueConnection) cf.createQueueConnection(qUser, "");
            connection.start();
            LOG.info("MQ Connection Started." + cf.getChannel() + cf.
                    getHostName() + cf.getQueueManager());
        } catch (JMSException ex) {
            LOG.error("Unable contact  MQ startup" + ex.getMessage());
             throw new MQGatewayException("Unable to contact MQ startup.");
        }
    }

    public String sendMessage(byte[] payload) {
        String correlationId = new String(getCorrelationIDWithCalendar());
        LOG.debug("Sending with traceid: " + correlationId);
        logASCII("To Vision: ", payload);
        MQQueueSession session = null;
        MQQueueSender sender = null;
        try {

            session = (MQQueueSession) connection.
                    createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            ConnectionMetaData connInfo = connection.getMetaData();

            LOG.debug(
                    "Fucntion " + cicsMQFunc + " progname: " + cicsMQProgName + " user: " + cicsUserName);

            MQQueue requestQueue = (MQQueue) session.createQueue(
                    "queue:///" + requestQ);
            requestQueue.setTargetClient(MQJMS_CLIENT_NONJMS_MQ);

            // Start the connection
            requestQueue.setBooleanProperty(WMQConstants.WMQ_MQMD_WRITE_ENABLED,
                    true);
            requestQueue.setIntProperty(WMQConstants.WMQ_MQMD_MESSAGE_CONTEXT,
                    WMQConstants.WMQ_MDCTX_SET_IDENTITY_CONTEXT);
            //WMQ_MDCTX_SET_IDENTITY_CONTEXT
            //WMQ_MDCTX_SET_ALL_CONTEXT
            LOG.info("At MQQue Sender ::::");
            sender = (MQQueueSender) session.createSender(requestQueue);

            BytesMessage bm = session.createBytesMessage();
            LOG.info("1 ::::");
            MQCIH cih = new MQCIH();
            LOG.info("2 ::::");
            cih.setVersion(2);
            cih.setFunction(cicsMQFunc);
            cih.setAuthenticator(cicsPassword);
            cih.setTransactionId(transId);
            cih.setUOWControl(MQCUOWC_ONLY);
            cih.setFlags(MQCIH_REPLY_WITHOUT_NULLS | MQCIH_PASS_EXPIRATION
                    | MQCIH_SYNC_ON_RETURN);
            cih.setReplyToFormat(MQFMT_NONE);
            cih.setFormat(MQFMT_NONE);
            cih.setLinkType(1);
            cih.setOutputDataLength(1600);
            MQHeader header = cih;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            header.write(new DataOutputStream(out));
            byte[] hd = out.toByteArray();
            bm.setStringProperty("JMS_IBM_Format", MQFMT_CICS);	//Struc Length v1=164 v2=180
            bm.
                    setStringProperty("JMS_IBM_MQMD_UserIdentifier",
                            cicsUserName);
            bm.setStringProperty("JMS_IBM_MQMD_ReplyToQ", responseQ);
            bm.setStringProperty("JMS_IBM_MQMD_ReplyToQMgr",
                    cicsMQManagerName);
            LOG.info("3 ::::");
            bm.setIntProperty("JMS_IBM_Character_Set", 1208);
            bm.setIntProperty("JMS_IBM_MQMD_Priority", 0);
            bm.setIntProperty("JMS_IBM_MQMD_Expiry", cicsMQExpiry);
            bm.setIntProperty("JMS_IBM_MQMD_Persistence",
                    MQPER_NOT_PERSISTENT);
            bm.setObjectProperty("JMS_IBM_MQMD_CorrelId",
                    MQC.MQCI_NEW_SESSION);
            bm.setIntProperty("JMS_IBM_MsgType", MQC.MQMT_REQUEST);

            bm.setObjectProperty("JMS_IBM_MQMD_MsgId", correlationId.
                    getBytes());
            bm.setIntProperty("JMS_IBM_MQMD_Report",
                    MQRO_COPY_MSG_ID_TO_CORREL_ID);
            bm.setIntProperty("JMS_IBM_MQMD_PutApplType", MQAT_JAVA);

            bm.writeBytes(hd);

            bm.writeBytes(cicsMQProgName.getBytes("CP500"));
            bm.writeBytes("    1208".getBytes());

            bm.writeBytes(payload);
            // Start the connection
            sender.send(bm);
            LOG.info("Sent ::::");
            LOG.debug("Sent message to Queue: " + requestQueue.
                    getQueueName());

            LOG.debug("Message Sent OK.\n");

        } catch (JMSException | IOException jmsex) {
            LOG.error("Unable to send MQ Message." + jmsex.getMessage());
            throw new MQGatewayException("Unable to send MQ Message.");
        } finally {
            if (sender != null) {
                try {
                    sender.close();
                } catch (JMSException ex) {
                    LOG.error("Failed to close the message sender: "
                            + ex.toString());
                   throw new MQGatewayException("Failed to close the message sender:");
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException ex) {
                    LOG.error("Failed to close the session: "
                            + ex.toString());
                     throw new MQGatewayException("Failed to close the session:");
                }
            }
        }

        LOG.info("Success ::::");
        return correlationId;
    }

    public byte[] getMessage(String traceId) {
        String hexCorrelationID = String.format("%024x", new BigInteger(1,
                traceId.substring(0, 24).getBytes()));
        String selector = "JMSCorrelationID='ID:" + hexCorrelationID + "'";
        LOG.debug("Getting Message for " + selector);
        byte[] responsePayload = null;
        MQQueueSession session = null;
        MessageConsumer receiver = null;
        try {
            session = (MQQueueSession) connection.createQueueSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            MQQueue Replyqueue = (MQQueue) session.createQueue(
                    "queue:///" + responseQ);
            receiver = session.createConsumer(Replyqueue, selector);
            Message message = receiver.receive(mqReadTimeOut);
            if (message != null) {
                LOG.debug("correlation id " + message.getJMSCorrelationID());
            }
            if (message instanceof BytesMessage) {
                LOG.debug(
                        "We have a ByteMessage response Payload so process it accordingly");
                BytesMessage bm = (BytesMessage) message;
                responsePayload = new byte[(int) bm.getBodyLength()];
                bm.readBytes(responsePayload);
                String codePage = message.getStringProperty(
                        WMQConstants.JMS_IBM_CHARACTER_SET);
                String textString = new String(responsePayload, codePage);
                LOG.debug("RESPONSE: " + codePage + " - " + textString);
            } else if (message instanceof TextMessage) {
                LOG.debug(
                        "We have a TextMessage response Payload so process it accordingly");
                TextMessage tm = (TextMessage) message;
                responsePayload = tm.getText().getBytes();
            } else if (message == null) {
                LOG.error("No message was received.");
            } else {
                LOG.error("The message received was of an unknown type.");
            }
        } catch (JMSException | UnsupportedEncodingException ex) {
            LOG.error(ex.toString());
        } finally {
            if (receiver != null) {
                try {
                    receiver.close();
                } catch (JMSException ex) {
                    LOG.error("Failed to close the message receiver: "
                            + ex.toString());
                        throw new MQGatewayException("Failed to close the message receiver:");
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException ex) {
                    LOG.error("Failed to close the session: "
                            + ex.toString());
                    throw new MQGatewayException("Failed to close the session:");
                }
            }
        }
        return responsePayload;
    }

    @PreDestroy
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException ex) {
                java.util.logging.Logger.getLogger(CICSHandlerBean.class.
                        getName()).log(Level.SEVERE, null, ex);
                 throw new MQGatewayException("Failed to close the connection:");
            }

        }
    }

    private static byte[] getCorrelationIDWithCalendar() {
        Calendar cal = Calendar.getInstance();
        String val = String.valueOf(cal.get(Calendar.YEAR));
        val += TIME_FORMAT.format(cal.get(Calendar.DAY_OF_YEAR));
        val += UUID.randomUUID().toString().replaceAll("-", "");
        return val.substring(0, 24).getBytes();
    }

    private void decryptValues() {

        String baseDir = System.getProperty("jboss.server.config.dir");
        String keysPath = baseDir + "/crypto/keys";
        String logPath = baseDir + "/crypto.log4j.properties";

        Encryptor encryptor = new Encryptor(keysPath, logPath);

        cicsPassword = encryptor.decrypt(cicsPassword);
        qPassword = encryptor.decrypt(qPassword);

    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getCicsMQProgName() {
        return cicsMQProgName;
    }

    public void setCicsMQProgName(String cicsMQProgName) {
        this.cicsMQProgName = cicsMQProgName;
    }

    public String getCicsUserName() {
        return cicsUserName;
    }

    public void setCicsUserName(String cicsUserName) {
        this.cicsUserName = cicsUserName;
    }

    public String getCicsPassword() {
        return cicsPassword;
    }

    public void setCicsPassword(String cicsPassword) {
        this.cicsPassword = cicsPassword;
    }

    public String getCicsMQFunc() {
        return cicsMQFunc;
    }

    public void setCicsMQFunc(String cicsMQFunc) {
        this.cicsMQFunc = cicsMQFunc;
    }

    public int getCicsMQExpiry() {
        return cicsMQExpiry;
    }

    public void setCicsMQExpiry(int cicsMQExpiry) {
        this.cicsMQExpiry = cicsMQExpiry;
    }

    public String getCicsMQManagerName() {
        return cicsMQManagerName;
    }

    public void setCicsMQManagerName(String cicsMQManagerName) {
        this.cicsMQManagerName = cicsMQManagerName;
    }

    public String getMqHostName() {
        return mqHostName;
    }

    public void setMqHostName(String mqHostName) {
        this.mqHostName = mqHostName;
    }

    public String getqManagerName() {
        return qManagerName;
    }

    public void setqManagerName(String qManagerName) {
        this.qManagerName = qManagerName;
    }

    public String getqChannelName() {
        return qChannelName;
    }

    public void setqChannelName(String qChannelName) {
        this.qChannelName = qChannelName;
    }

    public int getqPort() {
        return qPort;
    }

    public void setqPort(int qPort) {
        this.qPort = qPort;
    }

    public String getqUser() {
        return qUser;
    }

    public void setqUser(String qUser) {
        this.qUser = qUser;
    }

    public String getqPassword() {
        return qPassword;
    }

    public void setqPassword(String qPassword) {
        this.qPassword = qPassword;
    }

    public String getRequestQ() {
        return requestQ;
    }

    public void setRequestQ(String requestQ) {
        this.requestQ = requestQ;
    }

    public String getResponseQ() {
        return responseQ;
    }

    public void setResponseQ(String responseQ) {
        this.responseQ = responseQ;
    }

    public int getMqReadTimeOut() {
        return mqReadTimeOut;
    }

    public void setMqReadTimeOut(int mqReadTimeOut) {
        this.mqReadTimeOut = mqReadTimeOut;
    }

    private void logASCII(String label, byte[] payload) {
        try {
            LOG.info(label + new String(payload, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            LOG.error(ex.toString());
        }
    }
}
