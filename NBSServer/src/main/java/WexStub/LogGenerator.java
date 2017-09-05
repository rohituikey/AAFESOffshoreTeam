/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WexStub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uikuyr
 */
public class LogGenerator {

    private File logFile;
    BufferedWriter bw = null;
    FileWriter fw = null;

    public void generateLogFile(String log) {
        try {
            logFile = new File("NBSServerlog.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile.getAbsoluteFile(),true);
            bw = new BufferedWriter(fw);
            bw.write(new Date()+"\n"+log+"\n");
        } catch (IOException ex) {
            Logger.getLogger(LogGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
