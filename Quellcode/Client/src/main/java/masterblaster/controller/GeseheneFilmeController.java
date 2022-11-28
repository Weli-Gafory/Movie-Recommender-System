package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import masterblaster.Main;
import masterblaster.models.Film;
import masterblaster.models.Nutzer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GeseheneFilmeController extends SceneController implements Initializable {
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
    private Button delete;

    @FXML
    private Button foreward;

    @FXML
    private Button back;
    private Integer counterLeft;
    private Integer counterRight;
    @FXML
    private TableColumn<Film, String> regisseur;

    ObservableList<Film> filme = FXCollections.observableArrayList();

    private Nutzer nutzer;

    @FXML
    private void deleteButtonClicked(){
        try {
            Main.getClient().deleteFromSeenList(nutzer, filmTableView.getSelectionModel().getSelectedItem());
            this.filme.remove(filmTableView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
        cast.setCellValueFactory(new PropertyValueFactory<>("Cast"));
        categorie.setCellValueFactory(new PropertyValueFactory<>("Kategorie"));
        drehBuchAutor.setCellValueFactory(new PropertyValueFactory<>("Drehbuchautor"));
        erscheinungsDatum.setCellValueFactory(new PropertyValueFactory<>("Erscheinungsdatum"));
        filmLaenge.setCellValueFactory(new PropertyValueFactory<>("Filmlaenge"));
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        regisseur.setCellValueFactory(new PropertyValueFactory<>("Regisseur"));

        try {
            ObservableList<Film> filme = getSeenListFilms();
            filmTableView.setItems(filme);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pageTitel.setText("Gesehene Filme von " + nutzer.getName() + " " + nutzer.getSurname());
    }

    private ObservableList<Film> getSeenListFilms() throws IOException, InterruptedException {
        JSONArray jsonArray = Main.getClient().getAllFilmsInSeenList(nutzer);

        for (int i = 0; i <jsonArray.length() ; i++) {
            JSONObject object = jsonArray.getJSONObject(i);

            // TODO: 17.05.2022 Filmbanner hinzufuegen
            Integer id = object.getInt("id");
            String name = object.getString("name");
            String kategorie = object.getString("kategorie");
            String erscheinungsdatum = object.getString("erscheinungsdatum");
            String regisseur = object.getString("regisseur");
            String drehbuchautor = object.getString("drehbuchautor");
            String cast = object.getString("cast");
            String filmlaenge = object.getString("filmlaenge");
            Film film = new Film(id, name, kategorie, erscheinungsdatum, regisseur, drehbuchautor, cast, filmlaenge, "");
            filme.add(film);
        }
        return filme;

    }

    private void showButtons (boolean x){
        delete.setVisible(x);
    }
}
