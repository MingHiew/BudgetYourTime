package ch.supsi.minhhieu.budgetyourtime.Models;


/**
 * Created by acer on 12/07/2016.
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String thumbURL;

    public User() {
    }

    public User(int id, String userName, String userEmail, String thumbURL) {
        this.id = id;
        this.username = userName;
        this.email = userEmail;
        this.thumbURL = thumbURL;
    }

    public User(String userName, String userEmail, String thumbURL) {
        this.username = userName;
        this.email = userEmail;
        this.thumbURL = thumbURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getUserEmail() {
        return email;
    }

    public void setUserEmail(String userEmail) {
        this.email = userEmail;
    }

    public String getUserPhoto() {
        return thumbURL;
    }

    public void setUserPhoto(String userPhoto) {
        this.thumbURL = userPhoto;
    }
}