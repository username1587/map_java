
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import socialnetwork.Main;
import socialnetwork.service.UserService;

public class MainFX extends Application {

    private Scene authentificationScene;
    private Scene userPageScene;

    public void functieLuataCuCopyPaste(Stage stage) {
        stage.show();
        stage.setTitle("titlu");
        stage.setHeight(600);
        stage.setWidth(900);


        GridPane gridPane = createTable(stage);

        authentificationScene = new Scene(gridPane);
        stage.setScene(authentificationScene);

        BorderPane borderPane=new BorderPane();
        userPageScene =new Scene(borderPane);
        ListView<String> listView=new ListView<>(FXCollections.observableArrayList());
        listView.getItems().addAll("ana","george","camionie");
//        borderPane.getChildren().add(listView);

//        ComboBox<String> comboBox=new ComboBox<>();
//        ArrayList<String> allItems = new ArrayList<>(Arrays.asList("abc", "abd", "ade", "banana", "iuon"));
//        comboBox.getItems().setAll(allItems);
        TextField textField=new TextField();
        textField.setPromptText("adauga un prieten...");
        Button buttonAddFriend=new Button("adauga prieten");
        Button buttonDelFriend=new Button("sterge prieten");
//        borderPane.getChildren().addAll(comboBox,listView);
        borderPane.setLeft(listView);
//        borderPane.setRight(comboBox);
        VBox vBoxRightGridPane=new VBox();
        ListView<String> listViewUsers=new ListView<>(/*Arrays.asList("unu","doi","trei")*/);
        listViewUsers.getItems().addAll("unu","doi","trei");
        listViewUsers.setMaxHeight(100);
        vBoxRightGridPane.getChildren().addAll(textField,listViewUsers);
        borderPane.setRight(vBoxRightGridPane);
        borderPane.setPadding(new Insets(40,40,40,40));

        VBox vBoxUserPageCenter=new VBox();
        vBoxUserPageCenter.setSpacing(20f);
        vBoxUserPageCenter.setAlignment(Pos.CENTER);
        vBoxUserPageCenter.getChildren().addAll(new Label("display error messages here"),buttonAddFriend,buttonDelFriend);

        borderPane.setCenter(vBoxUserPageCenter);

        //TODO se poate folosi tabPane pt taburi
    }

    /**
     *
     * @param gridPane
     * @param row
     * @param column
     * @return object in gridPane by row and column
     */
    private static Object getObjectInGrid(GridPane gridPane, int column, int row) {
        ObservableList<Node> allObj=gridPane.getChildren();
        for (Node node: allObj)
            if (GridPane.getColumnIndex(node).equals(column) && GridPane.getRowIndex(node).equals(row))
                return node;
        throw new NullPointerException("gridPane[row][column] is null");
    }

    private GridPane createTable(Stage stage) {
        GridPane gridPane = new GridPane();
        gridPane.add(new Label("Username"), 0, 0);
        gridPane.add(new TextField(), 1, 0);
        gridPane.add(new Label("Password"), 0, 1);
        gridPane.add(new TextField(), 1, 1);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.add(new Button("Login"), 1, 2);
        gridPane.add(new Button("Register"), 1, 3);

        Button loginButton=(Button)getObjectInGrid(gridPane,1,2);
        loginButton.setMinSize(160,25);
        Button registerButton=(Button)getObjectInGrid(gridPane,1,3);
        registerButton.setMinSize(160,25);

        gridPane.setAlignment(Pos.CENTER);

        loginButton.setOnMouseClicked(e->{
            stage.setScene(userPageScene);
        });

        return gridPane;
    }

    @Override
    public void start(Stage stage) {
        UserService userService= Main.createService();

        functieLuataCuCopyPaste(stage);
    }

    public static void main(String[] args) {
        launch();
    }

}