package reunion;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.time.LocalDate;

import static java.sql.Types.NULL;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.layout.BorderStrokeStyle.SOLID;
import static javafx.scene.paint.Color.*;
import static reunion.Login.idUser;
import static reunion.Login.nomComplet;
import static reunion.connexion.*;



public class gestion implements Serializable{
    @FXML
    public Text labelDynamic;
    @FXML
    public Pane paneDynamic1, paneDynamic2, paneDetails, message;
    @FXML
    public VBox paneDynamic1_vbox, vBoxmessage, vBoxMains;
    @FXML
    public AnchorPane ancchorPaneMessage;
    @FXML
    public TextArea messageGetText;
    @FXML
    public TextField libelle, sujet, duree_prevue, ordre_jour, libelle1, sujet1, duree_prevue1, ordre_jour1;
    @FXML
    public DatePicker date_debut;
    @FXML
    public ComboBox animateur, tp_reunion, animateur1, tp_reunion1;
    @FXML
    public DatePicker date_debut1;
    private int id_reunion_modifier = -333;
    protected ObjectInputStream in;
    protected  ObjectOutputStream out;

    @FXML
    public Button modifierButton;
    public ScrollPane scrollpaneReunion;
    public Scene scene;
    public Parent root;
    public Stage stage;

    public void nv_reunionButtonClick(ActionEvent actionEvent) {
        labelDynamic.setText("NOUVELLE REUNION");
        paneDynamic1.setVisible(false);

        paneDynamic2.setVisible(true);
        paneDetails.setVisible(false);
        libelle.setText("");
        sujet.setText("");
        date_debut.setValue(null);
        duree_prevue.setText("");
        ordre_jour.setText("");
        animateur.getSelectionModel().clearSelection();
        tp_reunion.getSelectionModel().clearSelection();


        message.setVisible(false);
    }

    public void reunionsView(ActionEvent actionEvent) {
        paneDynamic1_vbox.getChildren().clear();
        load();
        labelDynamic.setText("ACCUEIL");
        paneDynamic1.setVisible(true);
        paneDynamic2.setVisible(false);
        paneDetails.setVisible(false);

        message.setVisible(false);
    }

    public void quitter(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void reunionClicked(int e) {
        try{
            Class.forName(drive);
            Connection cn = DriverManager.getConnection(url, user, password);
            String req = "SELECT * FROM reunions WHERE id_reunion='"+e+"'";
            Statement statement = cn.createStatement();
            ResultSet rs = statement.executeQuery(req);
            while (rs.next()){
                if (idUser == rs.getInt("animateur")) {
                    labelDynamic.setText("NOUVELLE REUNION");
                    paneDynamic1.setVisible(false);
                    paneDynamic2.setVisible(false);
                    paneDetails.setVisible(false);
                    message.setVisible(true);

                    vBoxmessage.setSpacing(10);//vBoxmessage.setPadding(new Insets(10));
                    //vBoxmessage.setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));
                    vBoxmessage.setAlignment(Pos.TOP_LEFT);

                    try{
                        Socket socket = new Socket("localhost", 125);

                        new Thread(()->{
                            while(true){
                                try{
                                    out = new ObjectOutputStream(socket.getOutputStream());
                                    in = new ObjectInputStream(socket.getInputStream());
                                    System.out.println("Client a cree les flux");
                                    Object o = in.readObject();
                                    Text t = (Text) o;

                                    String reponse = t.getText().toString();
                                    int sp = reponse.indexOf("♂");

                                    String nom = "";
                                    String messa = "";
                                    for (int i = 0; i < sp; i++) {
                                        nom = nom + reponse.charAt(i);
                                    }
                                    for (int i = sp + 1; i <= reponse.length() - 1; i++) {
                                        messa = messa + reponse.charAt(i);
                                    }
                                    Label n = new Label(nom);
                                    n.setFont(Font.font("Verdana", FontWeight.BOLD,13.0));
                                    Text mess = new Text(messa);
                                    mess.setFont(Font.font("Verdana", FontWeight.THIN,11.0));
                                    //listModel.add(reponse);
                                    VBox m = new VBox();
                                    m.setSpacing(1);
                                    m.setBorder(new Border(new BorderStroke(BLACK, SOLID, null, null)));
                                    //m.setStyle("-fx-border-radius: 20.0");
                                    //m.setStyle("-fx-background-radius: 20.0");
                                    //m.setFillWidth(true);
                                    m.setBackground(new Background(new BackgroundFill(web("#66cccc"),null, null)));
                                    m.getChildren().addAll(n,mess);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            String IP = socket.getRemoteSocketAddress().toString();
                                            System.out.println("Connexion du client Numero  IP = " +IP);
                                            vBoxmessage.getChildren().add(m);
                                        }
                                    });
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                } catch (ClassNotFoundException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }).start();
                    } catch (UnknownHostException exception) {
                        exception.printStackTrace();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    //ancchorPaneMessage.getChildren().addAll(vBoxConverse);
                    //vBoxmessage.getChildren().addAll(listView);
                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Vous n'êtes pas animateur de cette reunion !!!\nVous ne pouvez pas la démarer");
                    alert.setTitle("Date déjà passée");
                    alert.show();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

    }

    public void deconnection(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
    }

    public void creer_reunion(ActionEvent event) {

        String lib = libelle.getText();
        String suje = sujet.getText();
        String date = date_debut.getValue().toString();
        String duree = duree_prevue.getText();
        String ordre = ordre_jour.getText();
        String animateurValue = String.valueOf(animateur.getSelectionModel().getSelectedItem());
        String tp_reunionValue = String.valueOf(tp_reunion.getSelectionModel().getSelectedItem());
        int sp = animateurValue.indexOf(" ");
        String nom = "";
        String prenom = "";
        Date aujourdhui = Date.valueOf(LocalDate.now());
        for (int i = 0; i < sp; i++) {
            nom = nom + animateurValue.charAt(i);
        }
        for (int i = sp + 1; i <= animateurValue.length() - 1; i++) {
            prenom = prenom + animateurValue.charAt(i);
        }

        try{
            Class.forName(drive);
            System.out.println("Chargement reussi");
            Connection cn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion reussi");

            Statement statement1 = cn.createStatement();
            String req = "SELECT * FROM personne WHERE email='"+Login.organiEmail+"'";
            System.out.println(Login.organiEmail);
            ResultSet re = statement1.executeQuery(req);
            Statement statement = cn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM personne AS per, typereunions AS tp " +
                    "WHERE per.nom='"+nom+"' AND per.prenom='"+prenom+"' AND tp.type='"+tp_reunionValue+"'");
            while (rs.next()){
                while(re.next()){
                    String existe = "SELECT * FROM reunions WHERE date_debut='"+date+"' AND animateur='"+rs.getInt("id")+"'";
                    Statement stm = cn.createStatement();
                    ResultSet resultSet = stm.executeQuery(existe);
                    if(resultSet.next()){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("Cette reunion existe déjà !!!");
                        alert.setTitle("Reunion existante déjà");
                        alert.show();
                        break;
                    }
                    else {
                        Date date1 = Date.valueOf(date);
                        if (aujourdhui.before(date1)|| aujourdhui.equals(date1)) {
                            String req2 = "INSERT INTO reunions " +
                                    "(id_reunion, libelle_reunion, sujet, date_debut, duree_prevue, ordre_jour, animateur, type_reunion, organisateur) " +
                                    "VALUES ('"+NULL+"', '"+lib+"', '"+suje+"', '"+date+"', '"+duree+"', '"+ordre+"', '"+rs.getInt("id")+"', " +
                                    "'"+rs.getInt("id_type")+"', '"+re.getInt("id")+"');";
                            int rs2 = statement.executeUpdate(req2);
                            System.out.println(rs2);
                            switch (rs2) {
                                case 1: {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setContentText("Reunion " + lib + " creée avec succès !!!");
                                    alert.setTitle("Nouvelle reunion");
                                    alert.show();
                                    break;
                                }
                                default: {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setContentText("Une erreur s'est produite !!!");
                                    alert.setTitle("Nouvelle reunion");
                                    alert.show();
                                    break;
                                }
                            }
                        }else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("La date doit être supperieure ou égale à celle d'aujourd'hui !!!");
                            alert.setTitle("Date déjà passée");
                            alert.show();
                            break;
                        }
                    }
                }
            }

        }catch(ClassNotFoundException e){
            System.out.println("Chargement impossible !");
        }
        catch(SQLException e){
            System.out.println(e);
        }
    }

    public void animItems(MouseEvent mouseEvent) {
        animateur.getItems().clear();
        animateur1.getItems().clear();
        try{
            Class.forName(drive);
            Connection cn = DriverManager.getConnection(url, user, password);
            String req = "SELECT * FROM personne";
            Statement statement = cn.createStatement();
            ResultSet rs = statement.executeQuery(req);
            while (rs.next()){
                if (paneDetails.isVisible()){
                    animateur1.getItems().addAll(rs.getString("nom")+" "+rs.getString("prenom"));
                }
                else{
                    animateur.getItems().addAll(rs.getString("nom")+" "+rs.getString("prenom"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void typeItems(MouseEvent mouseEvent) {
        tp_reunion.getItems().clear();
        tp_reunion1.getItems().clear();
        try{
            Class.forName(drive);
            Connection cn = DriverManager.getConnection(url, user, password);
            String req2 = "SELECT * FROM typereunions";
            Statement statement = cn.createStatement();
            ResultSet rs2 = statement.executeQuery(req2);
            while (rs2.next()){
                if (paneDetails.isVisible()){
                    tp_reunion1.getItems().addAll(rs2.getString("type"));
                }
                else{
                    tp_reunion.getItems().addAll(rs2.getString("type"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        try{
            Class.forName(drive);
            Connection cn = DriverManager.getConnection(url, user, password);
            String req = "SELECT * FROM reunions ORDER BY id_reunion DESC";
            Statement statement = cn.createStatement();
            ResultSet rs = statement.executeQuery(req);
            
            while (rs.next()){
                HBox hBox = new HBox();
                hBox.setAlignment(CENTER_LEFT);
                hBox.setSpacing(15.0);
                hBox.setStyle("-fx-border-radius: 35.0");
                hBox.setStyle("-fx-background-radius: 35px");
                hBox.setStyle("-fx-background-color: #c9b5d4;");
                hBox.setPrefWidth(50.0);
                hBox.setPrefHeight(50.0);


                Circle circle = new Circle();
                circle.setFill(DODGERBLUE);
                circle.setRadius(30.0);
                circle.setStroke(BLACK);
                circle.setStrokeType(StrokeType.INSIDE);

                Label label = new Label();
                label.setText(rs.getString("libelle_reunion"));
                label.setUnderline(true);
                label.setFont(new Font( 17));
                label.setStyle("-fx-font-weight: bold;");

                Button button_details = new Button();
                button_details.setMnemonicParsing(false);
                int id  = rs.getInt("id_reunion");
                button_details.setOnAction(e -> detailsClicked(id));
                button_details.setText("Details de  la reunion");
                button_details.setId("btn");
                button_details.setStyle("-fx-font-size: 17px");
                button_details.setStyle("-fx-border-radius: 10.0");
                button_details.setStyle("-fx-background-radius: 10.0;");
                button_details.setStyle("-fx-cursor: hand;");
                button_details.setPadding(new Insets(5,5,5,5));

                Button button = new Button();
                button.setMnemonicParsing(false);
                button.setOnAction(e -> reunionClicked(id));
                button.setText("Ouvrir la reunion");
                button.setId("btn");
                button.setStyle("-fx-font-size: 17px");
                button.setStyle("-fx-border-radius: 10.0");
                button.setStyle("-fx-background-radius: 10.0;");
                button.setStyle("-fx-cursor: hand;");
                button.setPadding(new Insets(5,5,5,5));


                hBox.getChildren().addAll(circle, label, button_details, button);

                paneDynamic1_vbox.getChildren().addAll(hBox);
                paneDynamic1_vbox.setSpacing(15.0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void detailsClicked(int e) {
        id_reunion_modifier = e;
        try {
            Connection cn = DriverManager.getConnection(url, user, password);

            String req = "SELECT * FROM reunions AS reu, personne AS per, typereunions AS tpr WHERE id_reunion ='"+e+"' AND reu.animateur=per.id AND reu.type_reunion=tpr.id_type";
            Statement statement1 = cn.createStatement();
            ResultSet rs = statement1.executeQuery(req);

            String reqo = "SELECT * FROM personne WHERE email='"+Login.organiEmail+"'";
            Statement statement = cn.createStatement();
            ResultSet rso = statement.executeQuery(reqo);

            while (rs.next()){;
                while (rso.next()){
                    libelle1.setText(rs.getString("libelle_reunion"));
                    sujet1.setText(rs.getString("sujet"));
                    date_debut1.setValue(LocalDate.parse(rs.getString("date_debut")));
                    duree_prevue1.setText(rs.getString("duree_prevue"));
                    ordre_jour1.setText(rs.getString("ordre_jour"));
                    String animateu = rs.getString("nom")+" "+rs.getString("prenom");
                    String type_reunion = rs.getString("type");

                    animateur1.getSelectionModel().select(animateu);
                    tp_reunion1.getSelectionModel().select(type_reunion);
                    System.out.println(animateu+" "+type_reunion);

                    if(rs.getInt("organisateur") == rso.getInt("id")){
                        modifierButton.setVisible(true);
                        modifierButton.setVisible(true);
                        libelle1.setEditable(true);
                        sujet1.setEditable(true);
                        date_debut1.setDisable(false);
                        duree_prevue1.setEditable(true);
                        ordre_jour1.setEditable(true);
                        animateur1.setDisable(false);
                        tp_reunion1.setDisable(false);
                    }
                    else {
                        modifierButton.setVisible(false);
                        libelle1.setEditable(false);
                        sujet1.setEditable(false);
                        date_debut1.setDisable(true);
                        duree_prevue1.setEditable(false);
                        ordre_jour1.setEditable(false);
                        animateur1.setDisable(true);
                        tp_reunion1.setDisable(true);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        labelDynamic.setText("Details de la reunion");
        paneDynamic1.setVisible(false);
        paneDynamic2.setVisible(false);
        paneDetails.setVisible(true);

    }

    public void envoi(ActionEvent event) throws IOException {
        if (!messageGetText.getText().isEmpty()){
            try{
                Class.forName(drive);
                Connection cn = DriverManager.getConnection(url, user, password);
                String reqo = "SELECT * FROM personne WHERE email='"+Login.organiEmail+"'";
                Statement statement = cn.createStatement();
                ResultSet rso = statement.executeQuery(reqo);
                if(rso.next()){
                    Text ttt = new Text(rso.getString("prenom")+" "+rso.getString("nom")+"♂"+messageGetText.getText().toString());
                    if(out.equals(null)){
                        out.writeObject(ttt);
                        out.flush();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



    public void ModifierReunion(ActionEvent event) {
        try {
            Connection cn = DriverManager.getConnection(url, user, password);
            String reqo = "SELECT * FROM personne WHERE email='"+Login.organiEmail+"'";
            Statement statement = cn.createStatement();
            ResultSet rso = statement.executeQuery(reqo);
            while (rso.next()){
                String animateurValue = String.valueOf(animateur1.getSelectionModel().getSelectedItem());
                String tp_reunionValue = String.valueOf(tp_reunion1.getSelectionModel().getSelectedItem());
                int sp = animateurValue.indexOf(" ");
                String nom = "";
                String prenom = "";
                for (int i = 0; i < sp; i++) {
                    nom = nom + animateurValue.charAt(i);
                }
                for (int i = sp + 1; i <= animateurValue.length() - 1; i++) {
                    prenom = prenom + animateurValue.charAt(i);
                }
                Statement statement11 = cn.createStatement();
                ResultSet rs = statement11.executeQuery("SELECT * FROM personne AS per, typereunions AS tp " +
                        "WHERE per.nom='"+nom+"' AND per.prenom='"+prenom+"' AND tp.type='"+tp_reunionValue+"'");
                while (rs.next()){
                    String s = "UPDATE reunions SET libelle_reunion = '"+libelle1.getText()+"', sujet = '"+sujet1.getText()+"', " +
                            "date_debut = '"+date_debut1.getValue().toString()+"', duree_prevue = '"+duree_prevue1.getText()+"', " +
                            "ordre_jour = '"+ordre_jour1.getText()+"', animateur = '"+rs.getInt("id")+"'," +
                            " type_reunion = '"+rs.getInt("id_type")+"' WHERE id_reunion = '"+id_reunion_modifier+"';";

                    Statement statement1 = cn.createStatement();
                    int resultSet = statement1.executeUpdate(s);
                    switch (resultSet){
                        case 1 :{
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Modifications effectuée avec sucès !!!");
                            alert.setTitle("Date déjà passée");
                            alert.show();
                            break;
                        }
                        default:{
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Une erreur s'est produite !!!");
                            alert.setTitle("Date déjà passée");
                            alert.show();
                            break;
                        }
                }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void demanderParole(ActionEvent event) {
        HBox main = new HBox();
        Label nomCp = new Label(nomComplet+" demande la parole !");
        Button autoriser = new Button("Accorder");
        main.getChildren().addAll(nomCp, autoriser);
        vBoxMains.getChildren().add(main);
    }
}
