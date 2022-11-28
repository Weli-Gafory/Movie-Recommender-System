package masterblaster.models;


import com.google.gson.JsonObject;

public abstract class Obermensch {

    private Integer id;
    private String name;
    private String surname;
    private String mail;
    private String password;

    @Override
    public String toString() {
        return "Obermensch{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String toJsonString() {
        JsonObject json = new JsonObject();
        if (this.getId() != null)
            json.addProperty("id", this.getId());
        if (this.getName() != null)
            json.addProperty("name", this.getName());
        if (this.getSurname() != null)
            json.addProperty("surname", this.getSurname());
        if (this.getMail() != null)
            json.addProperty("mail", this.getMail());
        if (this.getPassword() != null)
            json.addProperty("password", this.getPassword());

        return json.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
