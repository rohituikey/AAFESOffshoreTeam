/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WexStub;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;
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
        String request = "0231005F786BC000000000201007AUTHREQ000218231001A0100002WI001N00001S0012006123456007010000101001400000";
        NBSStub subjectUnderTest = new NBSStubImpl();
        String result = subjectUnderTest.getResponse(request);
        assertEquals("02000060000000000000002c$0031000200007FFFF800000000001A00402780133170621071655001N00200008Approved003WEX0000000000000000015001000630833900575.00001100775.000000300100578965", result);
        //fail("The test case is a prototype.");
    }
    
    @Ignore
    @Test
    public void testGetResponse_Declined() {
        String request = "231805FE00000000000000000000007C000013dummy Term ID013dummy Appname00110042501010dummy Info00110011010dummy Info0011010dummy Info001S0011010dummy Info0011";
        NBSStub subjectUnderTest = new NBSStubImpl();
        String result = subjectUnderTest.getResponse(request);
        assertEquals("02310060000000000000002c?0032000231007FFFF800000000011AuthRequest0064659870020100310000201008Declined008cardType002010054host00847596587008458745690057856900478960045896005753910067896450027900478960059856300578965", result);
        //fail("The test case is a prototype.");
    }
    
    @Ignore
    @Test
    public void testGetResponseSale_Refund() {
        String request = "231805FE00000000000000000000007C000013dummy Term ID013dummy Appname00110042501010dummy Info00110011010dummy Info0011010dummy Info001S0011010dummy Info0011";
        NBSStub subjectUnderTest = new NBSStubImpl();
        String result = subjectUnderTest.getResponse(request);
        assertEquals("02310060000000000000002c?0032000231007FFFF800000000011AuthRequest0064659870020100310000201008Declined008cardType002010054host00847596587008458745690057856900478960045896005753910067896450027900478960059856300578965", result);
        //fail("The test case is a prototype.");
    }
    
//    @Test
//    public void generateIso8583Response(){
//        NBSStub subjectUnderTest = new NBSStubImpl();
//        String test = subjectUnderTest.unpackIso8583("test");
//    }
    
}
