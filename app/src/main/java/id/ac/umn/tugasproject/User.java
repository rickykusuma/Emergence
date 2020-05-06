package id.ac.umn.tugasproject;

public class User {
    private String email;
    private String password;
    private String fullname;
    private String phone;
    private String address;

    public User() {

    }

    public User(String email, String password ){
        this.email = email;
        this.password = password;
    }


    public User(String email, String password, String fullname, String phone, String address) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.phone = phone;
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
