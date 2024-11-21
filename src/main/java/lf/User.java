package lf;

public class User {
    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Add a method to validate email domain
    public static boolean isValidDomain(String email) {
        return email.endsWith("@rajalakshmi.edu.in");
    }
}
