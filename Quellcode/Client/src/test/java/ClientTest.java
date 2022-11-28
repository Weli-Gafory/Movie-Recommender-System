import com.google.gson.JsonParser;
import masterblaster.Main;
import masterblaster.clientServer.Client;
import masterblaster.models.Einladung;
import masterblaster.models.Film;
import masterblaster.models.Nutzer;

import masterblaster.models.RecommendedMovies;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.commons.lang3.Range;

import java.util.ArrayList;


class ClientTest {
    //hat noch keinen film angesehen und bewertet
    static Nutzer n17 = new Nutzer(17, "", "", "", "", "", "");  //

    static Nutzer n18 = new Nutzer(17, "", "", "", "", "", "");  //



    static Nutzer n1 = new Nutzer(1, "Angela", "Merkel", "angela@merkel.de", "angela@merkel.de", "1959-02-31", "./.profilepics/merkel.jpg");  //existiert in DB
    static Nutzer n2 = new Nutzer(300, "Angela", "Merkel", "angela@merkel.de", "angela@merkel.de", "1959-02-31", "./.profilepics/merkel.jpg");  //existiert nicht in DB
    static Nutzer n3 = new Nutzer(2, "Angela", "Merkel", "angela@merkel.de", "angela@merkel.de", "1959-02-31", "./.profilepics/merkel.jpg");  //existiert in DB
    static Film f1 = new Film(6, "", "", "", "", "", "", "n", ".");;    //existiert in DB
    static Film f2 = new Film(300, "", "", "", "", "", "", "", ""); //existiert nicht in DB

    static Film f3 = new Film(1, "", "", "", "", "", "", "", "");;    //existiert in DB
    static Film f4 = new Film(3, "", "", "", "", "", "", "", "");;    //existiert in DB
    static Film f5 = new Film(14, "", "", "", "", "", "", "", "");;    //existiert in DB
    static Film f6 = new Film(19, "", "", "", "", "", "", "n", "");;    //existiert in DB
    static Film f7 = new Film(10, "", "", "", "", "", "", "n", "");;    //existiert in DB
    static Film f8 = new Film(11, "", "", "", "", "", "", "n", "");;    //existiert in DB
    static Film f9 = new Film(12, "", "", "", "", "", "", "n", ".");;    //existiert in DB
    static Film f10 = new Film(13, "", "", "", "", "", "", "n", ".");;    //existiert in DB
    static Film f11= new Film(20, "", "", "", "", "", "", "n", ".");;    //existiert in DB
    static Film f12= new Film(15, "", "", "", "", "", "", "n", ".");;    //existiert in DB



    static Client c;
    @BeforeAll
    static void initialAll() throws Exception {

        c = new Client();
        try {
            c.addFilmToWatchlist(n1, f1);
        } catch(Exception e){}
        try{
            c.deleteFromSeenList(n1, f1);
        } catch(Exception e){}
    }

    @Test
    void filmGesehenMitFalscherFilmID() throws Exception{
        Exception e = Assertions.assertThrows(Exception.class, () -> c.filmGesehen(n1, f2));
        Assertions.assertEquals("Ein Fehler ist aufgetreten", e.getMessage());
    }

    @Test
    void filmGesehenMitFalscherNutzerID() throws Exception{
        Exception e = Assertions.assertThrows(Exception.class, () -> c.filmGesehen(n2, f1));
        Assertions.assertEquals("Ein Fehler ist aufgetreten", e.getMessage());
    }

    @Test
    void filmGesehenKorrekt() throws Exception {
        JSONArray a = c.getAllFilmsInSeenList(n1);
        int expected = a.length() + 1;
        c.filmGesehen(n1, f1);
        int actual = c.getAllFilmsInSeenList(n1).length();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void filmVonWatchlistLoeschenKorrekt() throws Exception {
        JSONArray a = c.getAllFilmsInWatchlist(n1);
        int expected = a.length() - 1;
        c.deleteFilmFromWatchlist(n1, f1);
        int actual = c.getAllFilmsInSeenList(n1).length();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void addBewertungKorrekt() throws Exception{
        JSONArray a = c.getAllReviews(f1);
        int expected = a.length() + 1;
        c.addNewReview(n1, f1, "titel", 1, "text");
        int actual = c.getAllReviews(f1).length();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void addBewertungFalscheFilmID(){
        Exception e = Assertions.assertThrows(Exception.class, () -> c.addNewReview(n1, f2, "titel", 4, "text"));
        Assertions.assertEquals("Bewertung konnte nicht gespeichert werden, I AM SORRY", e.getMessage());
    }

    @Test
    void addBewertungFalscheNutzerID(){
        Exception e = Assertions.assertThrows(Exception.class, () -> c.addNewReview(n2, f1, "titel", 4, "text"));
        Assertions.assertEquals("Bewertung konnte nicht gespeichert werden, I AM SORRY", e.getMessage());
    }

    @Test
    void editBewertungFalscheBewertungsID(){
        Exception e = Assertions.assertThrows(Exception.class, () -> c.editReview(600, "neu", 3, "anders"));
        Assertions.assertEquals("fehlgeschlagen", e.getMessage());
    }

    @Test
    void insertFilm() throws Exception {
        JSONArray aktuell =c.getAllFilms();
        System.out.println("Anzahl der Filme vor dem Test :"+ aktuell.length());

        int expected = aktuell.length()+1 ;
        c.addNewFilmAction(new Film("living to tell the tale ","biography","",""," Gabriel García Márquez","","",""));
        JSONArray actuall = c.getAllFilms();
        System.out.println("Anzahl der Filme nachdem dem Test :"+ actuall.length());

        Assertions.assertEquals(expected ,actuall.length());
    }

    //Statistik mit der ID muss existieren

    @Test
    void filmStatistikZuruecksetzen() throws Exception {
        c.resetFilmstatistik(6);
        JSONArray actual = c.getFilmstatistik(6);
        String expected = "[{\"avgBewertung\":\"0\",\"anzahlGesehen\":\"0\",\"anzahlBewertung\":\"0\"}]";
        Assertions.assertEquals(expected, actual.toString());
    }

    @Test
    void filmEinladungVerschicken() throws Exception{
        c.setNutzer(n1);
        int expectedAnzahlEinladungen = c.getAllInvitations(n3).size() + 1;
        c.addNewEinladung(n1, n3, "Kill Bill", "Einladung", "2022-07-11", "12:30");
        int actual = c.getAllInvitations(n3).size();
        Assertions.assertEquals(expectedAnzahlEinladungen, actual);
        //Einladung manuell aus DB löschen!
    }
    @Test
    void keineVorgelaegeneFilme() throws Exception {

        JSONArray result = c.getRecommendedMovedBasedOnMySeenList( n17);
        System.out.println( " actuall is  0 ,"+"expected is : "+result .length() );

        Assertions.assertEquals(0, result.length());

    }

    @Test
    void vorgelaegeneFilme() throws Exception {
        helperMthode();

        JSONArray result = c.getRecommendedMovedBasedOnMySeenList(n17);

        Range<Integer> range = Range.between(1, 15);
        boolean actuall = false;

        if(range.contains(result.length())){
            actuall =true;
            Assertions.assertEquals(true,actuall);

        }
        else{
            Assertions.assertEquals(true,actuall);

        }

        System.out.println(result.length());
    }
    static void helperMthode() throws Exception {

        c.filmGesehen(n17,f3);
        c.filmGesehen(n17,f4);
        c.filmGesehen(n17,f5);
        c.filmGesehen(n17,f6);
        c.filmGesehen(n17,f7);
        c.filmGesehen(n17,f8);
        c.filmGesehen(n17,f9);
        c.filmGesehen(n17,f10);
        c.filmGesehen(n17,f11);
        c.filmGesehen(n17,f12);


        c.addNewReview(n17,f3,"",4,"");
        c.addNewReview(n17,f4,"",4,"");
        c.addNewReview(n17,f5,"",4,"");
        c.addNewReview(n17,f6,"",4,"");
        c.addNewReview(n17,f7,"",4,"");
        c.addNewReview(n17,f8,"",4,"");
        c.addNewReview(n17,f9,"",4,"");
        c.addNewReview(n17,f10,"",4,"");
        c.addNewReview(n17,f11,"",4,"");
        c.addNewReview(n17,f12,"",4,"");

    }
    @Test
    void durchFreundsVorgelaegeneFilme() throws Exception {

        JSONArray result = c.getRecommendedMovedBasedOnFriendSeenList(n1);
        Range<Integer> range = Range.between(1, 15);
        boolean actuall = false;

        if(range.contains(result.length())){
            actuall =true;
            Assertions.assertEquals(true,actuall);

        }
        else{
            Assertions.assertEquals(true,actuall);

        }

    }


    }