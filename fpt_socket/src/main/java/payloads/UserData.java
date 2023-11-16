/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package payloads;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Son
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    private String username;
    private String firstName;
    private String lastName;
    private Date birthdate;
    private String gender;
    private boolean anonymous;
    private boolean isBlockDownload;
    private boolean isBlockUpload;
    private long maxUploadSizeBytes;
    private long maxDownloadSizeBytes;
    private long quotaInBytes;
    private long usedBytes;
}
