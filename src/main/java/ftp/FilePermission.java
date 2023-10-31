package ftp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilePermission {
    private String path;
    private boolean isReadable = false; 
    private boolean isWritable = false;
    private boolean isDeletable = false;
    private boolean isRenamable = false;
    private String owner;
    private String appliedUser;
    
    public boolean isOwner() {
        return owner.equals(appliedUser);
    }
}
