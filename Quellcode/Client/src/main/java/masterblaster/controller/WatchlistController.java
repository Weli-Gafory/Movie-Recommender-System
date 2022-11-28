package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import masterblaster.Main;
import masterblaster.models.Film;
import masterblaster.models.Nutzer;
import masterblaster.models.Obermensch;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class WatchlistController extends SceneController implements Initializable {

    @FXML
    private Label pageTitel;

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
    private Button Filter;

    @FXML
    private Button details;

    @FXML
    private Button delete;

    @FXML
    private Button filmAnsehen;

    @FXML
    private Button foreward;

    @FXML
    private TableColumn<Film, String> regisseur;
    ObservableList<Film> filme = FXCollections.observableArrayList();

    private Nutzer nutzer;

    @FXML
    private void detailsButtonClicked(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FilmAnsicht.fxml"));
        Parent root;
        root = fxmlLoader.load();
        FilmAnsichtController filmAnsichtController = fxmlLoader.getController();

        filmAnsichtController.passFilmData(getSelectedFilm(),"WatchlistController");

        Scene addScene = new Scene(root);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addScene);
        window.show();
    }
    private  Film getSelectedFilm() {
        return filmTableView.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void deleteButtonClicked(){
        System.out.println(getSelectedFilm().getName());
        System.out.println(getSelectedFilm().getId());
        try {
            Main.getClient().deleteFilmFromWatchlist(nutzer,getSelectedFilm());
            filme.remove(getSelectedFilm());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (Main.getClient().getFremdNutzer() != null){
            nutzer = Main.getClient().getFremdNutzer();
            Main.getClient().setFremdNutzer(null);
            showButtons(false);
        }
        else {
            nutzer = Main.getClient().getNutzer();
            showButtons(true);
        }
        setMenuSettings();
        System.out.println("watchlist");
        cast.setCellValueFactory(new PropertyValueFactory<>("Cast"));
        categorie.setCellValueFactory(new PropertyValueFactory<>("Kategorie"));
        drehBuchAutor.setCellValueFactory(new PropertyValueFactory<>("Drehbuchautor"));
        erscheinungsDatum.setCellValueFactory(new PropertyValueFactory<>("Erscheinungsdatum"));
        filmLaenge.setCellValueFactory(new PropertyValueFactory<>("Filmlaenge"));
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        regisseur.setCellValueFactory(new PropertyValueFactory<>("Regisseur"));


        try {
            ObservableList<Film> filme = getWatchListFilms();
            System.out.println("test coneect");
            filmTableView.setItems(filme);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pageTitel.setText("Watchlist von " + nutzer.getName() + " " + nutzer.getSurname());



    }
    private ObservableList<Film> getWatchListFilms() throws IOException, InterruptedException {
        JSONArray jsonArray = Main.getClient().getAllFilmsInWatchlist(this.nutzer);

        for (int i = 0; i <jsonArray.length() ; i++) {
            JSONObject object = jsonArray.getJSONObject(i);

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
        return filme;


    }
    @FXML
    public void filmAnSehen() throws Exception {
        Main.getClient().filmGesehen(nutzer, getSelectedFilm());
        Main.getClient().deleteFilmFromWatchlist(nutzer,getSelectedFilm());
        filme.remove(getSelectedFilm());
    }

    private void showButtons(boolean x){
        filmAnsehen.setVisible(x);
        delete.setVisible(x);
        details.setVisible(x);
    }
}
