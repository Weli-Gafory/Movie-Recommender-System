package masterblaster.models;

import com.google.gson.JsonObject;

public class Film {

    private Integer id;
    private String Name;
    private String Kategorie;
    private String Erscheinungsdatum;
    private String Regisseur;
    private String Drehbuchautor;
    private String Cast;
    private String Filmlaenge;

    private String filmbanner;

    private int avgBewertung;

    public Film(String name, String kategorie, String erscheinungsdatum, String regisseur, String drehbuchautor, String cast, String filmlaenge, String filmbanner) {
        this.Name = name;
        this.Kategorie = kategorie;
        this.Erscheinungsdatum = erscheinungsdatum;
        this.Regisseur = regisseur;
        this.Drehbuchautor = drehbuchautor;
        this.Cast = cast;
        this.Filmlaenge = filmlaenge;
        this.filmbanner = filmbanner;
        avgBewertung = 0;   //default
    }

    public Film(Integer id, String name, String kategorie, String erscheinungsdatum, String regisseur, String drehbuchautor, String cast, String filmlaenge, String filmbanner) {
        this(name, kategorie, erscheinungsdatum, regisseur, drehbuchautor, cast, filmlaenge, filmbanner);
        this.id = id;
    }

    public Film() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public String getKategorie() {
        return Kategorie;
    }


    public void setKategorie(String kategorie) {
        Kategorie = kategorie;
    }


    public String getErscheinungsdatum() {
        return Erscheinungsdatum;
    }


    public void setErscheinungsdatum(String erscheinungsdatum) {
        Erscheinungsdatum = erscheinungsdatum;
    }


    public String getRegisseur() {
        return Regisseur;
    }


    public void setRegisseur(String regisseur) {
        Regisseur = regisseur;
    }


    public String getDrehbuchautor() {
        return Drehbuchautor;
    }


    public void setDrehbuchautor(String drehbuchautor) {
        Drehbuchautor = drehbuchautor;
    }


    public String getCast() {
        return Cast;
    }


    public void setCast(String cast) {
        Cast = cast;
    }


    public String getFilmlaenge() {
        return Filmlaenge;
    }


    public void setFilmlaenge(String i) {
        Filmlaenge = i;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilmbanner(){
        return filmbanner;
    }

    public void setFilmbanner(String filmbanner){
        this.filmbanner = filmbanner;
    }

    public int getAvgBewertung(){
        return avgBewertung;
    }

    public void setAvgBewertung(int b){
        avgBewertung = b;
    }


    @Override
    public String toString() {
        return "Film [Name=" + Name + ", Kategorie=" + Kategorie + ", Erscheinungsdatum=" + Erscheinungsdatum
                + ", Regisseur=" + Regisseur + ", Drehbuchautor=" + Drehbuchautor + ", Cast=" + Cast + ", Filmlaenge="
                + Filmlaenge + ", Filmbanner="
                + filmbanner + ", AvgBewertung="
                + avgBewertung+"]";
    }

    public String toJsonString() {
        JsonObject json = new JsonObject();
        if (this.getId() != null)
            json.addProperty("id", this.getId());
        if (this.getName() != null)
            json.addProperty("Name", this.getName());
        if (this.getKategorie() != null)
            json.addProperty("Kategorie", this.getKategorie());
        if (this.getErscheinungsdatum() != null)
            json.addProperty("Erscheinungsdatum", this.getErscheinungsdatum());
        if (this.getRegisseur() != null)
            json.addProperty("Regisseur", this.getRegisseur());
        if (this.getDrehbuchautor() != null)
            json.addProperty("Drehbuchautor", this.getDrehbuchautor());
        if (this.getCast() != null)
            json.addProperty("Cast", this.getCast());
        if (this.getFilmlaenge() != null)
            json.addProperty("Filmlaenge", this.getFilmlaenge());
        if (this.getFilmbanner() != null)
            json.addProperty("Filmbanner", this.getFilmbanner());

        json.addProperty("AvgBewertung", this.getAvgBewertung());

        return json.toString();
    }

    public Film(JsonObject json) {
        if (json.has("id"))
            this.setId(json.get("id").getAsInt());
        if (json.has("Name"))
            this.setName(json.get("Name").getAsString());
        if (json.has("Kategorie"))
            this.setKategorie(json.get("Kategorie").getAsString());
        if (json.has("Erscheinungsdatum"))
            this.setErscheinungsdatum(json.get("Erscheinungsdatum").getAsString());
        if (json.has("Regisseur"))
            this.setRegisseur(json.get("Regisseur").getAsString());
        if (json.has("Drehbuchautor"))
            this.setDrehbuchautor(json.get("Drehbuchautor").getAsString());
        if (json.has("Cast"))
            this.setCast(json.get("Cast").getAsString());
        if (json.has("Filmlaenge"))
            this.setFilmlaenge(json.get("Filmlaenge").getAsString());
        if (json.has("Filmbanner"))
            this.setFilmbanner(json.get("Filmbanner").getAsString());
        if (json.has("AvgBewertung"))
            this.setAvgBewertung(json.get("AvgBewertung").getAsInt());
    }
}