package WexStub;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import WexStub.NBSStub;

/**
 *
 * @author uikuyr
 */
public class NBSStubImpl implements NBSStub{

    @Override
    public String getResponse(String request) {
        if(request.contains("APPROVED"))
            return "APPROVED";
        else
            return "DECLINED";
    }
    
}
