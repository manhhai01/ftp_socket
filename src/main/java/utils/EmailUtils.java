/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 *
 * @author lamanhhai
 */
public class EmailUtils {

    private final String username = "nguyenhoangbao799@gmail.com";
    private final String password = "ikpcwbkckfchkolg";

    private final String subject = "Xác thực tài khoản - Mã OTP của bạn";

    public boolean sendEmail(String emailTo, String otp) {
        boolean isSended = false;
        String body = "<p>Chào bạn,</p>\n" +
"    <p>Chúng tôi đã nhận được yêu cầu xác thực tài khoản của bạn. Dưới đây là mã OTP của bạn:</p>\n" +
"    <p><strong>Mã OTP:</strong> "+otp+"</p>\n" +
"    <p>Mã OTP này sẽ có hiệu lực trong vòng 10 phút kể từ thời điểm bạn nhận được email này. Vui lòng không chia sẻ mã OTP này với bất kỳ ai khác.</p>\n" +
"    <p>Xin cảm ơn.</p>";
        try {

            Properties prop = new Properties();
            prop.put("mail.smtp.auth", true);
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");

            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(emailTo));
            message.setSubject(subject);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(body, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            isSended = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isSended;
    }
}
