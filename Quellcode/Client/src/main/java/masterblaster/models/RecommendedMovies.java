package masterblaster.models;

public class RecommendedMovies {
    private String name;
    private String kategory;
    private String director;
    private String aehnlichkeitsScore;


    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getKategory() {
        return kategory;
    }

    public void setKategory(String kategory) {
        this.kategory = kategory;
    }

    public String getAehnlichkeitsScore() {
        return aehnlichkeitsScore;
    }

    public void setAehnlichkeitsScore(String aehnlichkeitsScore) {
        this.aehnlichkeitsScore = aehnlichkeitsScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecommendedMovies(String name , String kategory, String director, String aehnlichkeitsScore) {
        this.name =name;
        this.kategory = kategory;
        this.director = director;
        this.aehnlichkeitsScore = aehnlichkeitsScore;
    }

}
