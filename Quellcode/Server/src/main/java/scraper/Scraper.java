package scraper;

import clientServer.DB;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import models.Film;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Scraper implements Runnable {

    WebClient client = new WebClient();
    HtmlPage imdb;
    HtmlPage targetUrl;
    String URL;

    private String kategorie;
    private String von;
    private String bis;

    public Scraper(String kategorie, String von, String bis) {
        super();
        this.kategorie = kategorie;
        this.von = von;
        this.bis = bis;
    }

    public void run() {
        String releaseDate = von + "," + bis;
        try {
            if (releaseDate.equals(",") && kategorie.equals("")) {
                System.out.println("Keine Eingabe!");
                throw new IOException("Fehler: Keine Eingabe");
            } else if ((von.equals("") && !bis.equals("") && !kategorie.equals("")) || (!von.equals("") && bis.equals("") && !kategorie.equals("")) || (von.equals("") && !bis.equals("") && kategorie.equals("")) || (!von.equals("") && bis.equals("") && kategorie.equals(""))) {
                System.out.println("Zeitraum unvollständig!");
                throw new IOException("Fehler: Zeitraum unvollständig");

            } else if (!releaseDate.equals(",") && kategorie.equals("")) {
                if (Integer.parseInt(von.substring(0, 4) + von.substring(5, 7) + von.substring(8, 10)) <= Integer.parseInt(bis.substring(0, 4) + bis.substring(5, 7) + bis.substring(8, 10))) {
                    URL = "https://www.imdb.com/search/title/?title_type=tv_movie&release_date=" + releaseDate + "&count=150";
                } else {
                    System.out.println("Von Zeitpunkt darf nicht später als Bis Zeitpunkt sein");
                    throw new IOException("Fehler: Von Zeitpunkt nicht vor Bis");
                }
            } else if (releaseDate.equals(",") && !kategorie.equals("")) {
                URL = "https://www.imdb.com/search/title/?title_type=tv_movie&genres=" + kategorie + "&count=150";
            } else {
                if (Integer.parseInt(von.substring(0, 4) + von.substring(5, 7) + von.substring(8, 10)) <= Integer.parseInt(bis.substring(0, 4) + bis.substring(5, 7) + bis.substring(8, 10))) {
                    URL = "https://www.imdb.com/search/title/?title_type=tv_movie&release_date=" + releaseDate + "&genres="
                            + kategorie + "&count=150";
                } else {
                    System.out.println("Von Zeitpunkt darf nicht später als Bis Zeitpunkt sein");
                    throw new IOException("Fehler: Von Zeitpunkt nicht vor Bis");
                }
            }

            collectData(URL);
        } catch (IOException | SQLException e) {
            System.out.println("Fehler beim Sammeln der Daten");
            e.printStackTrace();
        }

    }

    public void collectData(String url) throws IOException, SQLException {

        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        imdb = client.getPage(url);
        String target = "https://www.imdb.com";
        int numberofresults = 0;

        FileWriter DataWriter = new FileWriter("./Scraping.log", true);
        String results = imdb.getFirstByXPath("//div[@class='article']//div[1]//div[@class='desc']//span[1]/text()").toString();
        if (results.equals("No results.")) {
            DataWriter.write("No results found" + "\n\n");

        } else {
            if (results.substring(2, 5).equals("150")) {
                numberofresults = 150;
            } else {
                int a = results.indexOf(" ");
                String substring = results.substring(0, a);
                numberofresults = Integer.valueOf(substring);
            }
        }

        for (int i = 1; i <= numberofresults; i++) {
            StringBuilder cast = new StringBuilder();
            StringBuilder dbautor = new StringBuilder();
            StringBuilder directors = new StringBuilder();
            Film neu = new Film();
            HtmlAnchor anchor = imdb.getFirstByXPath("//div[@class='lister-list']//div[" + i + "]//div[3]//h3//a");
            Object a = anchor.getHrefAttribute();
            targetUrl = client.getPage(target + a.toString());
            Date date = new Date();

            // Drehbuchautor-----------------------------------------------------------------------------------------
            for (int index = 1; targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[" + index + "]//span/text()") != null && targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[" + index + "]//a[1]/text()") != null; index++) {
                if (targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[" + index + "]//span/text()").toString().equals("Writer") || targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[" + index + "]//span/text()").toString().equals("Writers") || targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[" + index + "]//a[1]/text()").toString().equals("Writer") || targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[" + index + "]//a[1]/text()").toString().equals("Writers")) {
                    for (int x = 1; targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[" + index + "]//div[1]//ul[1]//li[" + x + "]//a[1]/text()") != null; x++) {
                        dbautor.append(targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[" + index + "]//div[1]//ul[1]//li[" + x + "]//a[1]/text()").toString());
                        if (targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[" + index + "]//div[1]//ul[1]//li[" + (x + 1) + "]//a[1]/text()") != null) {
                            dbautor.append(", ");
                        }
                    }
                }
            }
            if (dbautor.toString().equals("")) {
                neu.setDrehbuchautor("kA");
            } else {
                neu.setDrehbuchautor(dbautor.toString());
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


            // Erscheinungsdatum-----------------------------------------------------------------------------------------------------------------
            if (targetUrl.getFirstByXPath(
                    "//div[@data-testid='title-details-section']//ul[1]//li[@data-testid='title-details-releasedate']//div[1]//ul[1]//li[1]//a[1]/text()") != null) {
                String erscheinungsdatum = targetUrl.getFirstByXPath(
                        "//div[@data-testid='title-details-section']//ul[1]//li[@data-testid='title-details-releasedate']//div[1]//ul[1]//li[1]//a[1]/text()").toString();

                int indexOfKlammer = erscheinungsdatum.indexOf("(");

                String erscheinungsdatum2 = erscheinungsdatum.substring(0, erscheinungsdatum.indexOf("(") - 1);
                DateFormat fmt = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                Date date2 = new Date();


                if (erscheinungsdatum2.indexOf(",") == -1) {
                    if (erscheinungsdatum2.length() > 4)
                        fmt = new SimpleDateFormat("MMMM yyyy");
                    else
                        fmt = new SimpleDateFormat("yyyy");
                }

                try {
                    date2 = fmt.parse(erscheinungsdatum2);
                } catch (ParseException e) {
                    System.out.println("Wrong date format");

                }

                neu.setErscheinungsdatum(format.format(date2));
            } else {
                neu.setErscheinungsdatum(format.format(new Date()));
            }

            // Filmname---------------------------------------------------------------------------------------------------------------------------
            if (imdb.getFirstByXPath("//div[@class='lister-list']//div[" + i + "]//div[3]/h3/a/text()") != null) {
                String filmname = imdb
                        .getFirstByXPath("//div[@class='lister-list']//div[" + i + "]//div[3]/h3/a/text()").toString();
                neu.setName(filmname);
            } else {
                neu.setName("kA");
            }

            // Kategorie--------------------------------------------------------------------------------------------------------------------------
            if (imdb.getFirstByXPath(
                    "//div[@class='lister-list']//div[" + i + "]//p[1]//span[@class='genre']/text()") != null) {
                String kategorie = imdb.getFirstByXPath(
                        "//div[@class='lister-list']//div[" + i + "]//p[1]//span[@class='genre']/text()").toString();
                neu.setKategorie(kategorie);
            } else {
                neu.setKategorie("kA");
            }

            // Regie------------------------------------------------------------------------------------------------------------------------------
            if (targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[1]//span/text()") != null) {
                if (targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[1]//span/text()").toString().equals("Director")) {
                    String regisseur = targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[1]//div[1]//ul[1]//li[1]//a/text()").toString();
                    neu.setRegisseur(regisseur);
                } else if (targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[1]//span/text()").toString().equals("Directors")) {
                    for (int x = 1; targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[1]//div[1]//ul[1]//li[" + x + "]//a/text()") != null; x++) {
                        if (targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[1]//div[1]//ul[1]//li[" + (x + 1) + "]//a/text()") != null) {
                            directors.append(targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[1]//div[1]//ul[1]//li[" + x + "]//a/text()").toString());
                            directors.append(", ");
                        } else {
                            directors.append(targetUrl.getFirstByXPath("//div[@data-testid='title-pc-wide-screen']//ul[1]//li[1]//div[1]//ul[1]//li[" + x + "]//a/text()").toString());
                        }
                    }
                    neu.setRegisseur(directors.toString());
                } else {
                    neu.setRegisseur("kA");
                }
            } else {
                neu.setRegisseur("kA");
            }

            // Filmlänge--------------------------------------------------------------------------------------------------------------------------
            if (imdb.getFirstByXPath(
                    "//div[@class='lister-list']//div[" + i + "]//p[1]//span[@class='runtime']/text()") != null) {
                String filmlaenge = imdb.getFirstByXPath(
                        "//div[@class='lister-list']//div[" + i + "]//p[1]//span[@class='runtime']/text()").toString();

                neu.setFilmlaenge(filmlaenge);
            } else {
                neu.setFilmlaenge("0 min");
            }

            // Besatzung--------------------------------------------------------------------------------------------------------------------------
            if (imdb.getFirstByXPath(
                    "//div[@class='lister-list']//div[" + i + "]//div[3]//p[3]//a[" + 2 + "]") != null) {
                for (int j = 2; imdb.getFirstByXPath(
                        "//div[@class='lister-list']//div[" + i + "]//div[3]//p[3]//a[" + j + "]") != null; j++) {
                    cast.append(imdb
                            .getFirstByXPath(
                                    "//div[@class='lister-list']//div[" + i + "]//div[3]//p[3]//a[" + j + "]/text()")
                            .toString());
                    if (imdb.getFirstByXPath(
                            "//div[@class='lister-list']//div[" + i + "]//div[3]//p[3]//a[" + (j + 1) + "]") != null) {
                        cast.append(", ");
                    }

                }
                neu.setCast(cast.toString());
            } else {
                neu.setCast("kA");
            }

            DB.insertFilm(neu.getName(), neu.getKategorie(), neu.getErscheinungsdatum(), neu.getRegisseur(), neu.getDrehbuchautor(), neu.getCast(), neu.getFilmlaenge(), "./.banner/platzhalter.png");

            DataWriter.write(
                    new Timestamp(date.getTime()) + "\n" +
                            "Collecting data from: " + url + "\n" +
                            "Name: " + neu.getName() + "\n" +
                            "Director: " + neu.getRegisseur() + "\n" +
                            "Screenwriter: " + neu.getDrehbuchautor() + "\n" +
                            "Length: " + neu.getFilmlaenge() + "\n" +
                            "Releasedate: " + neu.getErscheinungsdatum() + "\n" +
                            "Cast: " + neu.getCast() + "\n" +
                            "Tags: " + neu.getKategorie() + "\n\n"
            );


        }
        DataWriter.close();
        client.close();
    }

}
