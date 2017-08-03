/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WexStub;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author uikuyr
 */
public class NBSStubTest {
    
    @Before
    public void setUp() {
    }

    @Test
    public void testGetResponse() {
        String request = "0231805FE00000000000000000000007C000013dummy Term ID013dummy Appname00110042501010dummy Info00110011010dummy Info0011010dummy Info001S0011010dummy Info0011";
        NBSStub subjectUnderTest = new NBSStubImpl();
        String result = subjectUnderTest.getResponse(request);
        assertEquals("02310060000000000000002c$0031000231007FFFF800000000011AuthRequest0064659870020100310000225007Message008cardType002010054host00847596587008458745690057856900478960045896005753910067896450027900478960059856300578965", result);
        //fail("The test case is a prototype.");
    }
    
    @Test
    public void testGetResponse_Declined() {
        String request = "231805FE00000000000000000000007C000013dummy Term ID013dummy Appname00110042501010dummy Info00110011010dummy Info0011010dummy Info001S0011010dummy Info0011";
        NBSStub subjectUnderTest = new NBSStubImpl();
        String result = subjectUnderTest.getResponse(request);
        assertEquals("02310060000000000000002c?0032000231007FFFF800000000011AuthRequest0064659870020100310000225007Message008cardType002010054host00847596587008458745690057856900478960045896005753910067896450027900478960059856300578965", result);
        //fail("The test case is a prototype.");
    }
    
}