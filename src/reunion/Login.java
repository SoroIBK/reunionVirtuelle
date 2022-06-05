package reunion;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

import static java.lang.System.*;
import static java.sql.Types.NULL;
import static reunion.connexion.*;
public class Login {

    private Stage stage;
    private Scene scene;


    // LOGIN
    @FXML
    public TextField login;
    @FXML
    public PasswordField pass;
    @FXML
    private Pane pane_login;
    @FXML

    // INSCRIPTION
    private Pane pane_inscription;
    @FXML
    private ImageView photo;
    @FXML
    private TextField nom_insc;
    @FXML
    private TextField prenom_insc;
    @FXML
    private TextField email_insc;
    @FXML
    private TextField pass_insc;
    @FXML
    private TextField conf_insc;
    public static String organiEmail;
    public static int idUser;
    public static String nomComplet = "";

    public void loginButon(ActionEvent actionEvent) throws IOException {

            String email = login.getText().toString();
            String pas = pass.getText().toString();

        try{
            Class.forName(drive);
            System.out.println("Chargement reussi");

            Connection cn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion reussi");
            Statement instruction = cn.createStatement();
            String req = "SELECT * FROM personne WHERE email= '"+email+"' AND password='"+pas+"'";
            ResultSet resultSet = instruction.executeQuery(req);
            if(resultSet.next()){
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("gestion.fxml")));
                stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setTitle("Gestion");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
                organiEmail = email;
                idUser = resultSet.getInt("id");
                 nomComplet = resultSet.getString("nom")+" "+resultSet.getString("prenom");
            }
            else{
                out.println("Non");
            }
        }catch(ClassNotFoundException e){
            out.println("Chargement impossible !");
        }
        catch(SQLException e){
            out.println(e);
        }

    }

    public void p_inscription(ActionEvent actionEvent) {
        pane_login.setVisible(false);
        pane_inscription.setVisible(true);
    }

    public void se_connecter(ActionEvent event ) {
        pane_login.setVisible(true);
        pane_inscription.setVisible(false);
    }

    public void choisir_photo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(//
                new FileChooser.ExtensionFilter("Images files","*.png"),
                new FileChooser.ExtensionFilter("Images files","*.jpeg"),
                new FileChooser.ExtensionFilter("Images files","*.jpg")
        );
        File phot = fileChooser.showSaveDialog(stage);
        String m = phot.getPath();
        Image img = new Image(m);
        photo.setImage(img);
    }

    public void inscription(ActionEvent event) {
        String nom = nom_insc.getText().toString();
        String prenom = prenom_insc.getText().toString();
        String email = email_insc.getText().toString();
        String pass = pass_insc.getText().toString();
        String conf = conf_insc.getText().toString();
        //if (pass==conf){
            try{
                Class.forName(drive);
                out.println("Chargement reussi");

                Connection cn = DriverManager.getConnection(url, user, password);
                System.out.println("Connexion reussi");

                Statement statement1 = cn.createStatement();
                ResultSet resultSet = statement1.executeQuery("SELECT * FROM personne WHERE email='"+email+"'");
                if (!resultSet.next()) {
                    String req = "INSERT INTO personne (id, nom, prenom, email, password, photo)" +
                            " VALUES('"+NULL+"','"+nom+"','"+prenom+"','"+email+"','"+pass+"','"+NULL+"')";
                    Statement statement = cn.createStatement();
                    statement.executeUpdate(req);
                }else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Vous êtes déjà inscrire !!!");
                    alert.setTitle("Date déjà passée");
                    alert.show();
                }
            }catch(ClassNotFoundException e){
                out.println("Chargement impossible !");
            }
            catch(SQLException e){
                out.println(e);
            }
    }
}
