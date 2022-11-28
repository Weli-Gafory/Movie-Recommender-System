package clientServer.statistikuser;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedList;

public class StatistikUser {

    public static JSONArray getData(JSONObject kategorie, JSONObject cast, JSONObject filmlaenge, JSONArray nameScoreDate){
        JSONArray statisticData = new JSONArray()
                .put(getRankedActors(cast))
                .put(getRankedGenre(kategorie))
                .put(getLength(filmlaenge))
                .put(bestMovie(nameScoreDate));
        return statisticData;
    }

    public static JSONArray getRankedActors(JSONObject cast){
        LinkedList<Actors> castList = new LinkedList();
        String[] parts = cast.getString("casts").split(", ");

        for(int i=0;i<parts.length;i++){
            Actors a = new Actors(parts[i],1);
            if(castList.isEmpty()==true){
                castList.add(a);
            }
            else{
                for(int j=0;j<castList.size();j++){
                    if(castList.get(j).getName().equals(a.getName())){
                        castList.get(j).setCounter(castList.get(j).getCounter()+1);
                        j=castList.size();
                    }
                    else{
                        if(j==castList.size()-1){
                            castList.add(a);
                            j=castList.size();
                        }
                        }
                    }
                }
            }
        while(castList.size()<5){
            castList.add(new Actors("",0));
        }
        castList.sort(((o1, o2) -> o2.getCounter().compareTo(o1.getCounter())));

        JSONArray rankedList = new JSONArray();

        for(int i=0;i<=4;i++){
            JSONObject neu = new JSONObject().put("name",castList.get(i).getName());
            rankedList.put(neu);
        }

        return rankedList;
    }

    public static JSONArray getRankedGenre(JSONObject kategorie){
        LinkedList<Genre> genreList = new LinkedList();
        String[] parts = kategorie.getString("kategorien").split(", ");

        for(int i=0;i<parts.length;i++){
            Genre a = new Genre(parts[i],1);
            if(genreList.isEmpty()==true){
                genreList.add(a);
            }
            else{
                for(int j=0;j<genreList.size();j++){
                    if(genreList.get(j).getName().equals(a.getName())){
                        genreList.get(j).setCounter(genreList.get(j).getCounter()+1);
                        j=genreList.size();
                    }
                    else{
                        if(j==genreList.size()-1) {
                            genreList.add(a);
                            j = genreList.size();
                        }
                    }
                }
            }
        }
        while(genreList.size()<5){
            genreList.add(new Genre("",0));
        }
        genreList.sort(((o1, o2) -> o2.getCounter().compareTo(o1.getCounter())));

        JSONArray rankedList = new JSONArray();

        for(int i=0;i<=4;i++){
            JSONObject neu = new JSONObject().put("name",genreList.get(i).getName());
            rankedList.put(neu);
        }

        return rankedList;
    }

    public static JSONArray getLength(JSONObject filmlaenge){
        JSONArray movielength = new JSONArray().put(filmlaenge);
        return movielength;
    }

    public static JSONArray bestMovie(JSONArray movieAndScore){
        LinkedList<BestMovie> movieList = new LinkedList();
        JSONArray finalList = new JSONArray();

        for(int i=0; i<movieAndScore.length();i++) {
            JSONObject object = movieAndScore.getJSONObject(i);

            String name = object.getString("name");
            Integer bewertung = object.getInt("score");

            BestMovie neu = new BestMovie(name,bewertung);

            movieList.add(neu);
        }
        while(movieList.size()<5){
            movieList.add(new BestMovie("",0));
        }

        movieList.sort(((o1, o2) -> o2.getBewertung().compareTo(o1.getBewertung())));

        for(int i=0;i<=4;i++){
            JSONObject neu = new JSONObject().put("name",movieList.get(i).getName());
            finalList.put(neu);

        }
        return finalList;
    }
    public static class Actors {
        private String name;
        private int counter;
        public Actors(String name, Integer counter){
            super();
            this.name=name;
            this.counter=counter;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCounter() {
            return counter;
        }

        public void setCounter(Integer counter) {
            this.counter = counter;
        }
    }
    public static class BestMovie {
        private String name;
        private int bewertung;
        public BestMovie(String name, Integer bewertung){
            super();
            this.name=name;
            this.bewertung=bewertung;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getBewertung() {
            return bewertung;
        }

        public void setBewertung(Integer bewertung) {
            this.bewertung = bewertung;
        }
    }
    public static class Genre {
        private String name;
        private int counter;
        public Genre(String name, Integer counter){
            super();
            this.name=name;
            this.counter=counter;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCounter() {
            return counter;
        }

        public void setCounter(Integer counter) {
            this.counter = counter;
        }
    }

}
