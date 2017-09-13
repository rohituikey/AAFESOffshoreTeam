/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WexStub;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author uikuyr
 */
public class NBSStubFieldSeperatorTest {
            NBSStubFieldSeperator subjectUnderTest;
    @Before
    public void setUp() {
        subjectUnderTest = new NBSStubFieldSeperator();
    }

    @Test
    public void testCreateResponse_firstRequest() {
        String[] result = subjectUnderTest.createResponse("<SX>WE1055010401101<FS>AUTHREQ<FS>0001<FS>06001<FS>A<FS>0278<FS>08<FS>WI<FS>1<FS>26<FS>S<FS>2<FS>6900460XXXXXXXX4841=2003*************<FS>75.00<FS>2<FS>3<FS>325781<FS>4<FS>19<FS>1<FS>0.000<FS>0.000<FS>001<FS>0.00<EX><LF>".getBytes());
        assertEquals("<SX>c$<FS>100<EX>", result[0]);
    }
    
    @Test
    public void testCreateResponse_requestAcknowledgment() {
        String[] result = subjectUnderTest.createResponse("<SX>a$<FS>5227<EX><LF>".getBytes());
        assertEquals("", result[0]);
    }
    
    @Test
    public void testCreateResponse_requestLogOff() {
        assertFalse(NBSServer.logOff);
        String[] result = subjectUnderTest.createResponse("<SX>O<EX><LF>".getBytes());
        assertTrue(NBSServer.logOff);
    }
    
    @Test
    public void testCreateResponse_firstRequest_hex() {
        String[] result = subjectUnderTest.createResponse("<!00><!13>?@<!01>AAFES<!00><!13>?@<!02>WE1055479204301<!1C>AUTHREQ<!1C>0001<!1C>06001<!1C>A<!1C>5292<!1C>08<!1C>WI<!1C>1<!1C>42<!1C>S<!1C>2<!1C>6900460000000000006=18111013003600000<!1C>75.00<!1C>2<!1C>3<!1C>1161<!1C>4<!1C>7071<!1C>1<!1C>0.000<!1C>0.000<!1C>001<!1C>0.00<!03><!0A>".getBytes());
        assertEquals("<SX>c$<FS>100<EX>", result[0]);
    }
    
    @Test
    public void testCreateResponse_requestAcknowledgment_hex() {
        String[] result = subjectUnderTest.createResponse("<!00><!13>?@<!02>a$<!1C>5292<!03>V".getBytes());
        assertEquals("", result[0]);
    }
    
    @Test
    public void testCreateResponse_requestLogOff_hex() {
        assertFalse(NBSServer.logOff);
        String[] result = subjectUnderTest.createResponse("00><!13>?`<!02>O<!03><!0A><!00><!13>?`<!04>".getBytes());
        assertTrue(NBSServer.logOff);
    }
    
}
