/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cipher;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * @author lamanhhai
 */
public class Main {

    public static void main(String[] args) throws Exception {

//        String encryptText = AESCipher.encrypt(aesKey, text);
//        System.out.println("Dữ liệu đã qua mã hóa AES:\n" + encryptText);
//
//        String signature = RSACipher.sign(Config.CLIENT_PRIVATE_KEY, aesKey);
//        System.out.println("Chữ ký:\n" + signature);
//
//        byte[] encryptKey = RSACipher.encrypt(Config.SERVER_PUBLIC_KEY, aesKey);
//        System.out.println("Khóa AES đã mã hóa:\n" + new String(encryptKey));
//
//        System.out.println("\n************************Phân cách************************\n");
//
//        byte[] aesKey1 = RSACipher.decrypt(Config.SERVER_PRIVATE_KEY, encryptKey);
//        System.out.println("Khóa AES đã giải mã:\n" + new String(aesKey1));
//
//        Boolean result = RSACipher.checkSign(Config.CLIENT_PUBLIC_KEY, aesKey1, signature);
//        System.out.println("Kết quả kiểm tra chữ ký:\n" + result);
//
//        String decryptText = AESCipher.decrypt(aesKey1, encryptText);
//        System.out.println("Dữ liệu đã qua giải mã AES:\n" + decryptText);
        // Chuyển text thành mảng byte
//        String text = "Xin chào server!";
//
//        byte[] aesKey = SecureRandomUtil.getRandom(16).getBytes();
//        System.out.println("Khóa AES được tạo:\n" + new String(aesKey));
//
//        String message = AESCipher.encrypt(aesKey, text);
//
//        byte[] textBytes = message.getBytes(StandardCharsets.UTF_8);
//        
//  
//
//        byte[] combinedArray = new byte[aesKey.length + textBytes.length];
//        System.arraycopy(aesKey, 0, combinedArray, 0, aesKey.length);
//        System.arraycopy(textBytes, 0, combinedArray, aesKey.length, textBytes.length);
//
//        System.out.println("Kiem tra: " + new String(combinedArray));
//        
//        //
//
//        byte[] encrypt = RSACipher.encrypt(Config.SERVER_PUBLIC_KEY, combinedArray);
//        System.out.println("Ma hoa: " + new String(encrypt));
//        
        //
        
        String encrypt = "Welcome server";
        byte[] decrypt = RSACipher.decrypt(Config.SERVER_PRIVATE_KEY, encrypt.getBytes());
        System.out.println("Key: " + new String(decrypt));
        String test = "26q742RoR87hDIz0";
        if(new String(decrypt).equals(test)) {
            System.out.println("True");
        } else {
            System.out.println("False");
        }

    }
}
