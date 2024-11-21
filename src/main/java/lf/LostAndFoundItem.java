package lf;

public class LostAndFoundItem {
    private String rollNo;
    private String name;
    private String contact;
    private String location;
    private String photoPath;

    public LostAndFoundItem(String rollNo, String name, String contact, String location, String photoPath) {
        this.rollNo = rollNo;
        this.name = name;
        this.contact = contact;
        this.location = location;
        this.photoPath = photoPath;
    }

    // Getters and Setters
    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
