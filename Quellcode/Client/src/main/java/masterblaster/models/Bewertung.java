package masterblaster.models;

public class Bewertung {
    private String text;
    private String titel;
    private int bewertung;

    private Integer bewertungId;

    public Bewertung(Integer bewertungId, String text, String titel, int bewertung) {
        this.bewertungId = bewertungId;
        this.text = text;
        this.titel = titel;
        this.bewertung = bewertung;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public double getBewertung() {
        return bewertung;
    }

    public void setBewertung(int bewertung) {
        this.bewertung = bewertung;
    }

    public Integer getBewertungId() {
        return bewertungId;
    }

    public void setBewertungId(Integer bewertungId) {
        this.bewertungId = bewertungId;
    }
}
