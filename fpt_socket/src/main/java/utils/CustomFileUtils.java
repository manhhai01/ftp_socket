/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;

/**
 *
 * @author Son
 */
public class CustomFileUtils {
    private String type;
    public static String determineType(File file){
        String filetype= file.getName().split("\\.")[1];
        return switch (filetype) {
            case "jpg", "png" -> "I";
            case "txt", "doc" -> "A";
            default -> "I";
        };
    }
}
