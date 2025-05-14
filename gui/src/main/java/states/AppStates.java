package states;


import java.util.ArrayList;
import model.FileModel;

public class AppStates {
    public static boolean logIn = false;
    public static String username;
    public static ArrayList<FileModel> files = new ArrayList<>();

    public static String getCurrentUserName() {
        return username;
    }

    public static void setCurrentUser(String user) {
        username = user;
    }

    public static ArrayList<FileModel> getFiles() {
        return files;
    }

    public static void setFiles(ArrayList<FileModel> fileList) {
        files = fileList;
    }

    public static boolean getLogIn () {
        return logIn;
    }

    public static void isLogIn (boolean state) {
        logIn = state;
    }
}
