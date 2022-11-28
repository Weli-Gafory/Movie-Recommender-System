package models;

import com.google.gson.JsonObject;
import org.json.JSONObject;

public class Nutzer extends Obermensch{

    private String birthdate;
    private String profilepic;

    public Nutzer(String name, String surname, String mail, String password, String birthdate, String profilepic){
        this.setName(name);
        this.setSurname(surname);
        this.setMail(mail);
        this.setPassword(password);
        this.setBirthdate(birthdate);
        this.setProfilepic(profilepic);
    }

    public Nutzer(Integer id, String name, String surname, String mail, String password, String birthdate, String profilepic){
        this.setId(id);
        this.setName(name);
        this.setSurname(surname);
        this.setMail(mail);
        this.setPassword(password);
        this.setBirthdate(birthdate);
        this.setProfilepic(profilepic);
    }


    public Nutzer(JsonObject json){
        if (json.has("id"))
            this.setId(json.get("id").getAsInt());
        if (json.has("name"))
            this.setName(json.get("name").getAsString());
        if (json.has("surname"))
            this.setSurname(json.get("surname").getAsString());
        if (json.has("mail"))
            this.setMail(json.get("mail").getAsString());
        if (json.has("password"))
            this.setPassword(json.get("password").getAsString());
        if (json.has("birthdate"))
            this.setBirthdate(json.get("birthdate").getAsString());
        if (json.has("profilepic"))
            this.setProfilepic(json.get("profilepic").getAsString());
    }

    public Nutzer(JSONObject json){
        if (json.has("id"))
            this.setId(json.getInt("id"));
        if (json.has("name"))
            this.setName(json.get("name").toString());
        if (json.has("surname"))
            this.setSurname(json.get("surname").toString());
        if (json.has("mail"))
            this.setMail(json.get("mail").toString());
        if (json.has("password"))
            this.setPassword(json.get("password").toString());
        if (json.has("birthdate"))
            this.setBirthdate(json.get("birthdate").toString());
        if (json.has("profilepic"))
            this.setProfilepic(json.get("profilepic").toString());
    }

    @Override
    public String toString() {
        return "Nutzer{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", surname='" + this.getSurname() + '\'' +
                ", mail='" + this.getMail() + '\'' +
                ", password='" + this.getPassword() + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", profilepic='" + profilepic + '\'' +
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
        if (this.getBirthday() != null)
            json.addProperty("birthdate", this.getBirthday());
        if (this.getProfilepic() != null)
            json.addProperty("profilepic", this.getProfilepic());

        return json.toString();
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        if (this.getId() != null)
            json.put("id", this.getId());
        if (this.getName() != null)
            json.put("name", this.getName());
        if (this.getSurname() != null)
            json.put("surname", this.getSurname());
        if (this.getMail() != null)
            json.put("mail", this.getMail());
        if (this.getPassword() != null)
            json.put("password", this.getPassword());
        if (this.getBirthday() != null)
            json.put("birthdate", this.getBirthday());
        if (this.getProfilepic() != null)
            json.put("profilepic", this.getProfilepic());

        return json;
    }

    public String getBirthday(){
        return birthdate;
    }

    public void setBirthdate(String birthdate){
        this.birthdate = birthdate;
    }

    public String getProfilepic(){
        return profilepic;
    }

    public void setProfilepic(String profilepic){
        this.profilepic = profilepic;
    }

}
