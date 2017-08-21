/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.imported;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author singha
 */
@Stateless
public class WexRepository {

    @EJB
    private WexSettleMessagesDao settleMessageDAO;

    public List<WexSettleEntity> getWexTransactions(String tid, String processDate, String status) {

        return settleMessageDAO.getWexTransactions(tid, processDate, status);
    }

    public void updateWexSettleData(List<WexSettleEntity> Wexdata, String In_Progress) {
        settleMessageDAO.updateWexSettleData(Wexdata, In_Progress);
    }
}
