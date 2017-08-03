package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.ResponseType;
import com.aafes.stargate.util.StarGateConstants;
import com.aafes.stargate.util.SvsUtil;
import com.svs.svsxml.beans.Amount;
import com.svs.svsxml.beans.Card;
import com.svs.svsxml.beans.MerchandiseReturnRequest;
import com.svs.svsxml.beans.MerchandiseReturnResponse;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.service.SVSXMLWay;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author uikuyr
 */
@Stateless
public class MerchandiseReturnMessageProcessor extends Processor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MerchandiseReturnMessageProcessor.class.getSimpleName());

    @Override
    public void processRequest(Transaction transaction) {
        try {

            LOGGER.info("MerchandiseReturn.process method start...");
            
            MerchandiseReturnRequest request = new MerchandiseReturnRequest();
            LOGGER.debug("Created Merchandise Return Service ...");
            double amt = transaction.getAmount();
            amt = amt/100;
            if(amt < 0)
            {
                amt = amt*-1;
            }
            Amount amount = new Amount();
            amount.setAmount(amt);
            amount.setCurrency(StarGateConstants.CURRENCY);
            request.setReturnAmount(amount);

            Card card = new Card();
            card.setCardNumber(transaction.getAccount());
            card.setPinNumber(transaction.getGcpin());
            //card.setCardTrackOne(transaction.getTrack1());
            //card.setCardTrackTwo(transaction.getTrack2());
            request.setCard(card);

            request.setDate(SvsUtil.formatLocalDateTime());
            request.setInvoiceNumber(transaction.getOrderNumber().substring(transaction.getOrderNumber().length() - 8));
            Merchant merchant = new Merchant();
            merchant.setDivision(StarGateConstants.MERCHANT_DIVISION_NUMBER);
            merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
            merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
            merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
            request.setMerchant(merchant);

            request.setRoutingID(StarGateConstants.ROUTING_ID);
            request.setCheckForDuplicate(StarGateConstants.TRUE);
            request.setTransactionID(transaction.getRrn() + "0000");
            // request.setStan(transaction.getSTAN());
            SVSXMLWay svsXMLWay = SvsUtil.setUserNamePassword();
            LOGGER.debug("Sending Merchandise Return Request ...");
            MerchandiseReturnResponse response = svsXMLWay.merchandiseReturn(request);
            LOGGER.debug("Received Merchandise Return Response ...");
            if (response != null) {
                transaction.setReasonCode(response.getReturnCode().getReturnCode());
                transaction.setDescriptionField(response.getReturnCode().getReturnDescription());
                if (transaction.getReasonCode().equalsIgnoreCase("01")) {
                    transaction.setResponseType(ResponseType.APPROVED);
                } else {
                    transaction.setResponseType(ResponseType.DECLINED);
                }
                transaction.setAuthNumber(response.getAuthorizationCode());
                transaction.setBalanceAmount((long) (response.getBalanceAmount().getAmount()*100));

                LOGGER.debug("ReturnDescription : " + String.valueOf(response.getReturnCode().getReturnDescription()));
            } else {
                transaction.setResponseType(ResponseType.DECLINED);
            }

        } catch (Exception e) {
            
            LOGGER.error("Exception  while processing the Merchandise Return for RRN : " + transaction.getRrn());
            LOGGER.error(e.toString());
            throw new GatewayException("INTERNAL_SERVER_ERROR");
        }
        
        LOGGER.info("MerchandiseReturn.process method exit...");
    }
}