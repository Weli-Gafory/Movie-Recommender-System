package clientServer;

import clientServer.chat.GroupChatStarter;
import clientServer.chat.GroupChatTransponder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import models.*;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import scraper.Scraper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler extends AbstractHandler {

    private ArrayList<ArrayList<Integer>> chatPartners = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> groupChats = new ArrayList<>();
    private int chatPortCounter = 5000;
    private int groupChatPortCounter = 6000;

    public static Map<Integer, ArrayList<GroupChatTransponder>> chattingClients = new HashMap<>();

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (request.getMethod() != null && request.getMethod().equals("GET") && request.getPathInfo().equals("/getmovies")) {
            try {
                JSONArray toclient = DB.findAllElements();
                response.getWriter().println(toclient);
                baseRequest.setHandled(true);

            } catch (SQLException e) {
                e.printStackTrace();
                baseRequest.setHandled(false);
            }
        }


        // Login SYSADMIN Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/login")) {
            JsonObject credentials = JsonParser.parseReader(request.getReader()).getAsJsonObject();

            try {
                if (DB.checkAdmin(credentials.get("mail").getAsString())) {
                    Systemadministrator user = DB.checkPasswordAdmin(credentials.get("mail").getAsString(), credentials.get("password").getAsString());

                    System.out.println("User information gathered successful");

                    response.getWriter().println(user.toJsonString());
                    baseRequest.setHandled(true);
                }
            } catch (Exception e) {
                System.out.println("Email or password wrong!");
            }
        }

        // Registration SYSADMIN Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/register")) {
            Systemadministrator sysAdmin = new Systemadministrator(JsonParser.parseReader(request.getReader()).getAsJsonObject());

            try {
                Systemadministrator newUser = DB.registerAdmin(sysAdmin);
                System.out.println(newUser.toString());
                response.getWriter().println(newUser.toJsonString());

                System.out.println("User registered successful");

                baseRequest.setHandled(true);

            } catch (Exception e) {

                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // Update film Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/updateFilm")) {
            Film film = new Film(JsonParser.parseReader(request.getReader()).getAsJsonObject());

            if (!film.getFilmbanner().isEmpty()) {
                String filePath = "./.banner/" + film.getName() + ".png";
                OutputStream fileWriter = new FileOutputStream(filePath);
                fileWriter.write(Base64.getDecoder().decode(film.getFilmbanner()));

                film.setFilmbanner(filePath);
            }
            else {
                film.setFilmbanner("./.banner/platzhalter.png");
            }

            try {
                DB.updateFilm(film);

                System.out.println("Film updated successful");
                System.out.println(film.toString());
                response.getWriter().println(film.toJsonString());

                baseRequest.setHandled(true);

            } catch (Exception e) {

                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // New film Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/newFilm")) {
            Film film = new Film(JsonParser.parseReader(request.getReader()).getAsJsonObject());


            if (!film.getFilmbanner().isEmpty()) {
                String filePath = "./.banner/" + film.getName() + ".png";
                OutputStream fileWriter = new FileOutputStream(filePath);
                fileWriter.write(Base64.getDecoder().decode(film.getFilmbanner()));

                film.setFilmbanner(filePath);
            }
            else {
                film.setFilmbanner("./.banner/platzhalter.png");
            }

            try {
                DB.insertFilm(
                        film.getName(),
                        film.getKategorie(),
                        film.getErscheinungsdatum(),
                        film.getRegisseur(),
                        film.getDrehbuchautor(),
                        film.getCast(),
                        film.getFilmlaenge(),
                        film.getFilmbanner()
                );
                response.getWriter().println("Film added successful");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // Get all Nutzer handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getAllUsers")) {
            JSONObject values = new JSONObject(new JSONTokener(request.getReader()));

            try {
                ArrayList<Nutzer> allUsers = DB.getAllNutzer(values.getInt("currentUserId"));
                JSONArray result = new JSONArray();
                for (Nutzer nutzer : allUsers) {
                    try {
                        nutzer.setProfilepic(Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of(nutzer.getProfilepic()))));
                    } catch (Exception e) {
                        nutzer.setProfilepic(Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of("./.profilepics/platzhalter.png"))));
                    }
                    result.put(nutzer.toJSONObject());
                }
                response.getWriter().println(result);

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // Add new friend request handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/newFriendRequest")) {
            JSONObject newFriends = new JSONObject(new JSONTokener(request.getReader()));

            try {
                System.out.println(newFriends);

                 DB.createFreundschaftsanfrage(newFriends.getInt("currentUser"), newFriends.getInt("newFriend"));
                 response.getWriter().println("Friend request sent");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // decline friend request handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/declineFriendRequest")) {
            JSONObject declineNewFriend = new JSONObject(new JSONTokener(request.getReader()));

            try {
                System.out.println(declineNewFriend);

                 DB.deleteFreundschaftsanfrage(declineNewFriend.getInt("currentUser"), declineNewFriend.getInt("newFriend"));
                 response.getWriter().println("Friend declined successfully");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // accept friend request handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/acceptFriendRequest")) {
            JSONObject friends = new JSONObject(new JSONTokener(request.getReader()));

            try {
                System.out.println(friends);

                 DB.acceptFreundschaftsanfrage(friends.getInt("currentUser"), friends.getInt("newFriend"));
                 response.getWriter().println("Friend request accepted");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();

                baseRequest.setHandled(false);
            }
        }

        // delete friend request handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/deleteFriend")) {
            JSONObject noFriendsAnymore = new JSONObject(new JSONTokener(request.getReader()));

            try {
                System.out.println(noFriendsAnymore);

                 DB.deleteFriend(noFriendsAnymore.getInt("currentUser"), noFriendsAnymore.getInt("deletedFriend"));
                 response.getWriter().println("Friend deleted!");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // get all friends handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getAllFriends")) {
            JSONObject user = new JSONObject(new JSONTokener(request.getReader()));

            try {
                ArrayList<Nutzer> allFriends = DB.getFriends(user.getInt("currentUserId"));
                JSONArray result = new JSONArray();
                for (Nutzer nutzer : allFriends) {
                    nutzer.setProfilepic(Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of(nutzer.getProfilepic()))));
                    result.put(nutzer.toJSONObject());
                }
                response.getWriter().println(result);

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // get all friends that requested you handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getAllFriendRequests")) {
            JSONObject user = new JSONObject(new JSONTokener(request.getReader()));

            try {
                ArrayList<Nutzer> allFriends = DB.getAllFreundschaftsanfragen(user.getInt("currentUserId"));
                JSONArray result = new JSONArray();
                for (Nutzer nutzer : allFriends) {
                    nutzer.setProfilepic(Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of(nutzer.getProfilepic()))));
                    result.put(nutzer.toJSONObject());
                }
                response.getWriter().println(result);

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // Start chat handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/startChat")) {
            JSONObject newChatPartners = new JSONObject(new JSONTokener(request.getReader()));
            Integer currentUserId = newChatPartners.getInt("currentUser");
            Integer chatWithUserId = newChatPartners.getInt("chatWith");
            int chattingPort = 0;

            ArrayList<Integer> result = new ArrayList<>();
            Boolean isRequester = false;

            try {
                for (int i = this.chatPartners.size()-1; i > -1 ; i--) {
                    if (this.chatPartners.get(i).get(1) == currentUserId && this.chatPartners.get(i).get(0) == chatWithUserId && this.chatPartners.get(i).get(3) == 0) {
                        chattingPort = this.chatPartners.get(i).get(2);
                        // This indicates that chat partners have matched once on this port
                        this.chatPartners.get(i).set(3, 1);
                        result = this.chatPartners.get(i);
                        isRequester = false;
                        break;
                    }

                }
                if (chattingPort == 0) {
                    this.chatPortCounter++;
                    chattingPort = chatPortCounter;
                    result.add(currentUserId);
                    result.add(chatWithUserId);
                    result.add(chattingPort);
                    // This indicates that chat partners have not matched once on this port, on index 3
                    result.add(0);
                    isRequester = true;
                    this.chatPartners.add(result);
                }

                response.getWriter().println(
                        new JSONObject()
                                .put("chattingPort", result.get(2))
                                .put("isRequester", isRequester)
                );

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // Start group chat handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/startGroupChat")) {
            JSONObject newChatGroup = new JSONObject(new JSONTokener(request.getReader()));
            Integer chatGroupId = newChatGroup.getInt("groupId");
            int chattingPort = 0;
            boolean alreadyStarted = false;

            ArrayList<Integer> result = new ArrayList<>();

            try {
                for (ArrayList<Integer> groupChat: this.groupChats) {
                    if (groupChat.get(0) == chatGroupId) {
                        chattingPort = groupChat.get(1);
                        result = groupChat;
                        alreadyStarted = true;
                        break;
                    }
                }

                if (chattingPort == 0) {
                    this.groupChatPortCounter++;
                    chattingPort = groupChatPortCounter;
                    result.add(chatGroupId);
                    result.add(chattingPort);
                    this.groupChats.add(result);
                }


                if (!alreadyStarted) {
                    chattingClients.put(chattingPort, new ArrayList<>());
                    int finalChattingPort = chattingPort;
                    new Thread(() -> {
                        GroupChatStarter requester = new GroupChatStarter();
                        requester.getRequesterTransponder(finalChattingPort);
                    }).start();
                }


                response.getWriter().println(result.get(1));
                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // New film report handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/newFilmReport")) {
            JSONObject report = new JSONObject(new JSONTokener(request.getReader()));

            try {

                DB.addReport(report.getInt("authorId"), report.getInt("wrongFilm"), report.get("errorDescription").toString());
                 response.getWriter().println("Added new film report successful");

                mailer.sendNewReportNotification(DB.getAllEmailsFromAdmins());

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // get all discussion groups for one user handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/discussionGroups")) {
            try {
                response.getWriter().println(DB.getDiscussionGroups(Integer.parseInt(new JSONObject(new JSONTokener(request.getReader())).get("userId").toString())));

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // join discussion group handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/joinDiscussionGroup")) {
            JSONObject data = new JSONObject(new JSONTokener(request.getReader()));
            try {
                DB.joinDiscussionGroup(data.getInt("groupId"), data.getInt("userId"));

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // create new discussion group handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/createDiscussionGroup")) {
            JSONObject data = new JSONObject(new JSONTokener(request.getReader()));
            try {
                DB.createDiscussionGroup(data.getString("groupName"), data.getBoolean("isPrivate"), data.getInt("userId"));

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // New 2FA Login request handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/new2faRequest")) {
            String userEmail = new JSONObject(new JSONTokener(request.getReader())).get("email").toString();

            try {
                System.out.println(userEmail);

                Integer code = (1000 + (int) (Math.random() * ((10000 - 1000) + 1)));
                mailer.send2faMail(userEmail, code);
                response.getWriter().println(code);

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // watched a new movie handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/filmGesehen")) {
            JSONObject watchedMovie = new JSONObject(new JSONTokener(request.getReader()));

            try {
                System.out.println(watchedMovie);

                DB.addToSeenList(watchedMovie.getInt("currentUserId"), watchedMovie.getInt("watchedMovieId"));
                response.getWriter().println("You watched a movie!");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // Delete from watched movies handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/filmDochNichtGesehen")) {
            JSONObject unwatchedMovie = new JSONObject(new JSONTokener(request.getReader()));

            try {
                DB.removeFromSeenList(unwatchedMovie.getInt("currentUserId"), unwatchedMovie.getInt("watchedMovieId"));
                response.getWriter().println("You unwatched a movie!");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }


        // Add movies automatically Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/addFilmAuto")) {
            JsonObject timeAndCategoryOfMovies = JsonParser.parseReader(request.getReader()).getAsJsonObject();

            try {
                Thread first = new Thread(new Scraper(timeAndCategoryOfMovies.get("kategorie").getAsString(),
                        timeAndCategoryOfMovies.get("von").getAsString(),
                        timeAndCategoryOfMovies.get("bis").getAsString()));
                first.start();

                response.getWriter().println("Movies gonna be added");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        // Registration User Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/registerUser")) {
            Nutzer User = new Nutzer(JsonParser.parseReader(request.getReader()).getAsJsonObject());
            if (User.getProfilepic() != null) {
                String filePath = "./.profilepics/" + User.getMail() + ".png";
                OutputStream fileWriter = new FileOutputStream(filePath);
                fileWriter.write(Base64.getDecoder().decode(User.getProfilepic()));
                User.setProfilepic(filePath);
            } else
                User.setProfilepic("./.profilepics/platzhalter.png");

            try {
                Nutzer newUser = DB.addNutzer(
                        User.getName(),
                        User.getSurname(),
                        User.getMail(),
                        User.getPassword(),
                        User.getBirthday(),
                        User.getProfilepic()
                );
                System.out.println(newUser.toString());
                response.getWriter().println(newUser.toJsonString());

                System.out.println("User registered successful");

                baseRequest.setHandled(true);

            } catch (Exception e) {

                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }


        // Login User Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/loginUser")) {
            JsonObject credentials = JsonParser.parseReader(request.getReader()).getAsJsonObject();

            try {
                if (DB.checkNutzer(credentials.get("mail").getAsString())) {
                    Nutzer user = DB.checkPasswortNutzer(credentials.get("mail").getAsString(), credentials.get("password").getAsString());

                    user.setProfilepic(Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of(user.getProfilepic()))));

                    System.out.println("User information gathered successful");

                    response.getWriter().println(user.toJsonString());
                    baseRequest.setHandled(true);
                }
            } catch (Exception e) {
                System.out.println("Email or password wrong!");
                baseRequest.setHandled(false);
            }
        }


        // get User settings Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getsettings")) {
            JsonObject credentials = JsonParser.parseReader(request.getReader()).getAsJsonObject();

            try {
                // TODO Einkommentieren und prüfen

                 JSONObject settings = DB.getPrivacySettings(credentials.get("userId").getAsInt());
                response.getWriter().println(settings);

                baseRequest.setHandled(true);
            }
            // TODO Wenn noch keine Settings zu einem User existieren
            catch (Exception e) {
                System.out.println("No settings");
                baseRequest.setHandled(false);
            }
        }




        // set User settings Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/setsettings")) {
            JSONObject settings = new JSONObject(new JSONTokener(request.getReader()));
            try {
                // TODO Einkommen und prüfen
                DB.editPrivacy(
                        settings.getInt("userid"),
                        settings.getInt("freundesliste"),
                        settings.getInt("watchlist"),
                        settings.getInt("geseheneFilme"),
                        settings.getInt("bewertungen"));
                response.getWriter().println("Settings changed");
                baseRequest.setHandled(true);
            } catch (Exception e) {
                System.out.println("No Database connection");
                baseRequest.setHandled(false);
            }
        }

        // get all Films in Watchlist Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getmoviesinwatchlist")) {
            JSONObject filterwerte = new JSONObject(new JSONTokener(request.getReader()));
            try {
                // TODO Einkommentieren und prüfen

                  JSONArray watchlist = DB.getAllFromWatchlistweli(
                          filterwerte.getInt("userId")
                  );
                    response.getWriter().println(watchlist);
                baseRequest.setHandled(true);
            }
            // TODO Wenn noch keine Filme zu einem User existieren
            catch (Exception e) {
                System.out.println("No Movies in Watchlist");
                baseRequest.setHandled(false);
            }
        }

        // get all Films in Seenlist Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getmoviesinseenlist")) {
            JSONObject filterwerte = new JSONObject(new JSONTokener(request.getReader()));
            try {
                // TODO Einkommentieren und prüfen

                  JSONArray seenlist = DB.getAllFromSeenlist(
                          filterwerte.getInt("userId")
                  );
                    response.getWriter().println(seenlist);
                baseRequest.setHandled(true);
            }
            // TODO Wenn noch keine Filme zu einem User existieren
            catch (Exception e) {
                System.out.println("No Movies in Seenlist");
                baseRequest.setHandled(false);
            }
        }
        //recomenndedBasedOnFriendsSeenList
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/recomenndedbasedonfriendsseenlist")) {
            JSONObject BasedOnfriendSeenList = new JSONObject(new JSONTokener(request.getReader()));
            try {
                String reque= request.getPathInfo().toString();
                String recommendationMethode =reque.substring(1,reque.length());

                System.out.println(recommendationMethode);

                JSONArray seenlist = DB.getSimilarMovies(
                        BasedOnfriendSeenList.getInt("userId"),recommendationMethode
                );
                response.getWriter().println(seenlist);
                baseRequest.setHandled(true);
            }
            catch (Exception e) {
                System.out.println("Friends have seen no Movie");
                baseRequest.setHandled(false);
            }
        }
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/recomenndedbasedonmyseenlist")) {
            JSONObject BasedOnMySeenList = new JSONObject(new JSONTokener(request.getReader()));
            try {
                String reque= request.getPathInfo().toString();
                String recommendationMethode =reque.substring(1,reque.length());

                System.out.println(recommendationMethode);

                JSONArray seenlist = DB.getSimilarMovies(
                        BasedOnMySeenList.getInt("userId"),recommendationMethode
                );
                response.getWriter().println(seenlist);
                baseRequest.setHandled(true);
            }
            catch (Exception e) {
                System.out.println("Friends have seen no Movie");
                baseRequest.setHandled(false);
            }
        }


        //add Movie to Watchlist Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/filmToWatchlist")) {
            JSONObject unwatchedMovie = new JSONObject(new JSONTokener(request.getReader()));

            try {
                // TODO: 23.05.2022 Einkommentieren & prüfen

                 DB.addToWatchlist(unwatchedMovie.getInt("userId"), unwatchedMovie.getInt("filmId"));
                response.getWriter().println("Movie Added!");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();

                baseRequest.setHandled(false);
            }
        }

        //delete Movie from Watchlist Handler
        if (request.getReader() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/deleteFilm")) {
            JSONObject toDeleteFromWatchlist = new JSONObject(new JSONTokener(request.getReader()));

            try {
                // TODO: 23.05.2022 Einkommentieren & prüfen
                 DB.removeFromWatchlist(
                 toDeleteFromWatchlist.getInt("currentUserId"),
                 toDeleteFromWatchlist.getInt("filmToDeleteId")
                 );

                 response.getWriter().println("Film deleted from Watchlist");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        //get all Reviews Handler
        if (request.getReader() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getAllReviews")) {
            JSONObject reviews = new JSONObject(new JSONTokener(request.getReader()));

            try {
                // TODO Einkommentieren und prüfen
                JSONArray allreviews = DB.getAllFilmBewertungen(
                        reviews.getInt("filmId")
                );
                    response.getWriter().println(allreviews);
                baseRequest.setHandled(true);
            }
            catch (Exception e) {
                System.out.println("No Reviews");
                baseRequest.setHandled(false);
            }
        }

        //get all User Reviews Handler
        if (request.getReader() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getAllUserReviews")) {
            JSONObject userId = new JSONObject(new JSONTokener(request.getReader()));

            try {
                JSONArray userIdReviews = DB.getAllUserBewertungen(
                        userId.getInt("userId")
                );
                response.getWriter().println(userIdReviews);
                baseRequest.setHandled(true);
            }
            catch (Exception e) {
                System.out.println("No User Reviews");
                baseRequest.setHandled(false);
            }
        }

        //add new Review Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/addReview")) {
            JSONObject newReview = new JSONObject(new JSONTokener(request.getReader()));

            try {
                DB.addBewertung(
                        newReview.getInt("userId"),
                        newReview.getInt("filmId"),
                        newReview.getString("titel"),
                        newReview.getDouble("bewertung"),
                        newReview.getString("text"));
                response.getWriter().println(newReview);
                baseRequest.setHandled(true);
            } catch (Exception e) {
                System.out.println("Review not added");
                baseRequest.setHandled(false);
            }
        }

        //edit Review Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/editReview")) {
            JSONObject editedReview = new JSONObject(new JSONTokener(request.getReader()));

            try {
                DB.editBewertung(
                        editedReview.getInt("bewertungId"),
                        editedReview.getString("titel"),
                        editedReview.getInt("bewertung"),
                        editedReview.getString("text")
                );

                System.out.println("Review edited successful");
                System.out.println(editedReview.toString());

            } catch (Exception e) {

                System.out.println(e.getMessage());
                baseRequest.setHandled(false);
            }
        }

        //getUserFromReview Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getUserFromReview")) {
            JSONObject comment = new JSONObject(new JSONTokener(request.getReader()));
            try {

                Nutzer otherUser = DB.getAutorFromBewertung(comment.get("bewertungId").toString());

                response.getWriter().println(otherUser.toJSONObject().toString());
                baseRequest.setHandled(true);
            }
            catch (Exception e) {
                response.getWriter().println(e.getMessage());
                baseRequest.setHandled(false);
            }
        }

        //delete Report Handler
        if (request.getReader() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/deleteReport")) {
            JSONObject toDeleteReport = new JSONObject(new JSONTokener(request.getReader()));

            try {
                // TODO: 24.05.2022 Einkommentieren & prüfen
                 DB.removeReport(
                         toDeleteReport.getInt("reportId")
                 );

                 response.getWriter().println("Report deleted");

                baseRequest.setHandled(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        //get all Reports Handler
        if (request.getReader() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getAllReports")) {
            JSONObject reports = new JSONObject(new JSONTokener(request.getReader()));

            try {
                // TODO Einkommentieren und prüfen
                JSONArray allReports = DB.getAllReportsWeli();

                response.getWriter().println(allReports);
                baseRequest.setHandled(true);
            } catch (Exception e) {
                System.out.println("No Reviews");
                baseRequest.setHandled(false);
            }
        }

        //delete Review Handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/deleteReview")) {
            JSONObject reviewToDelete = new JSONObject(new JSONTokener(request.getReader()));

            try {
                DB.removeBewertung(
                        reviewToDelete.getInt("bewertungId")
                );

                System.out.println("Review edited successful");
                System.out.println(reviewToDelete.toString());
                response.getWriter().println(reviewToDelete);

                baseRequest.setHandled(true);

            } catch (Exception e) {

                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }



        // Hole Durchschnittsbewertung zu einem Film handler
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getAverageBewertung")) {
            JSONObject filmID = new JSONObject(new JSONTokener(request.getReader()));
            try{
                int avg = DB.getAvgBewertung(filmID.getInt("filmId"));
                System.out.println(avg);
                response.getWriter().println(avg);
                baseRequest.setHandled(true);
            } catch(Exception e){
                System.out.println("avg Bewertung");
                baseRequest.setHandled(false);
            }

        }


        //Filmstatistik holen
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getFilmstatistik")) {
            JSONObject filmID = new JSONObject(new JSONTokener(request.getReader()));
            try{
                JSONArray statistik = DB.getFilmstatistik(filmID.getInt("filmId"));
                System.out.println("ClientHandler");
                response.getWriter().println(statistik);
                baseRequest.setHandled(true);
            } catch(Exception e){
                System.out.println("Filmstatistik Download");
                baseRequest.setHandled(false);
            }

        }

        //Filmstatistik resetten
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/resetFilmstatistik")) {
            JSONObject filmID = new JSONObject(new JSONTokener(request.getReader()));
            try{
                DB.resetFilmstatistik(filmID.getInt("filmId"));
                response.getWriter().println("Filmstatistik resetted");
                baseRequest.setHandled(true);
            } catch(Exception e){
                System.out.println("Filmstatistik Download");
                baseRequest.setHandled(false);
            }

        }

        // get User Statistics Handler
        if (request.getReader() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/bestMovies")) {
            JSONObject userId = new JSONObject(new JSONTokener(request.getReader()));

            try {
                JSONArray userIdReviews = DB.userStatistics(
                        userId.getInt("userId"),
                        userId.getInt("von"),
                        userId.getInt("bis")
                );
                response.getWriter().println(userIdReviews);
                baseRequest.setHandled(true);
            }
            catch (Exception e) {
                System.out.println("No User Statistics");
                baseRequest.setHandled(false);
            }
        }

        //neue Einladung adden
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/addNewEinladung")) {
            System.out.println("Testerino");
            JSONObject newInvitation = new JSONObject(new JSONTokener(request.getReader()));
            Nutzer currentUser = new Nutzer(new JSONObject(newInvitation.getString("currentUser")));
            Nutzer invitedUser = new Nutzer(new JSONObject(newInvitation.getString("invitedUser")));

            try{
                DB.addEinladung(
                        currentUser.getId(),
                        invitedUser.getId(),
                        newInvitation.getString("filmName"),
                        newInvitation.getString("datum"),
                        newInvitation.getString("uhrzeit"),
                        newInvitation.getString("nachricht"));
                response.getWriter().println(newInvitation);
                baseRequest.setHandled(true);

                mailer.sendNewInvitationNotification(invitedUser,currentUser,
                        newInvitation.getString("filmName"),newInvitation.getString("datum"),newInvitation.getString("uhrzeit"),newInvitation.getString("nachricht"));
            } catch (Exception e) {
                System.out.println("Invitation wasn't added");
                baseRequest.setHandled(false);
            }

        }
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getAllInvitations")) {
            JSONObject user = new JSONObject(new JSONTokener(request.getReader()));

            try {
                ArrayList<Einladung> allPendingInvitations = DB.getAllEinladungen(user.getInt("currentUserId"));
                JSONArray result = new JSONArray();
                for (Einladung einladung: allPendingInvitations) {
                    result.put(einladung.toJSONObject());
                }
                response.getWriter().println(result);

                baseRequest.setHandled(true);
            } catch (Exception e){
                System.out.println(e.getMessage());

                baseRequest.setHandled(false);
            }
        }

        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/acceptInvitation")) {
            JSONObject invitation = new JSONObject(new JSONTokener(request.getReader()));

            try {
                System.out.println(invitation);

                DB.deleteEinladung(invitation.getInt("invitationSender"), invitation.getInt("currentUser"));
                response.getWriter().println("Invitation accepted");

                baseRequest.setHandled(true);

                mailer.sendNewInvitationAcceptedNotification(DB.getNutzerById(invitation.getInt("invitationSender")), DB.getNutzerById(invitation.getInt("currentUser")));

            } catch (Exception e){
                System.out.println(e);
                e.printStackTrace();

                baseRequest.setHandled(false);
            }
        }

        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/declineInvitation")) {
            JSONObject invitation = new JSONObject(new JSONTokener(request.getReader()));


            try {
                System.out.println(invitation);

                DB.deleteEinladung(invitation.getInt("invitationSender"), invitation.getInt("currentUser"));
                response.getWriter().println("Invitation declined");

                baseRequest.setHandled(true);
                mailer.sendNewInvitationDeclinedNotification(DB.getNutzerById(invitation.getInt("invitationSender")), DB.getNutzerById(invitation.getInt("currentUser")));
            } catch (Exception e){
                System.out.println(e);
                e.printStackTrace();
                baseRequest.setHandled(false);
            }
        }
        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/sumOpenInvitations")) {
            JSONObject invitation = new JSONObject(new JSONTokener(request.getReader()));
            try{
                response.getWriter().println(DB.countPendingInvitations(invitation.getInt("userID")));
                baseRequest.setHandled(true);

            } catch (Exception e){
                System.out.println(e);
                e.printStackTrace();
                baseRequest.setHandled(false);
            }
        }

        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/getAllMessages")) {
            JSONObject user = new JSONObject(new JSONTokener(request.getReader()));

            try{
                ArrayList<Message> allMessages = DB.getAllMessages(user.getInt("currentUserId"));
                JSONArray result = new JSONArray();
                for (Message message: allMessages){
                    result.put(message.toJSONObject());
                }
                response.getWriter().println(result);
                baseRequest.setHandled(true);
            } catch (Exception e){
                System.out.println(e.getMessage());
                baseRequest.setHandled(false);
            }
        }

        if (request.getMethod() != null && request.getMethod().equals("POST") && request.getPathInfo().equals("/deleteMessage")) {
            JSONObject message = new JSONObject(new JSONTokener(request.getReader()));

            try{
                System.out.println(message);

                DB.deleteMessage(message.getInt("messageId"));
                response.getWriter().println("Message deleted successful");

                baseRequest.setHandled(true);
            } catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
                baseRequest.setHandled(false);
            }
        }


    }
}
