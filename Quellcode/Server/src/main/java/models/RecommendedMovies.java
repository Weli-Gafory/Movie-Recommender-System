package models;

public class RecommendedMovies {
    private String name;
    private String kategory;
    private String Director;
    private String aehnlichkeitsScore;

    @Override
    public String toString() {
        return "RecommendedMovies{" +
                "name='" + name + '\'' +
                ", kategory='" + kategory + '\'' +
                ", Director='" + Director + '\'' +
                ", aehnlichkeitsScore='" + aehnlichkeitsScore + '\'' +
                '}';
    }

    public String getKategory() {
        return kategory;
    }

    public void setKategory(String kategory) {
        this.kategory = kategory;
    }

    public String getDirector() {
        return Director;
    }

    public void setDirector(String director) {
        Director = director;
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

    public RecommendedMovies(String name , String kategory, String director, String matchPercentage) {
        this.name =name;
        this.kategory = kategory;
        Director = director;
        this.aehnlichkeitsScore = matchPercentage;
    }
}
