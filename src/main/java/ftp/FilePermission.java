package ftp;

public record FilePermission(boolean isReadable, boolean isWritable, boolean isDeletable, boolean isRenamable) {}
