/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bus;

import convertor.UserConvertor;
import dao.UserDao;
import java.time.Duration;
import java.time.LocalDateTime;
import mapper.UserMapper;
import model.User;
import payload.UserDto;
import utils.EmailUtils;
import utils.MP5Utils;
import utils.OtpUtils;

/**
 *
 * @author lamanhhai
 */
public class UserBus {

    private UserDao userDao = new UserDao();

    @SuppressWarnings("empty-statement")
    public boolean registerUser(String jsonUserRegister) {
        boolean isSuccess = false;

        UserConvertor userConvertor = new UserConvertor();
        UserDto userDto = userConvertor.convertJsonToObject(jsonUserRegister);

        UserMapper userMapper = new UserMapper();
        User user = userMapper.userDtoToUser(userDto);

        User isUserExist = userDao.getUserByUserName(user.getUsername());

        if (isUserExist == null) {
            if (userDao.save(user) == true) {
                OtpUtils otpUtils = new OtpUtils();
                String otp = otpUtils.generateOtp();
                LocalDateTime currentDateTime = LocalDateTime.now();
                System.out.println("Thời gian hiện tại là: " + currentDateTime);

                user.setOtp(otp);
                user.setCreateDateOtp(currentDateTime);
                if (userDao.update(user) == true) {
                    EmailUtils emailUtils = new EmailUtils();
                    emailUtils.sendEmail(user.getUsername(), otp);
                    isSuccess = true;
                }
            }
        } else {
            System.out.println("Username " + user.getUsername() + " đã tồn tại");
        }
        return isSuccess;
    }

    public boolean verifyOtp(String username, String otp) {
        boolean isVerify = false;
        User userCheck = userDao.getUserByUserName(username);
        if (userCheck != null) {
            LocalDateTime currentDateTime = LocalDateTime.now();

            // Tính thời gian chênh lệch
            Duration duration = Duration.between(userCheck.getCreateDateOtp(), currentDateTime);
            long minutesDiff = duration.toMinutes();

            System.out.println("Kiem tra user: " + userCheck.getOtp());

            if (minutesDiff <= 10 && userCheck.getOtp().equals(otp)) {
                userCheck.setIsActive(1);
                if (userDao.update(userCheck) == true) {
                    isVerify = true;
                }
            }
        }

        return isVerify;
    }

    public boolean reGenerateOtp(String username, String password) {
        boolean isReGenerate = false;
        User userCheck = userDao.getUserByUserName(username);
        if (userCheck != null && userCheck.getIsActive() == 0) {
            boolean match = false;

            MP5Utils mP5Utils = new MP5Utils();
            String pwdHash = mP5Utils.getMD5Hash(password);
            if (pwdHash.equals(userCheck.getPassword())) {
                match = true;
            }
            
            if(!match) {
                return false;
            }
            
            OtpUtils otpUtils = new OtpUtils();
            String otp = otpUtils.generateOtp();
            LocalDateTime currentDateTime = LocalDateTime.now();
            System.out.println("Thời gian hiện tại là: " + currentDateTime);

            userCheck.setOtp(otp);
            userCheck.setCreateDateOtp(currentDateTime);
            if (userDao.update(userCheck) == true) {
                EmailUtils emailUtils = new EmailUtils();
                emailUtils.sendEmail(userCheck.getUsername(), otp);
                isReGenerate = true;
            }
        }

        return isReGenerate;
    }

    public boolean checkLogin(String username, String password) {
        boolean isLogin = false;
        User userCheck = userDao.getUserByUserName(username);
        if (userCheck != null && userCheck.getIsActive() == 1) {
            MP5Utils mP5Utils = new MP5Utils();
            String pwdHash = mP5Utils.getMD5Hash(password);
            if (pwdHash.equals(userCheck.getPassword())) {
                isLogin = true;
            }
        }
        return isLogin;
    }

    public static void main(String[] args) {

        UserBus userBus = new UserBus();
        String jsonUserRegister = "{\n"
                + "            \"username\": \"lahai7744@gmail.com\",\n"
                + "            \"password\": \"123\";\n"
                + "            \"firstName\": \"Nguyễn Văn\",\n"
                + "            \"lastName\": \"C\",\n"
                + "            \"gender\": \"Nam\",\n"
                + "            \"birthday\": \"12/12/1999\"\n"
                + "        }";

        boolean res = userBus.registerUser(jsonUserRegister);
        System.out.println("Kiem tra register: " + res);

        boolean res1 = userBus.verifyOtp("lahai7744@gmail.com", "424873");
        System.out.println("Kiem tra verify otp: " + res1);

        boolean res2 = userBus.reGenerateOtp("lahai7744@gmail.com", "123");
        System.out.println("Kiem tra regen: " + res2);

        boolean res3 = userBus.checkLogin("lahai7744@gmail.com", "123");
        System.out.println("Kiem tra login: " + res3);
    }

}
