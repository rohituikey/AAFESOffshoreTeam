/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.wex.simulator.NBSFormatterFS;

/**
 *
 * @author alugumetlas
 */
public class WexProcessor_FS {
 
    private NBSFormatterFS nBSFormatterFS;
  public Transaction processRequest(Transaction t) 
  {
      nBSFormatterFS = new NBSFormatterFS();
     String request =  nBSFormatterFS.createPreAuthRequestForNBS(t);
    return t;
  }  
}
