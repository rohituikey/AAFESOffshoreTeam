/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;


@Table(keyspace = "tokenizer", name = "vault")
public class Vault {

    @PartitionKey
    private String tokennumber = "";
    private String accountnumber = "";
    
    
    
    /**
     * @return the tokennumber
     */
    public String getTokennumber() {
        return tokennumber;
    }

    /**
     * @param tokennumber the tokennumber to set
     */
    public void setTokennumber(String tokennumber) {
        this.tokennumber = tokennumber;
    }

    /**
     * @return the accountnumber
     */
    public String getAccountnumber() {
        return accountnumber;
    }

    /**
     * @param accountnumber the accountnumber to set
     */
    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }
    
}