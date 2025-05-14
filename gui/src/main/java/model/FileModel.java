package model;

import java.sql.Timestamp;

public class FileModel {
    private int id;
    private int userId;
    private String nomFichier;
    private Timestamp dateUpload;


    public FileModel(int id, int userId, String nomFichier, Timestamp dateUpload) {
        this.id = id;
        this.userId = userId;
        this.nomFichier = nomFichier;
        this.dateUpload = dateUpload;
    }

    public FileModel(int userId, String nomFichier) {
        this.userId = userId;
        this.nomFichier = nomFichier;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public Timestamp getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(Timestamp dateUpload) {
        this.dateUpload = dateUpload;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "id=" + id +
                ", userId=" + userId +
                ", nomFichier='" + nomFichier + '\'' +
                ", dateUpload=" + dateUpload +
                '}';
    }
}
