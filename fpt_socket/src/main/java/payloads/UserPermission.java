/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package payloads;

import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Bum
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPermission {
    private String fileType;
    private UserData userData;
    private HashMap<String,Boolean> permission;
}
