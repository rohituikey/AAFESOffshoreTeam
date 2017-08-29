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
        String Request = "0200005F786BC000000000201007AUTHREQ000217481001A0100002WI001N00001S0012006123456007010000101001400000";
        String result = subjectUnderTest.getResponse(Request);
    }

}
