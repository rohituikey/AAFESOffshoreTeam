/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.control;

import com.aafes.credit.AccountTypeType;
import com.aafes.credit.Message;
import com.aafes.credit.RequestTypeType;
import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.tokenizer.TokenEndPointService;
import com.aafes.starsettler.util.CardType;
import com.aafes.starsettler.util.ResponseType;
import com.aafes.starsettler.util.SettleStatus;
import com.aafes.starsettler.util.TransactionType;
import com.aafes.token.TokenMessage;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Stateless
public class AuthorizationService {

    @Inject
    private String creditMessageEndPoint;

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(AuthorizationService.class.getSimpleName());

    public void sendGiftCardRefundAuths(List<SettleEntity> settleEntitys) throws DatatypeConfigurationException, SettlerException {
        for (SettleEntity settleEntity : settleEntitys) {
            if (CardType.GIFT_CARD.equalsIgnoreCase(settleEntity.getCardType())
                    && TransactionType.Refund.equalsIgnoreCase(settleEntity.getTransactionType())) {
                Message creditMessage = this.buildCreditMessage(settleEntity);
                Client client = ClientBuilder.newBuilder().build();
                WebTarget target = client.target(creditMessageEndPoint);
                Entity entity = Entity.entity(creditMessage, MediaType.APPLICATION_XML);
                Response response = target.request(MediaType.APPLICATION_XML).post(entity);
                creditMessage = response.readEntity(Message.class);
                if (creditMessage != null
                        && creditMessage.getResponse() != null
                        && creditMessage.getResponse().get(0) != null) {
                    Message.Response cmResponse = creditMessage.getResponse().get(0);
                    if (ResponseType.APPROVED.equalsIgnoreCase(cmResponse.getResponseType())) {
                        settleEntity.setSettlestatus(SettleStatus.Done);
                    } else {
                        throw new SettlerException("INVALID_GIFT_CARD");
                    }
                }
            }
        }
    }

    private Message buildCreditMessage(SettleEntity settleEntity) throws DatatypeConfigurationException {
        Message creditMessage = new Message();
        creditMessage.setTypeCode("Request");
        creditMessage.setMajorVersion(BigInteger.valueOf(3));
        creditMessage.setMinorVersion(BigInteger.ONE);
        creditMessage.setFixVersion(BigInteger.ZERO);

        Message.Header header = new Message.Header();
        header.setIdentityUUID(settleEntity.getIdentityUUID());
        header.setComment("Refund Request from StarSettler");
        header.setLocalDateTime(this.getSystemDateTime());
        header.setSettleIndicator(false);
        header.setOrderNumber(settleEntity.getOrderNumber());
        header.setTransactionId(settleEntity.getTransactionId());
        header.setTermId("00");
        creditMessage.setHeader(header);

        Message.Request request = new Message.Request();
        request.setRRN(settleEntity.getRrn());
        request.setMedia(settleEntity.getCardType());
        request.setRequestType(RequestTypeType.REFUND);
        request.setInputType("Keyed");
        request.setToken(AccountTypeType.TOKEN);
        request.setAccount(settleEntity.getCardToken());
        request.setAmountField(BigDecimal.valueOf(Long.parseLong(settleEntity.getPaymentAmount())));
        request.setDescriptionField(RequestTypeType.REFUND.toString());
        Message.Request.AddressVerificationService avs = new Message.Request.AddressVerificationService();
        avs.setBillingAddress1(settleEntity.getAddressLine1());
        avs.setBillingAddress2(settleEntity.getAddressLine2());
        avs.setBillingCity(settleEntity.getCity());
        avs.setBillingCountryCode(settleEntity.getCountryCode());
        avs.setBillingZipCode(settleEntity.getPostalCode());
        request.setAddressVerificationService(avs);
        creditMessage.getRequest().add(request);

        return creditMessage;
    }

    private XMLGregorianCalendar getSystemDateTime() throws DatatypeConfigurationException {

        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

        return date;
    }

}
