package masterblaster.models;

import com.google.gson.JsonObject;

public class Systemadministrator extends Obermensch {

    public Systemadministrator(String name, String surname, String mail, String password) {
        this.setName(name);
        this.setSurname(surname);
        this.setMail(mail);
        this.setPassword(password);
    }

    public Systemadministrator(Integer id, String name, String surname, String mail, String password) {
        Systemadministrator sysAdmin = new Systemadministrator(name, surname, mail, password);
        this.setId(id);
    }

    public Systemadministrator(JsonObject json) {
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
    }

}
