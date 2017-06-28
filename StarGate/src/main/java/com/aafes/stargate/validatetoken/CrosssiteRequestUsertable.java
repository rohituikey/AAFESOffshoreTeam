/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.validatetoken;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Table;

/**
 * MAPPING CLASS FOR TABLE STARGATE.CROSSSITEREQUESTUSERTABLE
 * @author burangir
 */
@Table(keyspace = "stargate", name = "crosssiterequestusertable")
public class CrosssiteRequestUsertable {
    @ClusteringColumn(0)
    private String identityuuid;
    @ClusteringColumn(1)
    private String userid;
    @ClusteringColumn(2)
    private String password;
    
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
     * @return the userid
     */
    public String getUserid() {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}