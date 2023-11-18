/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cipher;

import static cipher.RSACipher.getPrivateKey;
import static cipher.RSACipher.getPublicKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

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
        String encrypt = "1Gxd9lXdiBPgIEixdWxOrH76cKIuExDGEinxYbY/O21XfIp9qzgkZBLUqTFKN6N47o/pfoKqnXAEFOCBWCJwfUNUcBMQ9fK3StZ+fvZF3ZuBBM/4oNKbY34ddhBEdik8dokik3QXi2IJg09sa97L+IGeEXJ6aqYLVHUZEfHj9MA=";
        byte[] decrypt = RSACipher.decrypt(Config.SERVER_PRIVATE_KEY, encrypt.getBytes());
        System.out.println("Key: " + new String(decrypt));
        String test = "40DlMM1f6ltMzG9o";
        if(new String(decrypt).equals(test)) {
            System.out.println("True");
        } else {
            System.out.println("False");
        }
//        System.out.println(Config.SERVER_PRIVATE_KEY);
//        byte[] doFinal = RSACipher.decrypt(Config.SERVER_PRIVATE_KEY, "VÇrGkk}Ê¼o³(\\;Î$9´h.j>00‡p\"#³è%•'*)à›Up;¸Ò’¯ ¸fš˜}çÀ\",õ›,Ç¶XZ.N©ç†÷ŒÇ`r“<Õ HT¡æ+qåT!Ê†`ÝÊ6Ú>w52;£?òÆa^ˆ•åf:ÙÃ]Õmhˆì… {".getBytes());
//        System.out.println(new String(doFinal));
//        System.out.println("VÇrGkk}Ê¼o³(\\;Î$9´h.j>00‡p\"#³è%•'*)à›Up;¸Ò’¯ ¸fš˜}çÀ\",õ›,Ç¶XZ.N©ç†÷ŒÇ`r“<Õ HT¡æ+qåT!Ê†`ÝÊ6Ú>w52;£?òÆa^ˆ•åf:ÙÃ]Õmhˆì… {".getBytes().length);
        
    }
}
