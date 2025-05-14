package model;

import java.io.Serializable;

public class FileRecord implements Serializable {
    private int fileID;
    private String fileName;
    private String uploadDate;

    public FileRecord(int fileID, String fileName, String uploadDate) {
        this.fileID = fileID;
        this.fileName = fileName;
        this.uploadDate = uploadDate;
    }

    // Getters and Setters
    public int getFileID() {
        return fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    @Override
    public String toString() {
        return "FileRecord{" +
               "fileID=" + fileID +
            ", fileName='" + fileName + '\'' +
            ", uploadDate='" + uploadDate + '\'' +
            '}';
    }
}
