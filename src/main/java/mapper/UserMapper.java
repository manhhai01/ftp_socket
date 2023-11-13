/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mapper;

import java.util.Date;
import model.User;
import payload.UserDto;
import utils.DateUtils;

/**
 *
 * @author lamanhhai
 */
public class UserMapper {
    public User userDtoToUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setGender(userDto.getGender());
        DateUtils dateUtils = new DateUtils();
        Date birthday = new Date(dateUtils.converDateToLong(userDto.getBirthday()));
        user.setBirthdate(birthday);
        user.setMaxDownloadFileSizeBytes(300000 * 1000); // 300,000 KB = 300 KB
        user.setMaxUploadFileSizeBytes(300000 * 1000); // 300,000 KB = 300 MB
        user.setQuotaInBytes(1000000 * 10000); // 1,000,000 KB = 1 GB
        user.setAnonymous(false);
        user.setUsedBytes(0);
        user.setIsActive(0);
        user.setBlock(false);
        user.setBlockUpdown(false);
        return user;
    }
}
