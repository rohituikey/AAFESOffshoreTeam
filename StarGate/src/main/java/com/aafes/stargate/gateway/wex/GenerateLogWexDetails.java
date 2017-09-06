/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alugumetlas
 */
public class GenerateLogWexDetails {

    public static void generateDetails(String logFilename, String iSOMsgResponse,String iSOMsg) {
        BufferedWriter logs = null;
        FileWriter fw = null;
        try {
            File file = new File(logFilename+".log");
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            logs = new BufferedWriter(fw);
            logs.write("\n" +new Date()+"\n" + "AUTH REQUEST : "+"\n" + iSOMsg+ "\n"  
                    +"AUTH RESPONSE :"+"\n"+iSOMsgResponse+"\n"+"-----------------------------------------------------------------------------------------------------------------------------------------------------------");

        } catch (IOException ex) {
            Logger.getLogger(GenerateLogWexDetails.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {

                if (logs != null) {
                    logs.close();
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
