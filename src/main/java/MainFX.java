import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import socialnetwork.Main;
import socialnetwork.domain.FriendshipDTO;
import socialnetwork.domain.User;
import socialnetwork.helpers.FriendshipRequestStatus;
import socialnetwork.helpers.Message;
import socialnetwork.helpers.UserToString;
import socialnetwork.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MainFX extends Application {
    private Scene authentificationScene;
    private Scene userPageScene;
    private TabPane tabPane;
    private VBox friendRequestPanel;

    TableView<FriendshipDTO> tableView;
    ObservableList<UserToString> modelFriends = FXCollections.observableArrayList();
    ObservableList<UserToString> modelUsers = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> friendshipsModel /*= FXCollections.observableArrayList()*/;
    List<FriendshipDTO> friendshipRequestsList;
    private TextField textFieldPrefixAddFriend;

    private void loadFriendshipRequestsList(UserService userService) {
        // TODO
        friendshipRequestsList = userService.getFriendshipsOfUser(userService.authentificatedUserId)
                .stream()
                .map(friendship -> {
                    Long friendId;
                    if (friendship.getId().getLeft().equals(userService.authentificatedUserId))
                        friendId = friendship.getId().getRight();
                    else
                        friendId = friendship.getId().getLeft();
                    String firstName = ((User) (userService.getRepo().findOne(friendId))).getFirstName();
                    String lastName = ((User) (userService.getRepo().findOne(friendId))).getLastName();

                    FriendshipDTO friendshipDTO = new FriendshipDTO(firstName, lastName, friendship.getStatus(), friendship.getDate(), friendship.getId());
                    return friendshipDTO;
                })
                .collect(Collectors.toList());

        friendshipsModel = FXCollections.observableArrayList(friendshipRequestsList);
//        friendshipsModel.addListener((InvalidationListener) x -> {
//            tableView = getFriendshipDTOTableView(userService);
//        });
    }

    private void functieLuataCuCopyPaste(Stage stage, UserService userService) {
        stage.show();
        stage.setTitle("titlu");
        stage.setHeight(600);
        stage.setWidth(900);

        GridPane authentificationSceneRootPanel = createTable(stage, userService);
        authentificationScene = new Scene(authentificationSceneRootPanel);

        stage.setScene(authentificationScene);

    }

    private BorderPane createUserPagePanel(UserService userService) {
        BorderPane userPageSceneRootPanel = new BorderPane();
        ListView<UserToString> friendsListView = new ListView<>(modelFriends);

        BorderPane userPageSceneMyPagePanel = new BorderPane();

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab tab = new Tab();
        tab.setText("pagina mea");
        tab.setContent(userPageSceneMyPagePanel);
        Tab tab2 = new Tab();
        tab2.setText("cererile de prietenie");
        loadFriendRequestsPanel(userService);
        tab2.setContent(friendRequestPanel);
        Tab tab3 = new Tab();
        tab3.setText("mesaje");
        populateMessageTab(tab3, userService);
        tabPane.getTabs().addAll(tab, tab2, tab3); // TODO: sa pun tabpane u asta undeva

        userPageSceneRootPanel.setCenter(tabPane);

        Object userBeforeCheck = userService.getRepo().findOne(userService.authentificatedUserId);
        if (userBeforeCheck == null)
            throw new RuntimeException("user is null");
        if (!(userBeforeCheck instanceof User))
            throw new RuntimeException("user not found");

        User currentUser = (User) userBeforeCheck;

        updateModelFriends(userService);
        textFieldPrefixAddFriend = new TextField();
        textFieldPrefixAddFriend.setPromptText("adauga un prieten...");
        Button buttonAddFriend = new Button("adauga prieten");
        Button buttonDelFriend = new Button("sterge prieten");
//        borderPane.getChildren().addAll(comboBox,listView);
        userPageSceneMyPagePanel.setLeft(friendsListView);
//        borderPane.setRight(comboBox);
        VBox vBoxRightGridPane = new VBox();
        ListView<UserToString> listViewUsers = new ListView<>(modelUsers);

        updateModelUsers(userService);
        listViewUsers.setMaxHeight(100);
        vBoxRightGridPane.getChildren().addAll(textFieldPrefixAddFriend, listViewUsers);
        userPageSceneMyPagePanel.setRight(vBoxRightGridPane);
        userPageSceneRootPanel.setPadding(new Insets(10, 40, 40, 40));

        VBox vBoxUserPageCenter = new VBox();
        vBoxUserPageCenter.setSpacing(20f);
        vBoxUserPageCenter.setAlignment(Pos.CENTER);
        Label errorDisplayer = new Label();
        vBoxUserPageCenter.getChildren().addAll(errorDisplayer, buttonAddFriend, buttonDelFriend);

        userPageSceneMyPagePanel.setCenter(vBoxUserPageCenter);

        VBox topPanelInUserPage = new VBox();
        Label numePagina = new Label(currentUser.getLastName() + " " + currentUser.getFirstName());
        numePagina.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
        topPanelInUserPage.getChildren().add(numePagina);
        topPanelInUserPage.setAlignment(Pos.CENTER);
        userPageSceneRootPanel.setTop(topPanelInUserPage);

        buttonDelFriend.setOnMouseClicked(event -> {
            User friendToDelete = friendsListView.getSelectionModel().getSelectedItem();
            if (friendToDelete == null) {
                errorDisplayer.setTextFill(Color.web("#FF0000"));
                errorDisplayer.setText("selecteaza un prieten pt stergere");
                return;
            }
            User userFrom = (User) (userService.getRepo().findOne(userService.authentificatedUserId));

            if (userService.deleteFriend(userFrom, friendToDelete) == null) {
                errorDisplayer.setTextFill(Color.web("#FF0000"));
                errorDisplayer.setText("prietenul nu a putut fi sters");
            } else {
                updateModelFriends(userService);
                updateModelUsers(userService);
                errorDisplayer.setTextFill(Color.web("#FFFFFF"));
                errorDisplayer.setText("prietenul a fost sters");
            }
        });

        buttonAddFriend.setOnMouseClicked(event -> {
            User user1 = (User) (userService.getRepo().findOne(userService.authentificatedUserId));
            User user2 = listViewUsers.getSelectionModel().getSelectedItem();
            if (user2 == null) {
                errorDisplayer.setTextFill(Color.web("#FF0000"));
                errorDisplayer.setText("selecteaza un user pt a-l adauga la prieteni");
                return;
            }
            userService.addFriend(user1, user2);
            updateModelFriends(userService);
            updateModelUsers(userService);

            errorDisplayer.setTextFill(Color.web("#000000"));
            errorDisplayer.setText("prieten adaugat");
        });

        textFieldPrefixAddFriend.textProperty().addListener(x -> handleFilter(textFieldPrefixAddFriend, userService));

        return userPageSceneRootPanel;
    }

    private void populateMessageTab(Tab tab3, UserService userService) {
//        HBox mainPanel = new HBox();
//        tab3.setContent(mainPanel);
//        ListView<UserToString> listViewFriends = new ListView<>(modelFriends);
//        Long selectedUserId=listViewFriends.getSelectionModel().getSelectedItem().getId();
//        List<Message> userServiceMessagesList=userService.getMessagesBetween2UsersInList(0L,1L);
//        ObservableList<Message> messageList=FXCollections.observableArrayList(userServiceMessagesList);
//        ListView<Message> listViewMessages=new ListView<>(messageList);
//        listViewMessages.setPrefWidth(600);
//        mainPanel.getChildren().addAll(listViewFriends,listViewMessages);

    }

    private void handleFilter(TextField textField, UserService userService) {
        Predicate<UserToString> firstNamePredicate = x -> x.getFirstName().startsWith(textField.getText());
        Predicate<UserToString> lastNamePredicate = x -> x.getLastName().startsWith(textField.getText());

        List<UserToString> listUsers = new ArrayList<>();
        userService.getAll().forEach(el -> listUsers.add(new UserToString(el)));

        modelUsers.setAll(listUsers
                .stream()
                .filter(firstNamePredicate.or(lastNamePredicate))
                .collect(Collectors.toList()));
    }

    private void loadFriendRequestsPanel(UserService userService) {
        friendRequestPanel = new VBox();
        friendRequestPanel.setSpacing(10f);
        tableView = getFriendshipDTOTableView(userService);

//        new TextField().textProperty().addListener(x->handleFilter());
//        tableColumnTema.textProperty().addListener(x->handleFilter());
//        tableColumnNota.textProperty().addListener(x->handleFilter());

        VBox vBox = new VBox();
        Button acceptFriendReq = new Button("accepta prietenie");
        vBox.getChildren().addAll(acceptFriendReq);
        vBox.setAlignment(Pos.BOTTOM_RIGHT);
        friendRequestPanel.getChildren().addAll(tableView, vBox);


        friendshipsModel.addListener((InvalidationListener) x -> loadFriendRequestsPanel(userService));
//        acceptFriendReq.setDisable(true);

//        ObservableList<Person> selectedItems = selectionModel.getSelectedItems();
//
//        selectedItems.addListener(new ListChangeListener<Person>() {
//            @Override
//            public void onChanged(Change<? extends Person> change) {
//                System.out.println("Selection changed: " + change.getList());
//            }
//        })

        //TODO: daca nu ma ajuta profu sa fac un listener pt tableView care sa dezactiveze butonu de add cand friend request ii accepted sau rejected
//        tableView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<FriendshipDTO>() {
//            @Override
//            public void onChanged(Change<? extends FriendshipDTO> change) {
//                System.out.println("Selection changed: " + change.getList());
//                acceptFriendReq.setDisable(!acceptFriendReq.isDisable());
//                System.out.println((List<FriendshipDTO>)change);
//            }
//        });
//
//        tableView.selectionModelProperty().addListener((observable, oldSelection, newSelection) -> {
//            acceptFriendReq.setDisable(false);
//        });

        acceptFriendReq.setOnMouseClicked(event -> {
            FriendshipDTO friendshipDTO = tableView.getSelectionModel().getSelectedItem();
            userService.acceptOrRejectFriendReq(friendshipDTO.getId().getLeft(), friendshipDTO.getId().getRight(), FriendshipRequestStatus.APPROVED);
        });
    }

    private TableView<FriendshipDTO> getFriendshipDTOTableView(UserService userService) {
        TableView<FriendshipDTO> tableView = new TableView<>();
        TableColumn<FriendshipDTO, String> firstNameColumn = new TableColumn<>("First Name");
        TableColumn<FriendshipDTO, String> lastNameColumn = new TableColumn<>("Last Name");
        TableColumn<FriendshipDTO, FriendshipRequestStatus> statusColumn = new TableColumn<>("status");
        TableColumn<FriendshipDTO, LocalDateTime> dateColumn = new TableColumn<>("date");

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(firstNameColumn, lastNameColumn, statusColumn, dateColumn);

        loadFriendshipRequestsList(userService);
        friendshipsModel.setAll(friendshipRequestsList);

//        friendshipsModel.addAll(userService.getFriendsOfUser(userService.authentificatedUserId));
        tableView.setItems(friendshipsModel);

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("firstName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, FriendshipRequestStatus>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, LocalDateTime>("date"));
        return tableView;
    }

    private void updateModelFriends(UserService userService) {
        modelFriends.setAll(userService.getFriendshipsOfUser(userService.authentificatedUserId)
                        .stream()
//                        .filter(friendship -> friendship.getStatus().equals(FriendshipRequestStatus.APPROVED))
                        .map(friendship -> {
                            UserToString user;
                            if (friendship.getId().getLeft().equals(userService.authentificatedUserId)) {
                                User userAux = (User) (userService.getRepo().findOne(friendship.getId().getRight()));
                                user = new UserToString(userAux);
                            } else {
                                User userAux = (User) (userService.getRepo().findOne(friendship.getId().getLeft()));
                                user = new UserToString(userAux);
                            }
                            return user;
                        })
                        .collect(Collectors.toList())
        );
    }

    private void updateModelUsers(UserService userService) {
        List<UserToString> arrayOfUsers = new ArrayList<>();
        userService.getAll().forEach(user -> arrayOfUsers.add(new UserToString(user)));
        modelUsers.setAll(arrayOfUsers
                .stream()
                .filter(userToString -> !userService.getFriendsOfUser(userService.authentificatedUserId).contains(userToString))
                .collect(Collectors.toList())
        );
    }

    /**
     * @param gridPane
     * @param row
     * @param column
     * @return object in gridPane by row and column
     */
    private static Object getObjectInGrid(GridPane gridPane, int column, int row) {
        ObservableList<Node> allObj = gridPane.getChildren();
        for (Node node : allObj)
            if (GridPane.getColumnIndex(node).equals(column) && GridPane.getRowIndex(node).equals(row))
                return node;
        throw new NullPointerException("gridPane[row][column] is null");
    }

    private GridPane createTable(Stage stage, UserService userService) {
        GridPane gridPane = new GridPane();
        gridPane.add(new Label("Username"), 0, 0);
        TextField usernameTextField = new TextField();
        gridPane.add(usernameTextField, 1, 0);
        gridPane.add(new Label("Password"), 0, 1);
        TextField passwordTextField = new TextField();
        gridPane.add(passwordTextField, 1, 1);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.add(new Button("Login"), 1, 2);
        gridPane.add(new Button("Register"), 1, 3);

        Button loginButton = (Button) getObjectInGrid(gridPane, 1, 2);
        loginButton.setMinSize(160, 25);
        Button registerButton = (Button) getObjectInGrid(gridPane, 1, 3);
        registerButton.setMinSize(160, 25);

        gridPane.setAlignment(Pos.CENTER);
        Label errorDisplayer = new Label();
        errorDisplayer.setTextFill(Color.web("#FF0000"));
        gridPane.addRow(0, errorDisplayer);

        loginButton.setOnMouseClicked(e -> {
            userService.login(usernameTextField.getText(), passwordTextField.getText());
            if (userService.authentificatedUserId != null) {
                loadUserPage(userService);
                stage.setScene(userPageScene);
            } else
                errorDisplayer.setText("Incorrent id or password");

        });

        registerButton.setOnMouseClicked(e -> {
            errorDisplayer.setText("Butonu ala nu l-am mai facut");
        });

        return gridPane;
    }

    private void loadUserPage(UserService userService) {
        BorderPane userPageSceneRootPanel = createUserPagePanel(userService);
        userPageScene = new Scene(userPageSceneRootPanel);
    }

    @Override
    public void start(Stage stage) {
        UserService userService = Main.createService();

        functieLuataCuCopyPaste(stage, userService);
    }

    public static void main(String[] args) {
        launch();
    }
    // comentariu paul 1
    // comentariu paul 2
}