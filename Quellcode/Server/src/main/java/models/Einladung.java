package models;

import com.google.gson.JsonObject;
import org.json.JSONObject;

public class Einladung {
    private String text;
    private String filmName;
    private String datum;
    private String uhrzeit;
    private Integer einladungsId;

    private Integer idNo1;

    private Integer idNo2;

    private String vorname;
    private String nachname;

    public Einladung(Integer idNo1, Integer idNo2, String vorname, String nachname, String text, String filmName, String datum, String uhrzeit){
        this.idNo1 = idNo1;
        this.idNo2 = idNo2;
        this.vorname = vorname;
        this.nachname = nachname;
        this.text = text;
        this.filmName = filmName;
        this.datum = datum;
        this.uhrzeit = uhrzeit;
    }

    public Einladung(JsonObject json){
        if (json.has("idNo1"))
            this.setIdNo1(json.get("idNo1").getAsInt());
        if (json.has("idNo2"))
            this.setIdNo2(json.get("idNo2").getAsInt());
        if (json.has("vorname"))
            this.setVorname(json.get("vorname").getAsString());
        if (json.has("nachname"))
            this.setNachname(json.get("nachname").getAsString());
        if (json.has("filmname"))
            this.setFilmName(json.get("filmname").getAsString());
        if (json.has("datum"))
            this.setDatum(json.get("datum").getAsString());
        if (json.has("uhrzeit"))
            this.setUhrzeit(json.get("uhrzeit").getAsString());
        if (json.has("nachricht"))
            this.setText(json.get("nachricht").getAsString());
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        if (this.getIdNo1() != null)
            json.put("idNo1", this.getIdNo1());
        if (this.getIdNo2() != null)
            json.put("idNo2",this.getIdNo2());
        if (this.getVorname() != null)
            json.put("vorname",this.getVorname());
        if (this.getNachname() != null)
            json.put("nachname", this.getNachname());
        if (this.getFilmName() != null)
            json.put("filmname", this.getFilmName());
        if (this.getDatum() != null)
            json.put("datum",this.getDatum());
        if (this.getUhrzeit() != null)
            json.put("uhrzeit",this.getUhrzeit());
        if (this.getText() != null)
            json.put("nachricht", this.getText());
        return json;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public Integer getEinladungsId() {
        return einladungsId;
    }

    public void setEinladungsId(Integer einladungsId) {
        this.einladungsId = einladungsId;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getUhrzeit() {
        return uhrzeit;
    }

    public void setUhrzeit(String uhrzeit) {
        this.uhrzeit = uhrzeit;
    }

    public Integer getIdNo1() {
        return idNo1;
    }

    public void setIdNo1(Integer idNo1) {
        this.idNo1 = idNo1;
    }

    public Integer getIdNo2() {
        return idNo2;
    }

    public void setIdNo2(Integer idNo2) {
        this.idNo2 = idNo2;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }
}
