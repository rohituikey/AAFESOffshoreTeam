/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WexStub;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author uikuyr
 */
public class NBSStubTest {

    NBSStub subjectUnderTest;

    @Before
    public void setUp() {
        subjectUnderTest = new NBSStubImpl();
    }

    @Test
    public void testGetResponse() {
        String Request = "02007FB0D7FF8000000000223007AUTHREQ000215561001A663208002WI23001S00120376006496628299904508=200950041002101230106632415423010014005360790100002.099000008.1060010017.01000009.000000001.001020009.00";
       subjectUnderTest.getResponse(Request.getBytes());
     //   String result = subjectUnderTest.getResponse(Request.getBytes());
    }

}
