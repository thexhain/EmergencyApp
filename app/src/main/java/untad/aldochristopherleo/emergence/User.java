package untad.aldochristopherleo.emergence;

public class User {
    public String email, nama, nik, tipe, telp, token;

    public User() {
    }

    public User(String email, String nama, String nik, String tipe, String telp, String token) {
        this.email = email;
        this.nama = nama;
        this.nik = nik;
        this.tipe = tipe;
        this.telp = telp;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
