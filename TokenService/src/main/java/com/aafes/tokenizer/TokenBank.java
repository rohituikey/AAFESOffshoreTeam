/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Table;

 
@Table(keyspace = "tokenizer", name = "tokenbank")
public class TokenBank {
    
    @ClusteringColumn(0)
    private String tokennumber = "";
    @ClusteringColumn(1)
    private String tokenbankname = "";
    
    private String description = "";
    private String expirydate ="";
    private String status ="";

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
     * @return the tokenbankname
     */
    public String getTokenbankname() {
        return tokenbankname;
    }

    /**
     * @param tokenbankname the tokenbankname to set
     */
    public void setTokenbankname(String tokenbankname) {
        this.tokenbankname = tokenbankname;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the expirydate
     */
    public String getExpirydate() {
        return expirydate;
    }

    /**
     * @param expirydate the expirydate to set
     */
    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
}