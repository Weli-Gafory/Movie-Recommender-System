package masterblaster.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import masterblaster.Main;
import masterblaster.models.Film;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.ResourceBundle;

public class FilmAnsichtController extends SceneController implements Initializable {

    private Film film;
    @FXML
    private Text titleName;

    @FXML
    private Text name;

    @FXML
    private Text kategorie;

    @FXML
    private Text laenge;

    @FXML
    private Text datum;

    @FXML
    private Text drehbuch;

    @FXML
    private Text regisseur;

    @FXML
    private Label cast;

    @FXML
    private Button AlleBewertungen;

    @FXML
    private Button FehlerMelden;

    @FXML
    private Button Gesehen;

    @FXML
    private Button Zurueck;


    @FXML
    private ImageView filmBanner;

    @FXML
    private Text ratingMessage;

    @FXML
    private Slider ratingSlider;

    @FXML
    private TextField ratingTitle;

    @FXML
    private TextField ratingText;

    @FXML
    private Button saveRating;

    @FXML
    private Button discard;

    @FXML
    private Rectangle ratingRect;

    @FXML
    private Rectangle issueRect;

    @FXML
    private ImageView ikea;

    @FXML
    private Text errorMessage;


    @FXML
    private TextField errorText;

    @FXML
    private Button errorSender;

    @FXML
    private Button discardError;

    @FXML
    private SVGPath sternEins;

    @FXML
    private SVGPath sternZwei;

    @FXML
    private SVGPath sternDrei;

    @FXML
    private SVGPath sternVier;

    @FXML
    private SVGPath sternFuenf;

    private String fromWhere;


    public void passFilmData(Film film, String prev){
        this.film = film;
        titleName.setText(film.getName());
        name.setText(film.getName());
        kategorie.setText(film.getKategorie());
        laenge.setText(film.getFilmlaenge());
        datum.setText(film.getErscheinungsdatum());
        drehbuch.setText(film.getDrehbuchautor());
        regisseur.setText(film.getRegisseur());
        cast.setMaxWidth(550);
        cast.setWrapText(true);
        cast.setText(film.getCast());
        filmBanner.setImage(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(film.getFilmbanner()))));
        fromWhere = prev;
        try{
            setAvgBewertung(Main.getClient().getAverageBewertung(film.getId()));
        } catch(Exception ex){
            System.out.println("doof");
        }

    }

    @FXML
    private void alleBewertungenButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BewertungenUebersicht.fxml"));
        Parent root;
        root = loader.load();
        BewertungUebersichtController bewertungUebersichtController = loader.getController();

        bewertungUebersichtController.passFilm(this.film);

        Scene addScene = new Scene(root);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addScene);
        window.show();
    }

    @FXML
    private void fehlerMeldenButtonClicked(){
        //hier kommt das Pop-Up rein!
        filmAnsichtAnzeigen(false);

        fehlerReportAnzeigen(true);
    }

    @FXML
    private void errorSenderButtonClicked(){
        try {
            Main.getClient().newFilmReport(this.film, errorText.getText(), Main.getClient().getNutzer());

            fehlerReportAnzeigen(false);
            filmAnsichtAnzeigen(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void discardErrorButtonClicked(){
        filmAnsichtAnzeigen(true);
        fehlerReportAnzeigen(false);
    }


    @FXML
    private void gesehenButtonClicked(){
        filmAnsichtAnzeigen(false);

        bewertungAnzeigen(true);

        try {
            Main.getClient().filmGesehen(Main.getClient().getNutzer(), this.film);
            Main.getClient().deleteFilmFromWatchlist(Main.getClient().getNutzer(),this.film);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void saveRatingButtonClicked(){
        saveNewRating(ratingTitle.getText(), (int) ratingSlider.getValue(),ratingText.getText()); //hoffe doch sehr, dass der Cast hier funktioniert!
    }

    private void saveNewRating(String titel, Integer rating, String text){
        try {
            Main.getClient().addNewReview(Main.getClient().getNutzer(),film,titel,rating,text);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Main.getClient().getMensch();

        filmAnsichtAnzeigen(true);
        bewertungAnzeigen(false);

        //bei Erfolg sollten wir noch eine Erfolgsmeldung geben!
    }

    @FXML
    private void discardButtonClicked(){
        filmAnsichtAnzeigen(true);

        bewertungAnzeigen(false);
    }



    @FXML
    private void zurueckButtonClicked(){
        if (fromWhere == "FilmUebersichtController"){
            databaseViewMenuButtonClicked();
        }
        else {
            watchlistClick();
        }

    }

    private void filmAnsichtAnzeigen(boolean x){
        Gesehen.setVisible(x);
        filmBanner.setVisible(x);
        name.setVisible(x);
        kategorie.setVisible(x);
        cast.setVisible(x);
        datum.setVisible(x);
        regisseur.setVisible(x);
        drehbuch.setVisible(x);
        laenge.setVisible(x);
        Zurueck.setVisible(x);
        AlleBewertungen.setVisible(x);
        FehlerMelden.setVisible(x);
    }

    private void bewertungAnzeigen(boolean x){
        ratingMessage.setVisible(x);
        ratingSlider.setVisible(x);
        ratingTitle.setVisible(x);
        ratingText.setVisible(x);
        saveRating.setVisible(x);
        discard.setVisible(x);
        ratingRect.setVisible(x);
    }

    private void fehlerReportAnzeigen(boolean x){
        issueRect.setVisible(x);
        ikea.setVisible(x);
        errorMessage.setVisible(x);
        errorText.setVisible(x);
        errorSender.setVisible(x);
        discardError.setVisible(x);
        System.out.println(film.getId());
        System.out.println(errorText.getText());

    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuSettings();
        showAllStars(true);
    }

    private void einSternBewertung(){
        showAllStars(true);
        Color gold = Color.rgb(255, 240, 87);
        sternEins.setFill(gold);
    }

    private void zweiSternBewertung(){
        showAllStars(true);
        Color gold = Color.rgb(255, 240, 87);
        sternEins.setFill(gold);
        sternZwei.setFill(gold);
    }

    private void dreiSternBewertung(){
        showAllStars(true);
        Color gold = Color.rgb(255, 240, 87);
        sternEins.setFill(gold);
        sternZwei.setFill(gold);
        sternDrei.setFill(gold);
    }

    private void vierSternBewertung(){
        showAllStars(true);
        Color gold = Color.rgb(255, 240, 87);
        sternEins.setFill(gold);
        sternZwei.setFill(gold);
        sternDrei.setFill(gold);
        sternVier.setFill(gold);
    }

    private void fuenfSternBewertung(){
        showAllStars(true);
        Color gold = Color.rgb(255, 240, 87);
        sternEins.setFill(gold);
        sternZwei.setFill(gold);
        sternDrei.setFill(gold);
        sternVier.setFill(gold);
        sternFuenf.setFill(gold);

    }

    private void setAvgBewertung(int avg){

        if (avg == 0){
            showAllStars(true);
        } else {
            if (avg == 1){
                einSternBewertung();
            } else if (avg == 2) {
                zweiSternBewertung();
            } else if (avg == 3) {
                dreiSternBewertung();
            } else if (avg == 4) {
                vierSternBewertung();
            } else if (avg == 5) {
                fuenfSternBewertung();
            }
        }



    }

    private void showAllStars(boolean x){
        sternEins.setVisible(x);
        sternZwei.setVisible(x);
        sternDrei.setVisible(x);
        sternVier.setVisible(x);
        sternFuenf.setVisible(x);

        sternEins.setFill(Color.BLACK);
        sternZwei.setFill(Color.BLACK);
        sternDrei.setFill(Color.BLACK);
        sternVier.setFill(Color.BLACK);
        sternFuenf.setFill(Color.BLACK);



    }


}
