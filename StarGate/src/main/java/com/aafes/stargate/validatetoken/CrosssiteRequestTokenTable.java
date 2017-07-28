/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.validatetoken;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Table;

/**
 * MAPPING CLASS FOR TABLE STARGATE.CROSSSITEREQUESTTOKENTABLE
 * @author burangir
 */
@Table(keyspace = "stargate", name = "crosssiterequesttokentable")
public class CrosssiteRequestTokenTable {

    @ClusteringColumn(0)
    private String tokenid;
    @ClusteringColumn(1)
    private String identityuuid;
    private String tokenstatus;
    private String tokencredatetime;
    
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

    /**
     * @param tokencredatetime the tokencredatetime to set
     */
    public void setTokencredatetime(String tokencredatetime) {
        this.tokencredatetime = tokencredatetime;
    }
}