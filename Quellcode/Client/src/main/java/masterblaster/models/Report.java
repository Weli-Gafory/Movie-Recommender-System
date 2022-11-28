package masterblaster.models;

public class Report {
    private int id;
    private String titel;
    private String beschreibung;

    public Report(int id, String titel, String beschreibung) {
        this.id = id;
        this.titel = titel;
        this.beschreibung = beschreibung;
    }

    public int getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }
}
