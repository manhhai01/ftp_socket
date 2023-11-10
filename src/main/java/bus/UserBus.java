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
import payload.UserFileLimits;
import utils.EmailUtils;
import utils.MP5Utils;
import utils.OtpUtils;

/**
 *
 * @author lamanhhai
 */
public class UserBus {

    public static final String LOGIN_PASSWORD_MISMATCH_MSG = "Đăng nhập thất bại";
    public static final String LOGIN_ACCOUNT_NOT_VERIFIED_MSG = "Tài khoản chưa xác thực";
    public static final String LOGIN_SUCCESS_MSG = "";
    public static final String SET_FILE_LIMITS_USER_NOT_FOUND = "Không tìm thấy user";
    public static final String SET_FILE_LIMITS_QUOTA_SMALLER_THAN_USED_KB = "Dung lượng tối đa không được nhỏ hơn dung lượng hiện tại của thư mục";
    public static final String SET_FILE_LIMITS_SUCCESSFULLY = "Thành công";

    private final UserDao userDao = new UserDao();
    private final DirectoryBus directoryBus = new DirectoryBus();
    
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

    public boolean verifyOtp(String username, String password, String otp) {
        boolean isVerify = false;
        User userCheck = userDao.getUserByUserName(username);
        if (userCheck != null) {
            boolean match = false;

            MP5Utils mP5Utils = new MP5Utils();
            String pwdHash = mP5Utils.getMD5Hash(password);
            if (pwdHash.equals(userCheck.getPassword())) {
                match = true;
            }

            if (!match) {
                return false;
            }
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

            if (!match) {
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

    public String checkLogin(String username, String password) {
        String message = "";
        User userCheck = userDao.getUserByUserName(username);
//        if (userCheck != null) {
//            if(userCheck.getIsActive() == 0) {
//                return "Tài khoản chưa xác thực";
//            } else {
//                MP5Utils mP5Utils = new MP5Utils();
//                String pwdHash = mP5Utils.getMD5Hash(password);
//                if (pwdHash.equals(userCheck.getPassword()) && userCheck.getIsActive() == 1) {
//                   return "Đăng nhập thành công"; 
//                }
//            }
//            
        if (userCheck == null) {
            return LOGIN_PASSWORD_MISMATCH_MSG;
        }
        MP5Utils mP5Utils = new MP5Utils();
        String pwdHash = mP5Utils.getMD5Hash(password);
        if (!pwdHash.equals(userCheck.getPassword())) {
            return LOGIN_PASSWORD_MISMATCH_MSG;
        }
        if (userCheck.getIsActive() == 0) {
            return LOGIN_ACCOUNT_NOT_VERIFIED_MSG;
        }
        return LOGIN_SUCCESS_MSG;
    }

    public String setFileLimits(String username, UserFileLimits limits) {
        User user = userDao.getUserByUserName(username);
        if (user == null) {
            return SET_FILE_LIMITS_USER_NOT_FOUND;
        }
        
        if (limits.getMaxDownloadFileSizeKb() != null) {
            user.setMaxDownloadFileSizeKb(limits.getMaxDownloadFileSizeKb());
        }
        
        if (limits.getMaxUploadFileSizeKb() != null) {
            user.setMaxUploadFileSizeKb(limits.getMaxUploadFileSizeKb());
        }
        
        if(limits.getQuotaKb() != null) {
            int quotaKb = limits.getQuotaKb();
            if(quotaKb < user.getUsedKb()) {
                return SET_FILE_LIMITS_QUOTA_SMALLER_THAN_USED_KB;
            }
            
            user.setQuotaInKb(quotaKb);          
        }
        
        userDao.update(user);
        return SET_FILE_LIMITS_SUCCESSFULLY;
    }

    public static void main(String[] args) {

        UserBus userBus = new UserBus();
        String jsonUserRegister = "{\n"
                + "            \"username\": \"lequoctai201201@gmail.com\",\n"
                + "            \"password\": \"123\";\n"
                + "            \"firstName\": \"Nguyễn Văn\",\n"
                + "            \"lastName\": \"C\",\n"
                + "            \"gender\": \"Nam\",\n"
                + "            \"birthday\": \"12/12/1999\"\n"
                + "        }";

        boolean res = userBus.registerUser(jsonUserRegister);
        System.out.println("Kiem tra register: " + res);

        boolean res1 = userBus.verifyOtp("lahai7744@gmail.com", "123", "424873");
        System.out.println("Kiem tra verify otp: " + res1);

        boolean res2 = userBus.reGenerateOtp("lahai7744@gmail.com", "123");
        System.out.println("Kiem tra regen: " + res2);

        String res3 = userBus.checkLogin("lequoctai201201@gmail.com", "123");
        System.out.println("Kiem tra login: " + res3);
    }

}
