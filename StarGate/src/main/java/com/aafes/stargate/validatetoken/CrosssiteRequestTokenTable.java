/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.validatetoken;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Table;

/**
 *
 * @author burangir
 */
@Table(keyspace = "stargate", name = "crosssiterequesttokentable")
public class CrosssiteRequestTokenTable {

    @ClusteringColumn(0)
    private String tokenid;
    private String tokenstatus;
    private String tokencredatetime;
    @ClusteringColumn(1)
    private String identityuuid;
    //private String termid;
    //private String customerid;
    //private String media;
    //private String account;
    
    /**
     * @return the tokenid
     */
    public String getTokenid() {
        return tokenid;
    }

    /**
     * @param tokenid the tokenid to set
     */
    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    /**
     * @return the tokenstatus
     */
    public String getTokenstatus() {
        return tokenstatus;
    }

    /**
     * @param tokenstatus the tokenstatus to set
     */
    public void setTokenstatus(String tokenstatus) {
        this.tokenstatus = tokenstatus;
    }

    /**
     * @return the tokencredatetime
     */
    public String getTokencredatetime() {
        return tokencredatetime;
    }

    /**
     * @return the identityuuid
     */
    public String getIdentityuuid() {
        return identityuuid;
    }

    /**
     * @param identityuuid the identityuuid to set
     */
    public void setIdentityuuid(String identityuuid) {
        this.identityuuid = identityuuid;
    }

//    /**
//     * @return the termid
//     */
//    public String getTermid() {
//        return termid;
//    }
//
//    /**
//     * @param termid the termid to set
//     */
//    public void setTermid(String termid) {
//        this.termid = termid;
//    }
//
//    /**
//     * @return the customerid
//     */
//    public String getCustomerid() {
//        return customerid;
//    }
//
//    /**
//     * @param customerid the customerid to set
//     */
//    public void setCustomerid(String customerid) {
//        this.customerid = customerid;
//    }

//    /**
//     * @return the media
//     */
//    public String getMedia() {
//        return media;
//    }
//
//    /**
//     * @param media the media to set
//     */
//    public void setMedia(String media) {
//        this.media = media;
//    }
//
//    /**
//     * @return the account
//     */
//    public String getAccount() {
//        return account;
//    }
//
//    /**
//     * @param account the account to set
//     */
//    public void setAccount(String account) {
//        this.account = account;
//    }

    /**
     * @param tokencredatetime the tokencredatetime to set
     */
    public void setTokencredatetime(String tokencredatetime) {
        this.tokencredatetime = tokencredatetime;
    }
}