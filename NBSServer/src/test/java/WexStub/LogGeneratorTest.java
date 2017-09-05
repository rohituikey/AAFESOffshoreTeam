/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WexStub;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author uikuyr
 */
public class LogGeneratorTest {

    public LogGeneratorTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGenerateLogFile() {
        LogGenerator instance = new LogGenerator();
        instance.generateLogFile("first Line");
        instance.generateLogFile("Second Line");
    }

}
