package clientServer;

import clientServer.statistikuser.StatistikUser;
import com.google.gson.JsonObject;
import models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;

public class DB {
    public static Connection connection;
    public static ArrayList<String> categories = new ArrayList<>();
    public static ArrayList<String> directors = new ArrayList<>();

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/gruppe-b";
        String uname = "root";
        String pass = "";
        Connection conn;

        try {
            conn = DriverManager.getConnection(url, uname, pass);
            if (conn != null) {

                connection = conn;
            }
        } catch (SQLException e) {
            System.out.println("Oops error");
            e.printStackTrace();
        }

        createTables();
    }

    public static void createTables() {
        String film = "CREATE TABLE if NOT EXISTS film ("
                + "idNo INT(64) NOT NULL AUTO_INCREMENT,"
                + "name VARCHAR(200),"
                + "kategorie VARCHAR(200),"
                + "erscheinungsdatum VARCHAR(200),"
                + "regisseur VARCHAR(200),"
                + "drehbuchautor VARCHAR(200),"
                + "cast VARCHAR(400),"
                + "filmlaenge VARCHAR(200),"
                + "filmbanner VARCHAR(200),"
                + "PRIMARY KEY(idNo));";

        String admin = "CREATE TABLE if NOT EXISTS systemadmin (" +
                "idNo INT(64) NOT NULL AUTO_INCREMENT, " +
                "vorname VARCHAR(200), " +
                "nachname VARCHAR(200), " +
                "email VARCHAR(200), " +
                "passwort VARCHAR(200), " +
                "PRIMARY KEY(idNo));";

        String nutzer = "CREATE TABLE if NOT EXISTS nutzer (" +
                "idNo INT(64) NOT NULL AUTO_INCREMENT, " +
                "vorname VARCHAR(200), " +
                "nachname VARCHAR(200), " +
                "email VARCHAR(200), " +
                "passwort VARCHAR(200), " +
                "geburtstag VARCHAR(200), " +
                "profilbild VARCHAR(200), " +
                "PRIMARY KEY(idNo));";

        String freundesliste = "CREATE TABLE if NOT EXISTS freundesliste (" +
                "idNo1 INT(64) NOT NULL, " +
                "idNo2 INT(64) NOT NULL, " +
                "CONSTRAINT PK_Freundesliste Primary Key (idNo1, idNo2), " +
                "FOREIGN KEY (idNo1) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (idNo2) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String freundschaftsanfragen = "CREATE TABLE if NOT EXISTS freundschaftsanfragen (" +
                "idNo1 INT(64) NOT NULL, " +
                "idNo2 INT(64) NOT NULL, " +
                "CONSTRAINT PK_freundschaftsanfragen Primary Key (idNo1, idNo2), " +
                "FOREIGN KEY (idNo1) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (idNo2) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String watchlist = "CREATE TABLE if NOT EXISTS watchlist (" +
                "nutzerID INT(64) NOT NULL, " +
                "filmID INT(64) NOT NULL, " +
                "CONSTRAINT PK_Watchlist PRIMARY KEY (nutzerID, filmID), " +
                "FOREIGN KEY (nutzerID) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (filmID) REFERENCES film(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String seenList = "CREATE TABLE if NOT EXISTS seenList (" +
                "nutzerID INT(64) NOT NULL, " +
                "filmID INT(64) NOT NULL, " +
                "addedDate VARCHAR(200), " +
                "CONSTRAINT PK_AlreadyWatched PRIMARY KEY (nutzerID, filmID), " +
                "FOREIGN KEY (nutzerID) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (filmID) REFERENCES film(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String filmreport = "CREATE TABLE if NOT EXISTS filmreport (" +
                "idNo INT (64) NOT NULL AUTO_INCREMENT, " +
                "nutzerID INT(64) NOT NULL, " +
                "filmID INT (64) NOT NULL, " +
                "report VARCHAR (500), " +
                "PRIMARY KEY (idNo), " +
                "FOREIGN KEY (nutzerID) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (filmID) REFERENCES film(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String privacy = "CREATE TABLE if NOT EXISTS privacy (" +
                "nutzerID INT(64) NOT NULL, " +
                "freunde INT(64) NOT NULL, " +
                "watchlist INT(64) NOT NULL, " +
                "seenlist INT(64) NOT NULL, " +
                "bewertung INT(64) NOT NULL, " +
                "PRIMARY KEY (nutzerID), " +
                "FOREIGN KEY (nutzerID) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String bewertung = "CREATE TABLE if NOT EXISTS bewertung (" +
                "idNo INT (64) NOT NULL AUTO_INCREMENT, " +
                "nutzerID INT(64) NOT NULL, " +
                "filmID INT (64) NOT NULL, " +
                "titel TEXT," +
                "score DOUBLE NOT NULL, " +
                "text TEXT, " +
                "addedDate VARCHAR(200), " +
                "PRIMARY KEY (idNo), " +
                "FOREIGN KEY (nutzerID) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (filmID) REFERENCES film(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String filmStatistik = "CREATE TABLE if NOT EXISTS filmstatistik (" +
                "filmID INT(64) NOT NULL, " +
                "avgBewertung INT(8), " +
                "anzahlBewertung INT(16), " +
                "anzahlGesehen INT(16), " +
                "PRIMARY KEY (filmID), " +
                "FOREIGN KEY (filmID) REFERENCES film(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String diskussionsgruppen = "CREATE TABLE if NOT EXISTS diskussionsgruppen (" +
                "id INT(64) NOT NULL AUTO_INCREMENT, " +
                "name varchar(255) not null, " +
                "is_private boolean default false, " +
                "created_by INT(64) NOT NULL, " +
                "PRIMARY KEY (id), " +
                "FOREIGN KEY (created_by) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String diskussionsgruppen_nutzer = "CREATE TABLE if NOT EXISTS diskussionsgruppen_nutzer (" +
                "diskussionsgruppen_id INT(64) NOT NULL, " +
                "nutzer_id INT(64) NOT NULL, " +
                "CONSTRAINT pk_diskussionsgruppen_nutzer PRIMARY KEY (diskussionsgruppen_id, nutzer_id), " +
                "FOREIGN KEY (diskussionsgruppen_id) REFERENCES diskussionsgruppen(id) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (nutzer_id) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String filmeinladungen = "CREATE TABLE if NOT EXISTS filmeinladungen (" +
                "idNo1 INT(64) NOT NULL, " +
                "idNo2 INT(64) NOT NULL, " +
                "filmName VARCHAR(200) NOT NULL, " +
                "datum VARCHAR(200) NOT NULL, " +
                "uhrzeit VARCHAR(200) NOT NULL, " +
                "nachricht VARCHAR(200), " +
                "CONSTRAINT PK_filmeinladungen Primary Key (idNo1, idNo2), " +
                "FOREIGN KEY (idNo1) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (idNo2) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";

        String messages = "CREATE TABLE if NOT EXISTS messages (" +
                "messageId INT(64) NOT NULL AUTO_INCREMENT, " +
                "id INT(64) NOT NULL, " +
                "text TEXT(200) NOT NULL, " +
                "CONSTRAINT PK_messages Primary Key (messageId), " +
                "FOREIGN KEY (id) REFERENCES nutzer(idNo) ON UPDATE CASCADE ON DELETE CASCADE);";


        try {
            Statement statement = connection.createStatement();

            statement.execute(film);
            statement.execute(admin);
            statement.execute(nutzer);
            statement.execute(freundesliste);
            statement.execute(freundschaftsanfragen);
            statement.execute(watchlist);
            statement.execute(seenList);
            statement.execute(filmreport);
            statement.execute(privacy);
            statement.execute(bewertung);
            statement.execute(filmStatistik);
            statement.execute(diskussionsgruppen);
            statement.execute(diskussionsgruppen_nutzer);
            statement.execute(filmeinladungen);
            statement.execute(messages);
        } catch (SQLException e) {
            System.out.println("BAD SQL Error");
            e.printStackTrace();
        }
    }

    public static boolean checkAdmin(String email) throws SQLException {
        String sql = "Select * FROM systemadmin WHERE email = \"" + email + "\";";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(sql);
        if (rs.next()) {
            System.out.println("found");
            return true;
        }
        return false;

    }

    public static boolean checkNutzer(String email) throws SQLException {
        String sql = "Select * FROM nutzer WHERE email = \"" + email + "\";";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(sql);
        if (rs.next()) {
            System.out.println("found");
            return true;
        }
        return false;

    }

    public static void insertFilm(
            String name,
            String kategorie,
            String erscheinungsdatum,
            String regisseur,
            String drehbuchautor,
            String cast,
            String filmlaenge,
            String filmbanner
    ) throws SQLException {
        String sqlquery = "INSERT INTO film ( name , kategorie, erscheinungsdatum,regisseur,drehbuchautor,cast,filmlaenge, filmbanner) values (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sqlquery);


        pstmt.setString(1, name);
        pstmt.setString(2, kategorie);
        pstmt.setString(3, erscheinungsdatum);
        pstmt.setString(4, regisseur);
        pstmt.setString(5, drehbuchautor);
        pstmt.setString(6, cast);
        pstmt.setString(7, filmlaenge);
        pstmt.setString(8, filmbanner);

        pstmt.executeUpdate();

        //Film Statistik hinzufuegen
        String query = "SELECT max(idNo) AS id FROM film";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        rs.next();
        int id = rs.getInt("id");

        String query1 = "INSERT INTO filmstatistik (filmID, avgBewertung, anzahlBewertung, anzahlGesehen) values (?, ?, ?, ?);";
        PreparedStatement pstmt1 = connection.prepareStatement(query1);
        pstmt1.setInt(1, id);
        pstmt1.setInt(2, 0);
        pstmt1.setInt(3, 0);
        pstmt1.setInt(4, 0);
        pstmt1.executeUpdate();

    }

    public static JSONArray findAllElements() throws SQLException {
        String query = "Select * from film ";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        JSONArray array = new JSONArray();

        while (rs.next()) {
            JSONObject object = new JSONObject();

            Integer id = rs.getInt("idNo");
            String idString = id.toString();
            String name = rs.getString("name");
            String kategorie = rs.getString("kategorie");
            String erscheinungsdatum = rs.getString("erscheinungsdatum");
            String regisseur = rs.getString("regisseur");
            String drehbuchautor = rs.getString("drehbuchautor");
            String cast = rs.getString("cast");
            String filmlaenge = rs.getString("filmlaenge");
            String filmbanner = rs.getString("filmbanner");


            object.put("id", idString);
            object.put("name", name);
            object.put("kategorie", kategorie);
            object.put("erscheinungsdatum", erscheinungsdatum);
            object.put("regisseur", regisseur);
            object.put("drehbuchautor", drehbuchautor);
            object.put("cast", cast);
            object.put("filmlaenge", filmlaenge);
            try {
                object.put("filmbanner", Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of(filmbanner))));
                object.put("avgBewertung", getAvgBewertung(id));
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
            array.put(object);
        }
        return array;
    }

    public static Systemadministrator getAdmin(JsonObject credentials) throws Exception {
        System.out.println(credentials.get("mail"));
        System.out.println(credentials.get("password"));

        String query = "SELECT * FROM systemadmin WHERE email = '" + credentials.get("mail").getAsString() + "';";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);

        if (rs.next()) {
            System.out.println("User found");

            Integer id = rs.getInt("idNo");
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            String email = rs.getString("email");

            Systemadministrator object = new Systemadministrator(id, vorname, nachname, email);

            return object;
        }
        throw new Exception("User not found");
    }

    public static Nutzer getNutzer(JsonObject credentials) throws Exception {
        System.out.println(credentials.get("mail"));
        System.out.println(credentials.get("password"));

        String query = "SELECT * FROM nutzer WHERE email = '" + credentials.get("mail").getAsString() + "';";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);

        if (rs.next()) {
            System.out.println("User found");

            Integer id = rs.getInt("idNo");
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            String email = rs.getString("email");
            String password = rs.getString("passwort");
            String geburtstag = rs.getString("geburtstag");
            String profilbild = rs.getString("profilbild");

            Nutzer object = new Nutzer(id, vorname, nachname, email, password, geburtstag, profilbild);

            return object;
        }
        throw new Exception("User not found");
    }

    public static Nutzer getNutzerById(Integer nutzerId) throws Exception {

        String query = "SELECT * FROM nutzer WHERE idNo = '" + nutzerId + "';";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);

        if (rs.next()) {
            System.out.println("User found");

            Integer id = rs.getInt("idNo");
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            String email = rs.getString("email");
            String password = rs.getString("passwort");
            String geburtstag = rs.getString("geburtstag");
            String profilbild = rs.getString("profilbild");

            Nutzer object = new Nutzer(id, vorname, nachname, email, password, geburtstag, profilbild);

            return object;
        }
        throw new Exception("User not found");
    }


    public static Systemadministrator checkPasswordAdmin(String email, String passwort) throws Exception {
        String query = "SELECT * FROM systemadmin WHERE email = '" + email + "';";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        rs.next();

        if (rs.getString("passwort").equals(passwort)) {
            System.out.println("Passwort stimmt!");
            Integer id = rs.getInt("idNo");
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            String mail = rs.getString("email");

            return new Systemadministrator(id, vorname, nachname, mail);
        }
        throw new Exception("falsches Passwort eingegeben!");
    }

    public static Nutzer checkPasswortNutzer(String email, String passwort) throws Exception {
        String query = "SELECT * FROM nutzer WHERE email = '" + email + "';";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        rs.next();

        if (rs.getString("passwort").equals(passwort)) {
            System.out.println("Passwort stimmt!");
            Integer id = rs.getInt("idNo");
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            String mail = rs.getString("email");
            String geburtstag = rs.getString("geburtstag");
            String profilbild = rs.getString("profilbild");

            return new Nutzer(id, vorname, nachname, mail, "", geburtstag, profilbild);
        }
        throw new Exception("falsches Passwort eingegeben!");
    }

    public static Systemadministrator registerAdmin(Systemadministrator newUserData) throws Exception {
        if (checkAdmin(newUserData.getMail())) {
            throw new Exception("User couldn't be registered");
        }

        String query = "INSERT INTO systemadmin (vorname, nachname, email, passwort) values (?, ?, ?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, newUserData.getName());
        pstmt.setString(2, newUserData.getSurname());
        pstmt.setString(3, newUserData.getMail());
        pstmt.setString(4, newUserData.getPassword());
        System.out.println(pstmt.toString());

        pstmt.executeUpdate();
        System.out.println("done");
        JsonObject credentials = new JsonObject();
        credentials.addProperty("mail", newUserData.getMail());
        credentials.addProperty("password", newUserData.getPassword());

        return getAdmin(credentials); //return mit autogenerierter ID Nummer
    }

    public static void updateFilm(Film film) throws Exception {
        String query = "UPDATE film SET name = ?, kategorie = ?, erscheinungsdatum = ?, regisseur = ?, drehbuchautor = ?, cast = ?, filmlaenge = ?, filmbanner = ?" +
                "WHERE idNo = ?";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, film.getName());
        pstmt.setString(2, film.getKategorie());
        pstmt.setString(3, film.getErscheinungsdatum());
        pstmt.setString(4, film.getRegisseur());
        pstmt.setString(5, film.getDrehbuchautor());
        pstmt.setString(6, film.getCast());
        pstmt.setString(7, film.getFilmlaenge());
        pstmt.setString(8, film.getFilmbanner());
        pstmt.setString(9, film.getId().toString());

        pstmt.executeUpdate();
        System.out.println("film updated");
    }


    public static ArrayList<String> getAllEmailsFromAdmins() throws Exception {
        String query = "SELECT email FROM systemadmin";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        ArrayList<String> emails = new ArrayList<>();
        while (rs.next()) {
            emails.add(rs.getString("email"));
        }
        return emails;
    }

    public static Nutzer addNutzer(String vorname, String nachname, String email, String passwort, String geburtstag, String profilbild) throws Exception {
        if (checkNutzer(email)) {
            throw new Exception("User couldn't be registered");
        }

        String query = "INSERT INTO nutzer (vorname, nachname, email, passwort, geburtstag, profilbild) values (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, vorname);
        pstmt.setString(2, nachname);
        pstmt.setString(3, email);
        pstmt.setString(4, passwort);
        pstmt.setString(5, geburtstag);
        pstmt.setString(6, profilbild);
        System.out.println(pstmt.toString());

        pstmt.executeUpdate();
        System.out.println("done");
        JsonObject credentials = new JsonObject();
        credentials.addProperty("mail", email);
        credentials.addProperty("password", passwort);

        setDefaultSettings(credentials, 2, 2, 2, 2);

        return getNutzer(credentials); //return mit autogenerierter ID Nummer
    }


    public static Nutzer getAutorFromBewertung(String bewertungid) throws Exception {
        String query = "SELECT nutzer.idNo, vorname, nachname, email, geburtstag, profilbild FROM bewertung JOIN nutzer ON (bewertung.nutzerID = nutzer.idNo) WHERE bewertung.idNo = " + bewertungid + ";";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        rs.next();
        Integer id = rs.getInt("idNo");
        String vorname = rs.getString("vorname");
        String nachname = rs.getString("nachname");
        String mail = rs.getString("email");
        String geburtstag = rs.getString("geburtstag");
        String profilbild = rs.getString("profilbild");
        return new Nutzer(id, vorname, nachname, mail, "", geburtstag, profilbild);
    }

    public static int getAvgBewertung(Integer filmid) throws Exception {
        String query = "SELECT avgBewertung FROM filmstatistik WHERE filmID = " + filmid + ";";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        rs.next();
        return rs.getInt("avgBewertung");
    }

    private static void calcAvgBewertung(Integer filmid) throws Exception {
        String query = "SELECT AVG(score) as durchschnitt FROM bewertung WHERE filmid = " + filmid + " GROUP BY filmid;";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        rs.next();
        int avg = (int) Math.round(rs.getDouble("durchschnitt"));

        query = "UPDATE filmstatistik SET avgBewertung = ? WHERE filmID = ?;";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, avg);
        pstmt.setInt(2, filmid);
        pstmt.executeUpdate();
        System.out.println("avg bewertung updated");
    }

    public static ArrayList<Nutzer> getAllNutzer(Integer nutzerID) throws Exception {
        String query = "SELECT * FROM nutzer WHERE idNo <> " + nutzerID + ";";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        ArrayList<Nutzer> nutzer = new ArrayList<Nutzer>();
        while (rs.next()) {
            Integer id = rs.getInt("idNo");
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            String mail = rs.getString("email");
            String geburtstag = rs.getString("geburtstag");
            String profilbild = rs.getString("profilbild");
            Nutzer n = new Nutzer(id, vorname, nachname, mail, "", geburtstag, profilbild);
            nutzer.add(n);
        }
        return nutzer;
    }

    public static void deleteFreundschaftsanfrage(Integer nutzerID1, Integer nutzerID2) throws Exception {
        String query = "DELETE FROM freundschaftsanfragen WHERE (idNo1 = " + nutzerID1 + " AND idNo2 = " + nutzerID2 + ") OR (idNo1 = " + nutzerID2 + " AND idNo2 = " + nutzerID1 + ");";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.executeUpdate();
        System.out.println("deleted");
    }

    public static void acceptFreundschaftsanfrage(Integer nutzerid1, Integer nutzerid2) throws Exception {
        deleteFreundschaftsanfrage(nutzerid1, nutzerid2);
        String query = "INSERT INTO freundesliste (idNo1, idNo2) VALUES (?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, nutzerid1);
        pstmt.setInt(2, nutzerid2);
        pstmt.executeUpdate();
        System.out.println("done");
    }

    public static boolean checkFreundschaftsanfrage(Integer nutzerid1, Integer nutzerid2) throws Exception {
        String query = "SELECT * FROM freundschaftsanfragen WHERE (idNo1 = " + nutzerid1 + " AND idNo2 = " + nutzerid2 + ") OR (idNo1 = " + nutzerid2 + " AND idNo2 = " + nutzerid1 + ");";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        return rs.next();
    }

    public static ArrayList<Nutzer> getAllFreundschaftsanfragen(Integer nutzerid) throws Exception {
        String query = "SELECT idNo1 as id, vorname, nachname, email, geburtstag, profilbild FROM freundschaftsanfragen JOIN nutzer on IdNo1 = IdNo WHERE idNo2 = " + nutzerid;
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        ArrayList<Nutzer> freunde = new ArrayList<Nutzer>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            String mail = rs.getString("email");
            String geburtstag = rs.getString("geburtstag");
            String profilbild = rs.getString("profilbild");
            Nutzer n = new Nutzer(id, vorname, nachname, mail, "", geburtstag, profilbild);
            freunde.add(n);
        }
        return freunde;
    }

    public static void createFreundschaftsanfrage(Integer nutzerid1, Integer nutzerid2) throws Exception {
        if (checkFreundschaftsanfrage(nutzerid1, nutzerid2)) {
            throw new Exception("Anfrage existiert bereits");
        }
        String query = "INSERT INTO freundschaftsanfragen (idNo1, idNo2) VALUES (?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, nutzerid1);
        pstmt.setInt(2, nutzerid2);
        pstmt.executeUpdate();
        System.out.println("done");
    }

    public static void deleteFriend(Integer nutzerid1, Integer nutzerid2) throws Exception {
        String query = "DELETE FROM freundesliste WHERE (idNo1 = " + nutzerid1 + " AND idNo2 = " + nutzerid2 + ") OR (idNo1 = " + nutzerid2 + " AND idNo2 = " + nutzerid1 + ");";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.executeUpdate();
        System.out.println("deleted");
    }

    public static ArrayList<Nutzer> getFriends(Integer nutzerid) throws Exception {
        String query = "SELECT idNo1 AS id, vorname, nachname, email, geburtstag, profilbild FROM freundesliste JOIN nutzer ON (freundesliste.idNo1 = nutzer.idNo) WHERE idNo2 = " + nutzerid + " AND idNo1 <>" + nutzerid +
                " UNION SELECT idNo2 AS id, vorname, nachname, email, geburtstag, profilbild FROM freundesliste JOIN nutzer ON (freundesliste.idNo2 = nutzer.idNo) WHERE idNo1 = " + nutzerid + " AND idNo2 <>" + nutzerid + ";";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        ArrayList<Nutzer> freunde = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            String mail = rs.getString("email");
            String geburtstag = rs.getString("geburtstag");
            String profilbild = rs.getString("profilbild");
            Nutzer n = new Nutzer(id, vorname, nachname, mail, "", geburtstag, profilbild);
            freunde.add(n);
        }
        return freunde;
    }

    public static void addBewertung(Integer userID, Integer filmID, String titel, Double score, String comment) throws Exception {
        String Query = "INSERT INTO bewertung (nutzerID, filmID, titel, score, text, addedDate) values (?, ?, ?, ?, ?, ?)";

        LocalDateTime a = LocalDateTime.now();
        DateTimeFormatter b = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        PreparedStatement pstmt = connection.prepareStatement(Query);
        pstmt.setInt(1, userID);
        pstmt.setInt(2, filmID);
        pstmt.setString(3, titel);
        pstmt.setDouble(4, score);
        pstmt.setString(5, comment);
        pstmt.setString(6, a.format(b));

        pstmt.executeUpdate();
        System.out.println("done");
        calcAvgBewertung(filmID);
        addToAnzahlBewertung(filmID);
    }

    public static void editBewertung(Integer bewertungId, String titel, Integer score, String comment) throws Exception {
        if (!checkBewertung(bewertungId)) {
            throw new Exception("Bewertung existiert gar nicht");
        }
        String query = "UPDATE bewertung SET titel = ?, score = ?, text = ? WHERE idNo = " + bewertungId + ";";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, titel);
        pstmt.setInt(2, score);
        pstmt.setString(3, comment);

        pstmt.executeUpdate();
        System.out.println("review updated");
        //avg Bewertung updaten
        query = "SELECT filmID FROM bewertung WHERE idNo = " + bewertungId + ";";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        rs.next();
        int id = rs.getInt("filmID");
        calcAvgBewertung(id);
    }

    private static boolean checkBewertung(Integer bewertungId) throws Exception {
        String query = "SELECT * FROM bewertung WHERE idNo = " + bewertungId + ";";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        return rs.next();
    }

    public static void removeBewertung(Integer bewertungid) throws Exception {
        String Query = "DELETE FROM bewertung WHERE idNo = " + bewertungid + ";";

        connection.createStatement().executeUpdate(Query);
        System.out.println("done");
    }

    public static JSONArray getAllUserBewertungen(Integer userId) throws Exception {
        String Query = "SELECT * FROM bewertung WHERE nutzerID = " + userId + ";";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(Query);
        JSONArray z = new JSONArray();

        while (rs.next()) {
            JSONObject a = new JSONObject();

            Integer id = rs.getInt("idNo");
            String idString = id.toString();
            int userID = rs.getInt("nutzerID");
            int filmID = rs.getInt("filmID");
            String titel = rs.getString("titel");
            int score = rs.getInt("score");
            String comment = rs.getString("text");

            a.put("id", idString);
            a.put("userID", userID);
            a.put("filmID", filmID);
            a.put("titel", titel);
            a.put("score", score);
            a.put("comment", comment);

            z.put(a);
        }
        return z;
    }

    public static JSONArray getAllFilmBewertungen(Integer filmId) throws Exception {
        String Query = "SELECT * FROM bewertung WHERE filmID = " + filmId + ";";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(Query);
        JSONArray z = new JSONArray();

        while (rs.next()) {
            JSONObject a = new JSONObject();

            Integer id = rs.getInt("idNo");
            String idString = id.toString();
            int userID = rs.getInt("nutzerID");
            int filmID = rs.getInt("filmID");
            String titel = rs.getString("titel");
            int score = rs.getInt("score");
            String comment = rs.getString("text");

            a.put("id", idString);
            a.put("userID", userID);
            a.put("filmID", filmID);
            a.put("titel", titel);
            a.put("score", score);
            a.put("comment", comment);

            z.put(a);
        }
        return z;
    }

    public static void addToWatchlist(int userID, int filmID) throws Exception {
        String Query = "INSERT INTO watchlist (nutzerID, FilmID) values (?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(Query);
        pstmt.setInt(1, userID);
        pstmt.setInt(2, filmID);

        pstmt.executeUpdate();
        System.out.println("done");
    }

    public static void removeFromWatchlist(Integer userId, Integer filmId) throws Exception {
        String Query = "DELETE FROM watchlist WHERE nutzerID = " + userId + " AND filmID = " + filmId + ";";

        connection.createStatement().executeUpdate(Query);
        System.out.println("done");
    }

    public static JSONArray getAllFromWatchlistweli(Integer userId) throws Exception {
        // String Query = "SELECT film.idNo,film.name,film.kategorie,film.erscheinungsdatum,film.regisseur,film.drehbuchautor,film.cast,film.filmlaenge,watchlist.nutzerID FROM film,watchlist WHERE nutzerID = " + nutzer.getId() + ";";
        String Query = "SELECT film.idNo,film.name,film.kategorie,film.erscheinungsdatum,film.regisseur,film.drehbuchautor,film.cast,film.filmlaenge, film.filmbanner FROM watchlist JOIN film on watchlist.filmID =film.idNo WHERE nutzerID = " + userId + ";";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(Query);
        JSONArray array = new JSONArray();

        while (rs.next()) {
            JSONObject object = new JSONObject();

            Integer id = rs.getInt("idNo");
            String idString = id.toString();
            String name = rs.getString("name");
            String kategorie = rs.getString("kategorie");
            String erscheinungsdatum = rs.getString("erscheinungsdatum");
            String regisseur = rs.getString("regisseur");
            String drehbuchautor = rs.getString("drehbuchautor");
            String cast = rs.getString("cast");
            String filmlaenge = rs.getString("filmlaenge");

            object.put("id", idString);
            object.put("name", name);
            object.put("kategorie", kategorie);
            object.put("erscheinungsdatum", erscheinungsdatum);
            object.put("regisseur", regisseur);
            object.put("drehbuchautor", drehbuchautor);
            object.put("cast", cast);
            object.put("filmlaenge", filmlaenge);
            object.put("filmbanner", Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of(rs.getString("filmbanner")))));
            array.put(object);

        }
        return array;
    }

    public static void addToSeenList(int userID, int filmID) throws Exception {
        String Query = "INSERT INTO seenList (nutzerID, FilmID, addedDate) values (?, ?, ?)";

        LocalDateTime a = LocalDateTime.now();
        DateTimeFormatter b = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        PreparedStatement pstmt = connection.prepareStatement(Query);
        pstmt.setInt(1, userID);
        pstmt.setInt(2, filmID);
        pstmt.setString(3, a.format(b));

        pstmt.executeUpdate();
        System.out.println("done");
        addToAnzahlGesehen(filmID);
    }

    public static void removeFromSeenList(Integer nutzer, Integer film) throws Exception {
        String Query = "DELETE FROM seenList WHERE nutzerID = " + nutzer + " AND filmID = " + film + ";";

        connection.createStatement().executeUpdate(Query);
        System.out.println("done");
        subToAnzahlGesehen(film);
    }


    public static void addReport(int userID, int filmID, String report) throws Exception {
        String Query = "INSERT INTO filmreport (nutzerID, filmID, report) values (?, ?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(Query);
        pstmt.setInt(1, userID);
        pstmt.setInt(2, filmID);
        pstmt.setString(3, report);

        pstmt.executeUpdate();
        System.out.println("done");
    }

    public static void removeReport(int ReportID) throws Exception {
        String Query = "DELETE FROM filmreport WHERE idNo = " + ReportID + ";";

        connection.createStatement().executeUpdate(Query);
        System.out.println("done");
    }

    public static JSONArray getAllReportsWeli() throws Exception {
        String Query = "SELECT filmreport.idNo,film.name,filmreport.report FROM filmreport JOIN film on filmreport.filmID =film.idNo";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(Query);
        JSONArray z = new JSONArray();

        while (rs.next()) {
            JSONObject a = new JSONObject();

            int idNo = rs.getInt("idNo");
            String filmname = rs.getString("name");
            String report = rs.getString("report");
            System.out.println(filmname + " " + report);

            a.put("idNo", idNo);
            a.put("report", report);
            a.put("name", filmname);

            z.put(a);
        }
        return z;
    }

    public static void editPrivacy(int userId, int freundesliste, int watchlist, int geseheneFilme, int bewertung) throws Exception {
        String query = "UPDATE privacy SET freunde = ?, watchlist = ?, seenlist = ?, bewertung = ? WHERE nutzerID = " + userId + ";";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, freundesliste);
        pstmt.setInt(2, watchlist);
        pstmt.setInt(3, geseheneFilme);
        pstmt.setInt(4, bewertung);

        pstmt.executeUpdate();
        System.out.println("settings updated");

    }


    public static JSONObject getPrivacySettings(Integer userId) throws Exception {
        String Query = "SELECT * FROM privacy WHERE nutzerID = " + userId + ";";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(Query);
        JSONObject a = new JSONObject();
        rs.next();

        int userID = rs.getInt("nutzerID");
        int friends = rs.getInt("freunde");
        int watchlist = rs.getInt("watchlist");
        int seenlist = rs.getInt("seenlist");
        int bewertung = rs.getInt("bewertung");

        a.put("userID", userID);
        a.put("friends", friends);
        a.put("watchlist", watchlist);
        a.put("seenlist", seenlist);
        a.put("bewertung", bewertung);

        return a;

    }

    public static JSONArray getAllFromSeenlist(Integer userId) throws Exception {
        // String Query = "SELECT film.idNo,film.name,film.kategorie,film.erscheinungsdatum,film.regisseur,film.drehbuchautor,film.cast,film.filmlaenge,watchlist.nutzerID FROM film,watchlist WHERE nutzerID = " + nutzer.getId() + ";";
        String Query = "SELECT film.idNo,film.name,film.kategorie,film.erscheinungsdatum,film.regisseur,film.drehbuchautor,film.cast,film.filmlaenge FROM seenlist JOIN film on seenlist.filmID =film.idNo WHERE nutzerID = " + userId + ";";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(Query);
        JSONArray array = new JSONArray();

        while (rs.next()) {
            JSONObject object = new JSONObject();

            Integer id = rs.getInt("idNo");
            String idString = id.toString();
            String name = rs.getString("name");
            String kategorie = rs.getString("kategorie");
            String erscheinungsdatum = rs.getString("erscheinungsdatum");
            String regisseur = rs.getString("regisseur");
            String drehbuchautor = rs.getString("drehbuchautor");
            String cast = rs.getString("cast");
            String filmlaenge = rs.getString("filmlaenge");
            System.out.println(name);

            object.put("id", idString);
            object.put("name", name);
            object.put("kategorie", kategorie);
            object.put("erscheinungsdatum", erscheinungsdatum);
            object.put("regisseur", regisseur);
            object.put("drehbuchautor", drehbuchautor);
            object.put("cast", cast);
            object.put("filmlaenge", filmlaenge);
            array.put(object);

        }
        return array;
    }

    public static JSONArray getDiscussionGroups(int nutzerId) throws Exception {
        String Query = "select dg.id,dg.name, case when dg.created_by = " + nutzerId + " or (select count(*) from diskussionsgruppen_nutzer dn where dn.diskussionsgruppen_id = dg.id and dn.nutzer_id = " + nutzerId + ") then 1 else 0 end as is_member from diskussionsgruppen dg where not dg.is_private or dg.created_by = " + nutzerId + " or dg.created_by in (" +
                "SELECT idNo1 AS id FROM freundesliste WHERE idNo2 = " + nutzerId + " AND idNo1 <> " + nutzerId + " " +
                "UNION SELECT idNo2 AS id FROM freundesliste WHERE idNo1 = " + nutzerId + " AND idNo2 <> " + nutzerId +
                ")";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(Query);
        JSONArray groups = new JSONArray();

        while (rs.next()) {
            groups.put(new JSONObject().put("id", rs.getInt("id")).put("name", rs.getString("name")).put("isMember", rs.getBoolean("is_member")));
        }
        return groups;
    }

    public static void joinDiscussionGroup(int groupId, int userId) throws Exception {
        connection.createStatement().execute("insert into diskussionsgruppen_nutzer (diskussionsgruppen_id, nutzer_id) values (" + groupId + ", " + userId + ");");
    }

    public static void createDiscussionGroup(String groupName, boolean isPrivate, int userId) throws Exception {
        String query = "select * from diskussionsgruppen where name ='" + groupName + "';";
        if (connection.createStatement().executeQuery(query).next())
            throw new Exception("Eine Gruppe mit diesem Namen existiert bereits.");

        connection.createStatement().execute("insert into diskussionsgruppen (name, is_private, created_by) values ('" + groupName + "', " + isPrivate + ", " + userId + ");");
    }

    public static void setDefaultSettings(JsonObject credentials, int freunde, int watchlist, int seenlist, int bewertung) throws Exception {
        String query = "INSERT INTO privacy (nutzerID, freunde, watchlist, seenlist, bewertung) values (?, ?, ?, ?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, getNutzer(credentials).getId());
        pstmt.setInt(2, freunde);
        pstmt.setInt(3, watchlist);
        pstmt.setInt(4, seenlist);
        pstmt.setInt(5, bewertung);
        System.out.println(pstmt);

        pstmt.executeUpdate();
    }

    public static ArrayList<Integer> getFriendsIds(Integer nutzerid) throws Exception {
        String query = "SELECT idNo1 AS id FROM freundesliste JOIN nutzer ON (freundesliste.idNo1 = nutzer.idNo) WHERE idNo2 = " + nutzerid + " AND idNo1 <>" + nutzerid +
                " UNION SELECT idNo2 AS id FROM freundesliste JOIN nutzer ON (freundesliste.idNo2 = nutzer.idNo) WHERE idNo1 = " + nutzerid + " AND idNo2 <>" + nutzerid + ";";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        ArrayList<Integer> freunde = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");

            freunde.add(id);
        }
        return freunde;
    }
    public static void myfriendsTopratedcategory( Integer id) throws Exception {
        ArrayList<Integer> friendsIds= getFriendsIds(id);

        StringBuilder builderdirectors = new StringBuilder();
        //simlar();
        builderdirectors.append("(");

        for( int i = 0 ; i < friendsIds.size(); i++ ) {

            builderdirectors.append( "'"+friendsIds.get(i)+"'"+",");

        }

        builderdirectors.append(")");
        String placeholderfriendsIds=  builderdirectors.deleteCharAt( builderdirectors.length() -2).toString();
        System.out.println( "ids  :" +placeholderfriendsIds);


        String query = "SELECT nutzer.idNo , film.idNo,film.name , film.kategorie,film.regisseur, score FROM nutzer INNER JOIN bewertung" +
                " on nutzer.idNo =bewertung.nutzerID INNER JOIN film on bewertung.filmID=film.idNo INNER JOIN seenlist on film.idNo =seenlist.filmID " +
                "WHERE bewertung.score >=3 AND nutzer.idNo IN "+ placeholderfriendsIds+"GROUP BY film.idNo ASC LIMIT 10";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        while (rs.next()) {

            String n = rs.getString("name");
            int score = rs.getInt("score");
            int idno =rs.getInt("idNo");

            String cat = rs.getString("kategorie");
            String regis = rs.getString("regisseur");

            if (!categories.contains(cat)) categories.add(cat);
             if(!directors.contains(regis)) directors.add(regis);

        }
        System.out.println(" mr noby kam vorbei");

    }

    public static void myTopRatedCategoriesAndDirectors(Integer userId ) throws SQLException {

        String query = "SELECT film.kategorie,film.regisseur, score  FROM bewertung INNER JOIN film on bewertung.filmID=film.idNo INNER JOIN seenlist " +
                       "on film.idNo =seenlist.filmID WHERE score >=3 AND bewertung.nutzerID =" + userId + " GROUP BY film.idNo ASC LIMIT 10";

        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        while (rs.next()) {
            String category = rs.getString("kategorie");
            String regisseur = rs.getString("regisseur");
            if (!categories.contains(category)) categories.add(category);
            if(!directors.contains(regisseur)) directors.add(regisseur);
        }
    }

    public static JSONArray getSimilarMovies(Integer userId ,String recommendationMethode) throws Exception {
        if(recommendationMethode.equals("recomenndedbasedonmyseenlist")){
            myTopRatedCategoriesAndDirectors(userId);
        }
        if(recommendationMethode.equals("recomenndedbasedonfriendsseenlist")){
            myfriendsTopratedcategory(userId);
        }
        System.out.println("length categories :"+categories.size() +"lenght re : "+directors.size());

        ArrayList<RecommendedMovies> similars = new ArrayList<>();

        System.out.println("length categories :"+categories.size() +"lenght re : "+directors.size());

        JSONArray array = new JSONArray();

        StringBuilder builderCategories = new StringBuilder();
        builderCategories.append("(");
        for (int i = 0; i < categories.size(); i++) {
            builderCategories.append("'" + categories.get(i) + "'" + ",");
        }
        builderCategories.append(")");
        String placeHoldersCategories = builderCategories.deleteCharAt(builderCategories.length() - 2).toString();
        System.out.println(placeHoldersCategories);
        //change Directos array to stringbuilder

        StringBuilder builderdirectors = new StringBuilder();
        //simlar();
        builderdirectors.append("(");

        for( int i = 0 ; i < directors.size(); i++ ) {

            builderdirectors.append( "'"+directors.get(i)+"'"+",");

        }

        builderdirectors.append(")");
        String placeHoldersdirectors =  builderdirectors.deleteCharAt( builderdirectors.length() -2).toString();

        String query1 = "Select * from film WHERE film.idNo NOT IN (SELECT filmID FROM seenlist WHERE seenlist.nutzerID ="+userId+") " +
                "AND kategorie IN " + placeHoldersCategories + " AND regisseur IN "+ placeHoldersdirectors+" ORDER BY film.idNo LIMIT  15";
        Statement sta1 = connection.createStatement();
        ResultSet rs1 = sta1.executeQuery(query1);

        while (rs1.next()) {
            String name = rs1.getString("name");
            String category = rs1.getString("kategorie");
            String regisseur = rs1.getString("regisseur");
            RecommendedMovies movie = new RecommendedMovies(name, category, regisseur, "");
            similars.add(movie);
        }

        int limt =15-similars.size();
        String query2 = "Select * from film WHERE film.idNo NOT IN (SELECT filmID FROM seenlist WHERE seenlist.nutzerID ="+userId+") " +
                "AND kategorie IN " + placeHoldersCategories + "  ORDER BY RAND() LIMIT  "+limt+"";
        Statement sta2 = connection.createStatement();
        ResultSet rs2 = sta2.executeQuery(query2);

        while (rs2.next()) {
            System.out.println(limt);
            String name = rs2.getString("name");
            String category = rs2.getString("kategorie");
            String regisseur = rs2.getString("regisseur");
            RecommendedMovies movie = new RecommendedMovies(name, category, regisseur, "");
            similars.add(movie);
        }


        for (int i = 0; i < similars.size(); i++) {
            JSONObject object = new JSONObject();

            if (directors.contains(similars.get(i).getDirector())) {
                similars.get(i).setAehnlichkeitsScore("100%");

            } else {
                similars.get(i).setAehnlichkeitsScore("50%");
            }
            String name = similars.get(i).getName();
            String category = similars.get(i).getKategory();
            String director = similars.get(i).getDirector();
            String aenlichkeit = similars.get(i).getAehnlichkeitsScore();
            object.put("name", name);
            object.put("category", category);
            object.put("regisseur", director);
            object.put("aenlichkeit", aenlichkeit);
            array.put(object);
        }
        directors.clear();
        categories.clear();
        return array;

    }
    public static JSONArray getFilmstatistik(Integer filmID) throws Exception{
        String query = "SELECT * FROM filmstatistik WHERE filmID =" + filmID;
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        rs.next();
        int[] statistik = new int[3];
        statistik[0] = rs.getInt("avgBewertung");
        statistik[1] = rs.getInt("anzahlBewertung");
        statistik[2] = rs.getInt("anzahlGesehen");

        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();

        object.put("avgBewertung", String.valueOf(statistik[0]));
        object.put("anzahlBewertung",String.valueOf(statistik[1]));
        object.put("anzahlGesehen",String.valueOf(statistik[2]));
        array.put(object);

        return array;
    }

    public static void addToAnzahlBewertung(Integer filmID) throws Exception{
        int anzahl = getFilmstatistik(filmID).getJSONObject(0).getInt("anzahlBewertung") + 1;
        String query = "UPDATE filmstatistik SET anzahlBewertung = ? WHERE filmID = "+filmID + ";";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, anzahl);
        pstmt.executeUpdate();
    }

    public static void addToAnzahlGesehen(Integer filmID) throws Exception{
        int anzahl = getFilmstatistik(filmID).getJSONObject(0).getInt("anzahlGesehen") + 1;
        String query = "UPDATE filmstatistik SET anzahlGesehen = ? WHERE filmID = "+filmID + ";";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, anzahl);
        pstmt.executeUpdate();
    }

    public static void subToAnzahlGesehen(Integer filmID) throws Exception{
        int anzahl = getFilmstatistik(filmID).getJSONObject(0).getInt("anzahlGesehen") - 1;
        String query = "UPDATE filmstatistik SET anzahlGesehen = ? WHERE filmID = "+filmID + ";";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, anzahl);
        pstmt.executeUpdate();
    }

    public static void resetFilmstatistik(Integer filmID) throws Exception{
        String query = "UPDATE filmstatistik SET avgBewertung = ?, anzahlBewertung = ?,  anzahlGesehen = ? WHERE filmID = "+ filmID + ";";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, 0);
        pstmt.setInt(2, 0);
        pstmt.setInt(3, 0);
        pstmt.executeUpdate();
    }

    public static void addEinladung(Integer idNo1, Integer idNo2, String filmName, String date, String time, String message) throws Exception{
        String query = "INSERT INTO filmeinladungen (idNo1, idNo2, filmname, datum, uhrzeit, nachricht) values (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, idNo1);
        pstmt.setInt(2, idNo2);
        pstmt.setString(3,filmName);
        pstmt.setString(4, date);
        pstmt.setString(5,time);
        pstmt.setString(6,message);

        pstmt.executeUpdate();

    }

    public static ArrayList<Einladung> getAllEinladungen(Integer nutzerid) throws SQLException {
        String query = "SELECT idNo1, idNo2, vorname, nachname, filmname, datum, uhrzeit, nachricht FROM filmeinladungen JOIN nutzer on IdNo1 = IdNo WHERE idNo2 = " + nutzerid;
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        ArrayList<Einladung> einladungen = new ArrayList<Einladung>();
        while (rs.next()){
            Integer idNo1 = rs.getInt("idNo1");
            Integer idNo2 = rs.getInt("idNo2");
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            String filmName = rs.getString("filmname");
            String datum = rs.getString("datum");
            String uhrzeit = rs.getString("uhrzeit");
            String nachricht = rs.getString("nachricht");

            Einladung e = new Einladung(idNo1,idNo2,vorname,nachname,nachricht,filmName,datum,uhrzeit);
            einladungen.add(e);
        }
        return einladungen;
    }

    public static void deleteEinladung(Integer idNo1, Integer idNo2) throws Exception{
        String query = "DELETE FROM filmeinladungen WHERE(idNo1 = " + idNo1 + " AND idNo2 = " + idNo2 + " )";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.executeUpdate();
        System.out.println("deleted");
    }

    public static int countPendingInvitations(Integer userId) throws Exception{
        String query = "SELECT COUNT(filmname) as anzahl FROM filmeinladungen WHERE idNo2 = " + userId;
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        rs.next();
        return rs.getInt("anzahl");

    }


    public static JSONArray userStatistics(Integer userId, Integer von, Integer bis) throws Exception {
        String query = "SELECT film.kategorie,film.cast,film.filmlaenge,addedDate FROM seenlist JOIN film on seenlist.filmID =film.idNo WHERE nutzerID = " + userId + ";";
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        JSONObject kategorie = new JSONObject();
        JSONObject cast = new JSONObject();
        JSONObject filmlaenge = new JSONObject();
        JSONArray a = new JSONArray();
        StringBuilder kategorie1 = new StringBuilder();
        StringBuilder cast1 = new StringBuilder();
        int filmlaenge1=0;

            while (rs.next()) {
                String addedDate = rs.getString("addedDate");
                Integer addedDateInt = Integer.parseInt(addedDate.substring(0,4)+addedDate.substring(5,7)+addedDate.substring(8,10));

                if(addedDateInt>=von&&addedDateInt<=bis) {
                    kategorie1.append(rs.getString("kategorie"));
                    kategorie1.append(", ");
                    cast1.append(rs.getString("cast"));
                    cast1.append(", ");
                    String filmlaenge2 = rs.getString("filmlaenge");
                    filmlaenge1+= Integer.parseInt(filmlaenge2.substring(0,filmlaenge2.indexOf(" ")));
                }
            }

            filmlaenge.put("filmlaengeGes", filmlaenge1);
            kategorie.put("kategorien", kategorie1.toString());
            cast.put("casts", cast1.toString());

            query = "SELECT film.name, score, bewertung.addedDate  FROM bewertung INNER JOIN film on bewertung.filmID=film.idNo INNER JOIN seenlist on film.idNo =seenlist.filmID WHERE score >=3 AND bewertung.nutzerID =" + userId + ";";
            sta = connection.createStatement();
            rs = sta.executeQuery(query);

            while (rs.next()) {
                String addedDate = rs.getString("addedDate");
                Integer addedDateInt = Integer.parseInt(addedDate.substring(0,4)+addedDate.substring(5,7)+addedDate.substring(8,10));
                if(addedDateInt>=von&&addedDateInt<=bis) {
                    String name = rs.getString("name");
                    int score = rs.getInt("score");

                    JSONObject b = new JSONObject();
                    b.put("name", name);
                    b.put("score", score);

                    a.put(b);
                }
            }


        return StatistikUser.getData(kategorie,cast,filmlaenge,a);
    }

    public static void addMessage(Integer id, String text) throws SQLException {
        String query = "INSERT INTO messages (id, text) values (?,?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, text);

        preparedStatement.executeUpdate();
    }

    public static void deleteMessage(Integer messageId) throws SQLException{
        String query = "DELETE FROM messages WHERE(messageId = " + messageId + " ) ";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
        System.out.println("deleted");
    }

    public static ArrayList<Message> getAllMessages(Integer nachrichtId) throws SQLException {
        String query = "SELECT messageId, id, text FROM messages WHERE id = " + nachrichtId;
        Statement sta = connection.createStatement();
        ResultSet rs = sta.executeQuery(query);
        ArrayList<Message> messages = new ArrayList<Message>();

        while (rs.next()){
            Integer messageId = rs.getInt("messageId");
            Integer id = rs.getInt("id");
            String text = rs.getString("text");

            Message m = new Message(messageId, id, text);
            messages.add(m);
        }
        return messages;
    }
}
