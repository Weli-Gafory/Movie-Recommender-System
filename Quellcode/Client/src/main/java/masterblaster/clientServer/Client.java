package masterblaster.clientServer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import masterblaster.controller.EinladungController;
import masterblaster.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class Client {

    private Obermensch mensch;

    private Nutzer nutzer;

    private Nutzer fremdNutzer;

    private Film filmToBeUpdated;

    static String url = "http://localhost:1999/";

    static HttpClient client = HttpClient.newHttpClient();

    public JSONArray getAllFilms() throws IOException, InterruptedException {
        try {
            HttpResponse<String> response = this.getRequest("getmovies");
            return new JSONArray(response.body());
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public Systemadministrator loginAction(JSONObject credentials) throws Exception {
        HttpResponse<String> response = postRequest("login", credentials.toString());

        if (response.statusCode() == 200) {
            System.out.println("User found");
            return new Systemadministrator(JsonParser.parseString(response.body()).getAsJsonObject());

        } else {
            throw new Exception("User not found");
        }
    }

    public Systemadministrator registerAction(Systemadministrator newUserData) throws Exception {
        HttpResponse<String> response = postRequest("register", newUserData.toJsonString());

        if (response.statusCode() == 200) {
            System.out.println("New user registered successful");
            return new Systemadministrator(JsonParser.parseString(response.body()).getAsJsonObject());
        } else {
            throw new Exception("User couldn't be registered");
        }
    }

    public void addNewFilmAction(Film film) throws Exception {
        HttpResponse<String> response = postRequest("newFilm", film.toJsonString());

        if (response.statusCode() != 200) {
            throw new Exception("Film couldn't be added");
        }
    }

    public void updateFilmAction(Film film) throws Exception {
        HttpResponse<String> response = postRequest("updateFilm", film.toJsonString());

        if (response.statusCode() == 200) {
            System.out.println("Film updated successful");
        } else {
            throw new Exception("Film couldn't be updated");
        }
    }

    public void addFilmAutoAction(JSONObject automateFilmRequestData) throws Exception {
        HttpResponse<String> response = postRequest("addFilmAuto", automateFilmRequestData.toString());

        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            throw new Exception("Movies couldn't be added");
        }
    }

    public ArrayList<Nutzer> getAllUsers(Nutzer currentUser) throws Exception {
        HttpResponse<String> response = postRequest(
                "getAllUsers",
                new JSONObject()
                        .put("currentUserId", currentUser.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            JSONArray result = new JSONArray(response.body());
            ArrayList<Nutzer> nutzerliste = new ArrayList<>();
            for (Object user: result) {
                nutzerliste.add(new Nutzer(JsonParser.parseString(user.toString()).getAsJsonObject()));
            }
            return nutzerliste;
        } else {
            throw new Exception("Users couldn't be fetched");
        }
    }

    public ArrayList<Nutzer> getAllFriendRequests(Nutzer currentUser) throws Exception {
        HttpResponse<String> response = postRequest(
                "getAllFriendRequests",
                new JSONObject()
                        .put("currentUserId", currentUser.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            JSONArray result = new JSONArray(response.body());
            ArrayList<Nutzer> nutzerliste = new ArrayList<>();
            for (Object user: result) {
                nutzerliste.add(new Nutzer(JsonParser.parseString(user.toString()).getAsJsonObject()));
            }
            return nutzerliste;
        } else {
            throw new Exception("Users couldn't be fetched");
        }
    }

    public void newFriendRequest(Nutzer currentUser, Nutzer newFriend) throws Exception {
        HttpResponse<String> response = postRequest(
                "newFriendRequest",
                new JSONObject()
                        .put("currentUser", currentUser.getId())
                        .put("newFriend", newFriend.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            throw new Exception("User can't be found");
        }
    }

    public void declineFriendRequest(Nutzer currentUser, Nutzer declinedFriend) throws Exception {
        HttpResponse<String> response = postRequest(
                "declineFriendRequest",
                new JSONObject()
                        .put("currentUser", currentUser.getId())
                        .put("newFriend", declinedFriend.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            throw new Exception("User couldn't be declined");
        }
    }

    public void acceptFriendRequest(Nutzer currentUser, Nutzer acceptedFriend) throws Exception {
        HttpResponse<String> response = postRequest(
                "acceptFriendRequest",
                new JSONObject()
                        .put("currentUser", currentUser.getId())
                        .put("newFriend", acceptedFriend.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            throw new Exception("User couldn't be accepted");
        }
    }

    public void deleteFriend(Nutzer currentUser, Nutzer deletedFriend) throws Exception {
        HttpResponse<String> response = postRequest(
                "deleteFriend",
                new JSONObject()
                        .put("currentUser", currentUser.getId())
                        .put("deletedFriend", deletedFriend.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            throw new Exception("User couldn't be deleted");
        }
    }

    public JSONArray getAllFriends(Nutzer currentUser) throws Exception {
        HttpResponse<String> response = postRequest(
                "getAllFriends",
                new JSONObject()
                        .put("currentUserId", currentUser.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println(response.body());
            return new JSONArray(response.body());
        } else {
            throw new Exception("Users couldn't be fetched");
        }
    }

    public JSONObject startChat(Nutzer currentUser, Nutzer chatWith) throws Exception {
        HttpResponse<String> response = postRequest(
                "startChat",
                new JSONObject()
                        .put("currentUser", currentUser.getId())
                        .put("chatWith", chatWith.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            return new JSONObject(response.body());
        } else {
            throw new Exception("User isn't online");
        }
    }

    public void newFilmReport(Film wrongFilm, String errorDescription, Nutzer author) throws Exception {
        HttpResponse<String> response = postRequest(
                "newFilmReport",
                new JSONObject()
                        .put("wrongFilm", wrongFilm.getId())
                        .put("errorDescription", errorDescription)
                        .put("authorId", author.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            throw new Exception("New film report couldn't be created");
        }
    }


    public String new2faRequest(Nutzer currentUser) throws Exception {
        HttpResponse<String> response = postRequest(
                "new2faRequest",
                new JSONObject()
                        .put("email", currentUser.getMail())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("2FA Code: " + response.body());
            // returns the 2FA Code
            return response.body().trim();
        } else {
            throw new Exception("Email couldn't be send");
        }
    }


    public void filmGesehen(Nutzer currentUser, Film watchedMovie) throws Exception {
        HttpResponse<String> response = postRequest(
                "filmGesehen",
                new JSONObject()
                        .put("currentUserId", currentUser.getId())
                        .put("watchedMovieId", watchedMovie.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            System.out.println("Film wurde bereits von der Person gesehen.");
            throw new Exception("Ein Fehler ist aufgetreten");}
    }


    public void deleteFromSeenList(Nutzer currentUser, Film unwatchedMovie) throws Exception {
        HttpResponse<String> response = postRequest(
                "filmDochNichtGesehen",
                new JSONObject()
                        .put("currentUserId", currentUser.getId())
                        .put("watchedMovieId", unwatchedMovie.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            System.out.println("Film wurde noch nie von der Person gesehen.");
        }
    }

    public JSONArray getDiscussionGroups(Integer id) throws Exception {
        HttpResponse<String> response = postRequest(
                "discussionGroups",
                new JSONObject()
                        .put("userId", id)
                        .toString()
        );

        if (response.statusCode() == 200) {
            return new JSONArray(response.body());
        } else {
            System.out.println("Serverseitiger Fehler. I'm sorry :/");
            return null;
        }
    }

    public void joinDiscussionGroup(Integer id, Nutzer nutzer) throws Exception {
        HttpResponse<String> response = postRequest(
                "joinDiscussionGroup",
                new JSONObject()
                        .put("userId", nutzer.getId())
                        .put("groupId", id)
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("Successfully joined a new discussion group");
        } else {
            throw new Exception("Serverseitiger Fehler. I'm sorry :/");
        }
    }

    public void createDiscussionGroup(String groupName, Boolean isPrivate, Integer nutzerId) throws Exception {
        HttpResponse<String> response = postRequest(
                "createDiscussionGroup",
                new JSONObject()
                        .put("userId", nutzerId)
                        .put("groupName", groupName)
                        .put("isPrivate", isPrivate)
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("Successfully created a new discussion group");
        } else {
            throw new Exception("Eine Gruppe mit diesem Namen existiert bereits.");
        }
    }

    public Integer startGroupChat(Integer id) throws Exception {
        HttpResponse<String> response = postRequest(
                "startGroupChat",
                new JSONObject()
                        .put("groupId", id)
                        .toString()
        );

        if (response.statusCode() == 200) {
            // Give chat server more time to boot up else could fail because of race condition
            Thread.sleep(900);
            return Integer.parseInt(response.body().trim());
        } else {
            System.out.println("Serverseitiger Fehler. I'm sorry :/");
            return null;
        }
    }

    private HttpResponse<String> postRequest(String path, String jsonString) throws Exception {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(url + path))
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    private HttpResponse<String> getRequest(String path) throws Exception {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(url + path))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    public Nutzer registerUserAction(Nutzer newUserData) throws Exception {
        HttpResponse<String> response = postRequest("registerUser", newUserData.toJsonString());

        if (response.statusCode() == 200) {
            System.out.println("New user registered successful");
            return new Nutzer(JsonParser.parseString(response.body()).getAsJsonObject());
        } else {
            throw new Exception("User couldn't be registered");
        }
    }

    public Nutzer loginUserAction(JSONObject credentials) throws Exception {
        HttpResponse<String> response = postRequest("loginUser", credentials.toString());

        if (response.statusCode() == 200) {
            System.out.println("User found");
            return new Nutzer(JsonParser.parseString(response.body()).getAsJsonObject());

        } else {
            throw new Exception("User not found");
        }
    }

    public JSONObject getSettings(Nutzer user) {
        try {
            HttpResponse<String> response = this.postRequest("getsettings", new JSONObject()
                    .put("userId", user.getId())
                    .toString());
            return new JSONObject(response.body());
        } catch (Exception e) {
            return new JSONObject();
        }
    }


    public void setSettings(Nutzer user, Integer freundesliste, Integer watchlist, Integer geseheneFilme, Integer bewertungen) throws Exception {
        HttpResponse<String> response = this.postRequest("setsettings", new JSONObject()
                .put("userid", user.getId())
                .put("freundesliste", freundesliste)
                .put("watchlist", watchlist)
                .put("geseheneFilme", geseheneFilme)
                .put("bewertungen", bewertungen)
                .toString());

        if (response.statusCode() == 200) {
            System.out.println("Settings changed successful");

        } else {
            throw new Exception("Settings not changed");
        }

    }

    public JSONArray getAllFilmsInWatchlist(Nutzer currentUser) throws IOException, InterruptedException {
        JSONObject filmInWatchlist = new JSONObject().put("userId", currentUser.getId());

        try {
            HttpResponse<String> response = this.postRequest("getmoviesinwatchlist", filmInWatchlist.toString());
            return new JSONArray(response.body());
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public JSONArray getAllFilmsInSeenList(Nutzer currentUser) throws IOException, InterruptedException {
        JSONObject filmInSeenList = new JSONObject().put("userId", currentUser.getId());
        try {
            HttpResponse<String> response = this.postRequest("getmoviesinseenlist", filmInSeenList.toString());
            return new JSONArray(response.body());
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private JSONObject setFilmfilterValues(String Name, String Kategorie, String Erscheinungsdatum, String Regisseur, String Drehbuchautor, String Cast, String Filmlaenge) {
        JSONObject filmInSeenList = new JSONObject();

        if (!Name.isEmpty())
            filmInSeenList.put("name", Name);
        else
            filmInSeenList.put("name", "%%");

        if (!Kategorie.isEmpty())
            filmInSeenList.put("kategorie", Kategorie);
        else
            filmInSeenList.put("kategorie", "%%");

        if (!Erscheinungsdatum.isEmpty())
            filmInSeenList.put("erscheinungsdatum", Erscheinungsdatum);
        else
            filmInSeenList.put("erscheinungsdatum", "%%");

        if (!Regisseur.isEmpty())
            filmInSeenList.put("regisseur", Regisseur);
        else
            filmInSeenList.put("regisseur", "%%");

        if (!Drehbuchautor.isEmpty())
            filmInSeenList.put("drehbuchautor", Drehbuchautor);
        else
            filmInSeenList.put("drehbuchautor", "%%");

        if (!Cast.isEmpty())
            filmInSeenList.put("cast", Cast);
        else
            filmInSeenList.put("cast", "%%");

        if (!Filmlaenge.isEmpty())
            filmInSeenList.put("filmlaenge", Filmlaenge);
        else
            filmInSeenList.put("filmlaenge", "%%");
        return filmInSeenList;
    }

    public void addFilmToWatchlist(Nutzer currentUser, Film filmToAdd) throws Exception {
        JSONObject addFilm = new JSONObject();
        addFilm.put("filmId", filmToAdd.getId());
        addFilm.put("userId", currentUser.getId());
        HttpResponse<String> response = postRequest("filmToWatchlist", addFilm.toString());
        if (response.statusCode() == 200) {
            System.out.println("Film added successful");
        } else {
            throw new Exception("Film couldn't be added");
        }
    }

    public void deleteFilmFromWatchlist(Nutzer currentUser, Film filmToDelete) throws Exception {
        HttpResponse<String> response = postRequest("deleteFilm",
                new JSONObject()
                        .put("currentUserId", currentUser.getId())
                        .put("filmToDeleteId", filmToDelete.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("Film deleted successful");
        } else {
            throw new Exception("Film couldn't be deleted");
        }
    }

    public JSONArray getAllReviews(Film filmId) throws Exception {
        HttpResponse<String> response = postRequest("getAllReviews",
                new JSONObject()
                        .put("filmId", filmId.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("Bewertung erfolgreich geholt");
            return new JSONArray(response.body());
        } else {
            throw new Exception("Bewertungen konnten nicht geholt werden");
        }
    }

    public JSONArray getAllUserReviews(Nutzer currentUser) throws Exception {
        HttpResponse<String> response = postRequest("getAllUserReviews",
                new JSONObject()
                        .put("userId", currentUser.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("User Bewertungen erfolgreich geholt");
            return new JSONArray(response.body());
        } else {
            throw new Exception("Bewertungen konnten nicht geholt werden");
        }
    }

    public void addNewReview(Nutzer currentUser, Film film, String titel, Integer bewertung, String text) throws Exception {
        HttpResponse<String> response = postRequest("addReview",
                new JSONObject()
                        .put("userId", currentUser.getId())
                        .put("filmId", film.getId())
                        .put("titel", titel)
                        .put("bewertung", bewertung)
                        .put("text", text)
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("New Review added successful");
        } else {
            throw new Exception("Bewertung konnte nicht gespeichert werden, I AM SORRY");
        }
    }

    public void editReview(Integer bewertungId, String titel, int bewertung, String text) throws Exception {
        JSONObject editedReview = new JSONObject()
                .put("bewertungId",bewertungId)
                .put("titel", titel)
                .put("bewertung", bewertung)
                .put("text", text);
        HttpResponse<String> response = postRequest("editReview", editedReview.toString());

        if (response.statusCode() == 200) {
            System.out.println("hat geklappt");
        } else {
            throw new Exception("fehlgeschlagen");
        }
    }

    public Nutzer getUserFromReview(Bewertung bewertung) throws Exception {
        HttpResponse<String> response = postRequest("getUserFromReview",
                new JSONObject()
                        .put("bewertungId", bewertung.getBewertungId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("User from Review found");
            return new Nutzer(new JSONObject(response.body()));
        } else {
            throw new Exception("User from Review couldn't be found");
        }
    }

    public void deleteReport(Report report) throws Exception {
        HttpResponse<String> response = postRequest("deleteReport",
                new JSONObject()
                        .put("reportId", report.getId())
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("Report deleted successful");
        } else {
            throw new Exception("Report couldn't be deleted");
        }
    }


    public JSONArray getAllReports() throws Exception{
        HttpResponse<String> response = postRequest("getAllReports",
                new JSONObject().toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("Alle Reports erfolgreich geholt");
            return new JSONArray(response.body());
        } else {
            throw new Exception("Reports konnten nicht geholt werden");
        }
    }

    public void deleteReview(Bewertung bewertung) throws Exception{
        JSONObject reviewToDelete = new JSONObject()
                .put("bewertungId", bewertung.getBewertungId());
        HttpResponse<String> response = postRequest("deleteReview", reviewToDelete.toString());

        if (response.statusCode() == 200) {
            System.out.println("Review deleted successfully");
        } else {
            throw new Exception("Review not deleted");
        }
    }

    public Integer getAverageBewertung(Integer id) throws Exception {
        HttpResponse<String> response = postRequest("getAverageBewertung",
                new JSONObject().put("filmId", Integer.valueOf(id)).toString());

        if (response.statusCode() == 200) {
            System.out.println("AVG geholt");
            return parseInt(response.body().trim());
        } else {
            throw new Exception("kagge");
        }

    }

    public  JSONArray getRecommendedMovedBasedOnFriendSeenList (Nutzer currentUser){
        JSONObject friendSeenList = new JSONObject().put("userId", currentUser.getId());

        try {
            HttpResponse<String> response = this.postRequest("recomenndedbasedonfriendsseenlist", friendSeenList.toString());
            return new JSONArray(response.body());
        } catch (Exception e) {
            return new JSONArray();
        }


    }
    public  JSONArray getRecommendedMovedBasedOnMySeenList (Nutzer currentUser){
        JSONObject friendSeenList = new JSONObject().put("userId", currentUser.getId());

        try {
            HttpResponse<String> response = this.postRequest("recomenndedbasedonmyseenlist", friendSeenList.toString());
            return new JSONArray(response.body());
        } catch (Exception e) {
            return new JSONArray();
        }

    }

    public JSONArray downloadFilmstatistik(Integer id) throws Exception{
        HttpResponse<String> response = postRequest("getFilmstatistikDownload",
                new JSONObject().put("filmId", Integer.valueOf(id)).toString());

        if (response.statusCode() == 200) {
            return new JSONArray(response.body());
        } else {
            throw new Exception("Statistik coud not be downloaded");
        }
    }

    public JSONArray getFilmstatistik(Integer id) throws Exception{
        HttpResponse<String> response = postRequest("getFilmstatistik",
                new JSONObject().put("filmId", Integer.valueOf(id)).toString());

        if (response.statusCode() == 200) {
            System.out.println(new JSONArray(response.body()));
            return new JSONArray(response.body());
        } else {
            throw new Exception("Statistik could not be accessed");
        }
    }

    public void resetFilmstatistik(Integer id) throws Exception {
        HttpResponse<String> response = postRequest("resetFilmstatistik",
                new JSONObject().put("filmId", Integer.valueOf(id)).toString());

        if (response.statusCode() == 200) {
            System.out.println("Reset was successfully");
        } else {
            throw new Exception("kagge");
        }
    }

    public void addNewEinladung(Nutzer currentNutzer, Nutzer eingeladenerNutzer, String filmTitle, String message, String date, String time) throws Exception {
        HttpResponse<String> response = postRequest("addNewEinladung",
                new JSONObject()
                        .put("currentUser", currentNutzer.toJSONObject().toString())
                        .put("invitedUser", eingeladenerNutzer.toJSONObject().toString())
                        .put("filmName", filmTitle)
                        .put("datum", date)
                        .put("uhrzeit", time)
                        .put("nachricht", message)
                        .toString()
        );


        if (response.statusCode() == 200){
            System.out.println("New Invitation added successful");
        } else {
            throw new Exception("Invitation could not be saved!");
        }
    }


    public ArrayList<Einladung> getAllInvitations(Nutzer currentUser) throws Exception {
        HttpResponse<String> response = postRequest(
                "getAllInvitations",
                new JSONObject()
                        .put("currentUserId", currentUser.getId())
                        .toString()
        );
        if (response.statusCode() == 200){
            JSONArray result = new JSONArray(response.body());
            ArrayList<Einladung> einladungen = new ArrayList<>();
            for (Object einladung: result){
                einladungen.add(new Einladung(JsonParser.parseString(einladung.toString()).getAsJsonObject()));
            }
            return einladungen;
        } else {
            throw new Exception("Invitations could not be fetched");
        }
    }

    public void declineInvitation(Einladung einladung) throws Exception {
        HttpResponse<String> response = postRequest(
                "declineInvitation",
                new JSONObject()
                        .put("currentUser", einladung.getIdNo2())
                        .put("invitationSender", einladung.getIdNo1())
                        .toString()
        );

        if(response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            throw new Exception("Ablehnen der Einladung war nicht erfolgreich");
        }
    }

    public void acceptInvitation(Einladung einladung) throws  Exception {
        HttpResponse<String> response = postRequest(
                "acceptInvitation",
                new JSONObject()
                        .put("currentUser", einladung.getIdNo2())
                        .put("invitationSender", einladung.getIdNo1())
                        .toString()
        );

        if (response.statusCode() == 200){
            System.out.println(response.body());
        } else {
            throw new Exception("Annehmen der Einladung war nicht erfolgreich!");
        }
    }

    public int sumOpenInvitations(Integer userId) throws  Exception {
        HttpResponse<String> response = postRequest(
                "sumOpenInvitations",
                new JSONObject()
                        .put("userID", userId)
                        .toString()
        );
        if (response.statusCode() == 200){
            System.out.println(response.body());
            return Integer.parseInt(response.body().trim());
        } else {
            throw new Exception("Summieren hat nicht geklappt du Otto");
        }
    }


    public JSONArray getUserStatistics(Integer userId, Integer von, Integer bis) throws Exception{
        HttpResponse<String> response = postRequest("bestMovies",
                new JSONObject()
                        .put("userId", userId)
                        .put("von", von)
                        .put("bis", bis)
                        .toString()
        );

        if (response.statusCode() == 200) {
            System.out.println("Statistiken erfolgreich geholt");
            return new JSONArray(response.body());
        } else {
            throw new Exception("Statistiken konnten nicht geholt werden");
        }
    }

    public ArrayList<Message> getAllMessages(Nutzer currentUser) throws Exception {
        HttpResponse<String> response = postRequest(
                "getAllMessages",
                new JSONObject()
                        .put("currentUserId", currentUser.getId())
                        .toString()
        );
        if(response.statusCode() == 200){
            JSONArray result = new JSONArray(response.body());
            ArrayList<Message> messages = new ArrayList<>();
            for (Object message: result){
                messages.add(new Message(JsonParser.parseString(message.toString()).getAsJsonObject()));
            }
            return messages;
        } else {
            throw new Exception("Messages could not be fetched!");
        }

    }

    public void deleteMessage(Message message) throws Exception{
        HttpResponse<String> response = postRequest(
                "deleteMessage",
                new JSONObject()
                        .put("messageId", message.getMessageId())
                        .toString()
        );
        if (response.statusCode() == 200) {
            System.out.println(response.body());
        } else {
            throw  new Exception("löschen der Nachricht nicht erfolgreich!");
        }
    }




    public Obermensch getMensch() {
        return mensch;
    }

    public Nutzer getNutzer() {
        return nutzer;
    }

    public void setMensch(Obermensch mensch) {
        this.mensch = mensch;
    }

    public void setNutzer(Nutzer nutzer) {
        this.nutzer = nutzer;
    }

    public Film getFilmToBeUpdated() {
        return filmToBeUpdated;
    }

    public void setFilmToBeUpdated(Film filmToBeUpdated) {
        this.filmToBeUpdated = filmToBeUpdated;
    }

    public Nutzer getFremdNutzer() {
        return fremdNutzer;
    }

    public void setFremdNutzer(Nutzer fremdNutzer) {
        this.fremdNutzer = fremdNutzer;
    }
}
