package untad.aldochristopherleo.emergence;

public class Post {
    private String posLat, posLong, desc, email, telp;

    public Post() {
    }

    public Post(String posLat, String posLong, String desc, String email, String telp) {
        this.posLat = posLat;
        this.posLong = posLong;
        this.desc = desc;
        this.email = email;
        this.telp = telp;
    }

    public String getPosLat() {
        return posLat;
    }

    public void setPosLat(String posLat) {
        this.posLat = posLat;
    }

    public String getPosLong() {
        return posLong;
    }

    public void setPosLong(String posLong) {
        this.posLong = posLong;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }
}
