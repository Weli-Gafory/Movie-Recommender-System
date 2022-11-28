package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import masterblaster.Main;
import masterblaster.models.Film;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;


public class FilmueberSichtController extends SceneController implements Initializable {

    @FXML
    private TableView<Film> filmTableView;

    @FXML
    private TableColumn<Film, String> cast;

    @FXML
    private TableColumn<Film, String> categorie;

    @FXML
    private TableColumn<Film, String> drehBuchAutor;

    @FXML
    private TableColumn<Film, String> erscheinungsDatum;

    @FXML
    private TableColumn<Film, String> filmLaenge;

    @FXML
    private TableColumn<Film, String> name;

    @FXML
    private TableColumn<Film, String> bewertung;

    @FXML
    private Button back;

    @FXML
    private Button foreward;

    @FXML
    private Button Details;

    @FXML
    private Button Filter;
    @FXML
    private TextField keyword;

    @FXML
    private Button addToWatchlist;

    @FXML
    private Button EditButton;

    @FXML
    private Button statistik;

    @FXML
    private TableColumn<Film, String> regisseur;
    static String url = "http://localhost:1999";

    private Integer counterLeft;
    private Integer counterRight;
    private JSONObject filter = new JSONObject().put("Titel","").put("kategorie","").put("erscheinungsdatum","").put("cast","");

    @FXML
    private void EditEvent() {
        System.out.println("EditEvent gestartet");
    }

    @FXML
    private void CommitEvent() {
        System.out.println("CommitEvent gestartet");
    }

    @FXML
    private void EditCancel() {
        System.out.println("EditCancel gestartet");
    }

    @FXML
    private void EditButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditFilmVolker.fxml"));
        Parent root;
        root = loader.load();
        EditFilmController editFilmController = loader.getController();

        editFilmController.passFilmData(getSelectedFilm());

        Scene addScene = new Scene(root);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addScene);
        window.show();
    }

    @FXML
    private void BackButtonClicked() {
        counterLeft -= 100;
        counterRight -= 100;
        ForewardButtonClicked();
    }

    @FXML
    private void ForewardButtonClicked() {
        try {
            ObservableList<Film> filme = getFilms();
            filmTableView.setItems(filme);
            FilteredList<Film> filtered =  new FilteredList<>(filme, movie-> {
                if (
                        movie.getName().toLowerCase().contains(this.filter.getString("Titel").toLowerCase()) &&
                        movie.getKategorie().toLowerCase().contains(this.filter.getString("kategorie").toLowerCase()) &&
                        movie.getErscheinungsdatum().toLowerCase().contains(this.filter.getString("erscheinungsdatum")) &&
                        movie.getCast().toLowerCase().contains(this.filter.getString("cast").toLowerCase())
                )
                    return true;
                else
                    return false;
            });
            keyword.textProperty().addListener((observable ,oldvalue ,newValue)->{
                filtered.setPredicate(filmsearch ->{
                    if(newValue ==null){
                        return  true;
                    }
                    String searchword =newValue.toLowerCase();
                    if(
                            filmsearch.getName().toLowerCase().indexOf(searchword)>-1
                    ){
                        return  true;

                    }
                    if(
                            filmsearch.getKategorie().toLowerCase().indexOf(searchword)>-1
                    ){
                        return  true;

                    }
                    if(
                            filmsearch.getErscheinungsdatum().indexOf(searchword)>-1
                    ){
                        return  true;

                    }
                    if(
                            filmsearch.getCast().toLowerCase().indexOf(searchword)>-1
                    ){
                        return  true;

                    }
                    else{
                        return false;

                    }
                });

            });
            SortedList<Film> sortedList = new SortedList<>(filtered);
            sortedList.comparatorProperty().bind(filmTableView.comparatorProperty());
            filmTableView.setItems(sortedList);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private ObservableList<Film> getFilms() throws IOException, InterruptedException {
        JSONArray jsonArray = Main.getClient().getAllFilms();

        ObservableList<Film> filme = FXCollections.observableArrayList();

        this.checkCounterButtons(jsonArray.length());

        for (; counterLeft < counterRight; counterLeft++) {
            JSONObject object = jsonArray.getJSONObject(counterLeft);

            Integer id = object.getInt("id");
            String name = object.getString("name");
            String kategorie = object.getString("kategorie");
            String erscheinungsdatum = object.getString("erscheinungsdatum");
            String regisseur = object.getString("regisseur");
            String drehbuchautor = object.getString("drehbuchautor");
            String cast = object.getString("cast");
            String filmlaenge = object.getString("filmlaenge");
            String filmbanner = object.getString("filmbanner");
            Film film = new Film(id, name, kategorie, erscheinungsdatum, regisseur, drehbuchautor, cast, filmlaenge, filmbanner);
            filme.add(film);
        }
        counterRight = counterRight + 50;
        return filme;
    }

    private Film getSelectedFilm() {
        return filmTableView.getSelectionModel().getSelectedItem();
    }

    private void checkCounterButtons(int countedMovies) {
        if (counterLeft < 0)
            counterLeft = 0;
        if (counterRight <= 0)
            counterRight = 50;
        if (counterLeft == 0 || counterRight < counterLeft)
            counterRight = counterLeft + 50;
        if (counterRight >= countedMovies)
            counterRight = countedMovies;

        if (counterLeft == 0 || countedMovies == 0)
            back.setDisable(true);
        else
            back.setDisable(false);
        if (counterRight == 0 || countedMovies == 0 || counterRight == countedMovies)
            foreward.setDisable(true);
        else
            foreward.setDisable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cast.setCellValueFactory(new PropertyValueFactory<>("Cast"));
        categorie.setCellValueFactory(new PropertyValueFactory<>("Kategorie"));
        drehBuchAutor.setCellValueFactory(new PropertyValueFactory<>("Drehbuchautor"));
        erscheinungsDatum.setCellValueFactory(new PropertyValueFactory<>("Erscheinungsdatum"));
        filmLaenge.setCellValueFactory(new PropertyValueFactory<>("Filmlaenge"));
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        regisseur.setCellValueFactory(new PropertyValueFactory<>("Regisseur"));

        // TODO: 26.05.22 hier muss auch noch die Bewertung hin

        setMenuSettings();

        back.setDisable(true);

        counterLeft = 0;
        counterRight = 50;
        ForewardButtonClicked();
        Details.setVisible(true);
        if (Main.isAdmin){
            Details.setVisible(false);
            statistik.setVisible(true);
            System.out.println("Admin");
            addToWatchlist.setVisible(false);
            EditButton.setVisible(true);
        }
        else {
            EditButton.setVisible(false);
            statistik.setVisible(false);
        }
    }
    @FXML
    public void addToWatchlistButtonClicked() {
        try {
            Main.getClient().addFilmToWatchlist(Main.getClient().getNutzer(),getSelectedFilm());
            JOptionPane.showMessageDialog(null, "Film wurde zur Wishlist hinzugefügt!", "InfoBox: " + "Erfolgreich", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(getSelectedFilm().toString());

    }

    @FXML
    private void detailsButtonClicked(ActionEvent event) throws  IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FilmAnsicht.fxml"));
        Parent root;
        root = fxmlLoader.load();
        FilmAnsichtController filmAnsichtController = fxmlLoader.getController();

        Film temp = getSelectedFilm();
        filmAnsichtController.passFilmData(getSelectedFilm(),"FilmUebersichtController");

        Scene addScene = new Scene(root);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addScene);
        window.show();


    }

    @FXML
    private void filterButtonClicked() throws IOException {
        final Stage dialog = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Filter.fxml"));
        Parent root;
        root = fxmlLoader.load();
        FilterController filterController = fxmlLoader.getController();
        filterController.passData(this, this.filter);
        Scene addScene = new Scene(root);
        dialog.setScene(addScene);
        dialog.show();
    }

    public void filter(JSONObject filter){
        this.filter = filter;
        counterLeft = 0;
        counterRight = 50;
        ForewardButtonClicked();
        System.out.println("IM FILTERING HERE SOMETHING");


    }

    @FXML
    private void statistikButtonClicked(ActionEvent event) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FilmStatistikAnsicht.fxml"));
        Parent root;
        root = fxmlLoader.load();
        FilmStatistikAnsichtController filmStatistikAnsichtController = fxmlLoader.getController();

        Film temp = getSelectedFilm();
        filmStatistikAnsichtController.passFilmData(getSelectedFilm(),"FilmUebersichtController");

        Scene addScene = new Scene(root);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addScene);
        window.show();
    }
}

