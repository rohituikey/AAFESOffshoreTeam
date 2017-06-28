/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.vision.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.Stateless;

/**
 *
 * @author singha
 */
@Stateless
public class VisionStub {

    public boolean returnVisionResponse130(Transaction t) {

        if (t.getIdentityUuid() != null
                && (t.getIdentityUuid().equals("7081959f-ee14-495c-b22f-a0f602a8730f")
                || t.getIdentityUuid().equals("ecca7eb6-1314-4cb4-b156-eaf6929aebd5"))) {

            if (t.getAccount() != null
                    && (t.getAccount().equals("4012000033330026") //Visa
                    || t.getAccount().equals("5424180279791732") //Master Card
                    || t.getAccount().equals("2223000048400011") //Master Card 
                    || t.getAccount().equals("373953192351004") //American Express
                    || t.getAccount().equals("6011000990099818") //Discover
                    || t.getAccount().equals("6510000000001248") //Discover
                    )) {
                t.setReasonCode("130");
                t.setResponseType(ResponseType.TIMEOUT);

                return true;
            }

        }

        return false;
    }

    public boolean returnVisionResponse97(Transaction t) {

        if (t.getIdentityUuid() != null
                && (t.getIdentityUuid().equals("7081959f-ee14-495c-b22f-a0f602a8730f")
                || t.getIdentityUuid().equals("ecca7eb6-1314-4cb4-b156-eaf6929aebd5"))) {

            if (t.getAccount() != null
                    && (t.getAccount().equals("4012000033330026") //Visa
                    || t.getAccount().equals("5424180279791732") //Master Card
                    || t.getAccount().equals("2223000048400011") //Master Card 
                    || t.getAccount().equals("373953192351004") //American Express
                    || t.getAccount().equals("6011000990099818") //Discover
                    || t.getAccount().equals("6510000000001248") //Discover
                    )) {
                t.setReasonCode("97");
                t.setResponseType(ResponseType.TIMEOUT);

                return true;
            }

        }

        return false;
    }

    public boolean returnVisionResponse99(Transaction t) {

        if (t.getIdentityUuid() != null
                && (t.getIdentityUuid().equals("7081959f-ee14-495c-b22f-a0f602a8730f")
                || t.getIdentityUuid().equals("ecca7eb6-1314-4cb4-b156-eaf6929aebd5"))) {

            if (t.getAccount() != null
                    && (t.getAccount().equals("4012000033330026") //Visa
                    || t.getAccount().equals("5424180279791732") //Master Card
                    || t.getAccount().equals("2223000048400011") //Master Card 
                    || t.getAccount().equals("373953192351004") //American Express
                    || t.getAccount().equals("6011000990099818") //Discover
                    || t.getAccount().equals("6510000000001248") //Discover
                    )) {
                t.setReasonCode("99");
                t.setResponseType(ResponseType.TIMEOUT);

                return true;
            }

        }

        return false;
    }

}
