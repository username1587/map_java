import javafx.application.Application;
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
    UserService userService;

    TableView<FriendshipDTO> tableView;
    ListView<UserToString> friendsListView;
    ObservableList<UserToString> modelFriends /*= FXCollections.observableArrayList()*/;
    ObservableList<UserToString> modelUsers = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> modelFriendships /*= FXCollections.observableArrayList()*/;
    List<FriendshipDTO> friendshipRequestsList;
    List<UserToString> friendsList=new ArrayList<>();

    private TextField textFieldPrefixAddFriend;

    Tab tab3;
    ListView<UserToString> listViewFriendsFromMessages;
    ListView<Message> listViewMessages;
    ObservableList<Message> messageList= FXCollections.observableArrayList();

    private void loadFriendshipRequestsList() {
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

        if (modelFriendships ==null)
            modelFriendships = FXCollections.observableArrayList(friendshipRequestsList);
        else
            modelFriendships.setAll(friendshipRequestsList);

//        friendshipsModel = FXCollections.observableArrayList(friendshipRequestsList);
//        friendshipsModel.addListener((InvalidationListener) x -> {
//            tableView = getFriendshipDTOTableView(userService);
//        });
    }

    private void functieLuataCuCopyPaste(Stage stage) {
        stage.show();
        stage.setTitle("aici la titlu nu stiu exact ce nume sa pun asa ca o las chestia asta asa");
        stage.setHeight(600);
        stage.setWidth(900);

        GridPane authentificationSceneRootPanel = createTable(stage);
        authentificationScene = new Scene(authentificationSceneRootPanel);

        stage.setScene(authentificationScene);

    }

    private BorderPane createUserPagePanel() {
        BorderPane userPageSceneRootPanel = new BorderPane();
        updateModelFriends();
        friendsListView = new ListView<>(modelFriends);

        BorderPane userPageSceneMyPagePanel = new BorderPane();

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab tab = new Tab();
        tab.setText("pagina mea");
        tab.setContent(userPageSceneMyPagePanel);
        Tab tab2 = new Tab();
        tab2.setText("cererile de prietenie");
        loadFriendRequestsPanel();
        tab2.setContent(friendRequestPanel);
        tab3 = new Tab();
        tab3.setText("mesaje");
        tabPane.getTabs().addAll(tab, tab2, tab3); // TODO: sa pun tabpane u asta undeva

        userPageSceneRootPanel.setCenter(tabPane);

        Object userBeforeCheck = userService.getRepo().findOne(userService.authentificatedUserId);
        if (userBeforeCheck == null)
            throw new RuntimeException("user is null");
        if (!(userBeforeCheck instanceof User))
            throw new RuntimeException("user not found");

        User currentUser = (User) userBeforeCheck;

        textFieldPrefixAddFriend = new TextField();
        textFieldPrefixAddFriend.setPromptText("adauga un prieten...");
        Button buttonAddFriend = new Button("adauga prieten");
        Button buttonDelFriend = new Button("sterge prieten");
//        borderPane.getChildren().addAll(comboBox,listView);
        userPageSceneMyPagePanel.setLeft(friendsListView);
//        borderPane.setRight(comboBox);
        VBox vBoxRightGridPane = new VBox();
        ListView<UserToString> listViewUsers = new ListView<>(modelUsers);

        updateModelUsers();
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
                updateModelFriends();
                updateModelUsers();
                errorDisplayer.setTextFill(Color.web("#FFFFFF"));
                errorDisplayer.setText("prietenul a fost sters");
            }
            updateModelFriends();
            updateModelUsers();
            loadFriendshipRequestsList();
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
            updateModelFriends();
            updateModelUsers();

            errorDisplayer.setTextFill(Color.web("#000000"));
            errorDisplayer.setText("prieten adaugat");
            updateModelFriends();
            updateModelUsers();
            loadFriendshipRequestsList();
        });

        tab3.setOnSelectionChanged(event -> {
            if (tab3.isSelected())
                populateMessageTab();
        });

        textFieldPrefixAddFriend.textProperty().addListener(x -> handleFilter(textFieldPrefixAddFriend));

        return userPageSceneRootPanel;
    }

    // populates tab3
    private void populateMessageTab() {
        if (listViewFriendsFromMessages.getItems()==null)
            listViewFriendsFromMessages.setItems(modelFriends);

        HBox mainPanel = new HBox();
        tab3.setContent(mainPanel);
        if (listViewFriendsFromMessages.getSelectionModel().getSelectedItem() != null){
            Long selectedUserId = listViewFriendsFromMessages.getSelectionModel().getSelectedItem().getId();
            List<Message> userServiceMessagesList = userService.getMessagesBetween2UsersInList(userService.authentificatedUserId, selectedUserId);
            messageList.setAll(userServiceMessagesList);
        }
        listViewMessages = new ListView<>(messageList);
        listViewMessages.setPrefWidth(600);
        listViewFriendsFromMessages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        VBox rightPanel=new VBox();
        TextField sendMessageTextField=new TextField();
        sendMessageTextField.setPromptText("Scrie un mesaj..");
        rightPanel.getChildren().addAll(listViewMessages,sendMessageTextField);
        mainPanel.getChildren().addAll(listViewFriendsFromMessages, rightPanel);

        listViewMessages.setCellFactory(list -> new ListCell<Message>(){
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (item!=null){
                    if (item.getFrom().getId().equals(userService.authentificatedUserId)) {
                        setAlignment(Pos.BASELINE_RIGHT);
                    } else {
                        setAlignment(Pos.BASELINE_LEFT);
                    }
                    setText(item.getMessageString());
                }
                else{
                    setText("");
                }
            }
        });

        sendMessageTextField.setOnAction(event->{
            List<Long> to=new ArrayList<>();
            listViewFriendsFromMessages.getSelectionModel().getSelectedItems().forEach(item->to.add(item.getId()));
            String Message=sendMessageTextField.getText();
            userService.sendMessage(userService.authentificatedUserId,to,Message);
            sendMessageTextField.clear();

            Long selectedUserId = listViewFriendsFromMessages.getSelectionModel().getSelectedItem().getId();
            List<Message> userServiceMessagesList = userService.getMessagesBetween2UsersInList(userService.authentificatedUserId, selectedUserId);
//            messageList= FXCollections.observableArrayList(userServiceMessagesList);

            messageList.setAll(userServiceMessagesList);

//            listViewMessages.setItems(messageList);

            listViewMessages.scrollTo(listViewMessages.getItems().size()-1);
        });

    }

    private void initializeObjects(){
        listViewFriendsFromMessages = new ListView<>(modelFriends);
    }

    private void initializeActions(){
        listViewFriendsFromMessages.getSelectionModel().selectedItemProperty().addListener(e->populateMessageTab());
    }

    private void handleFilter(TextField textField) {
        Predicate<UserToString> firstNamePredicate = x -> x.getFirstName().startsWith(textField.getText());
        Predicate<UserToString> lastNamePredicate = x -> x.getLastName().startsWith(textField.getText());

        List<UserToString> listUsers = new ArrayList<>();
        userService.getAll().forEach(el -> listUsers.add(new UserToString(el)));

        modelUsers.setAll(listUsers
                .stream()
                .filter(firstNamePredicate.or(lastNamePredicate))
                .collect(Collectors.toList()));
    }

    private void loadFriendRequestsPanel() {
        friendRequestPanel = new VBox();
        friendRequestPanel.setSpacing(10f);
        tableView = getFriendshipDTOTableView();

//        new TextField().textProperty().addListener(x->handleFilter());
//        tableColumnTema.textProperty().addListener(x->handleFilter());
//        tableColumnNota.textProperty().addListener(x->handleFilter());

        HBox hBox = new HBox();
        Button acceptFriendReq = new Button("accepta prietenie");
        Button declineFriendReq = new Button("refuza prietenie");
        hBox.getChildren().addAll(acceptFriendReq,declineFriendReq);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        friendRequestPanel.getChildren().addAll(tableView, hBox);


//        friendshipsModel.addListener((InvalidationListener) x -> loadFriendRequestsPanel());
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
            loadFriendshipRequestsList();
            updateModelFriends();
        });

        declineFriendReq.setOnMouseClicked(event -> {
            FriendshipDTO friendshipDTO = tableView.getSelectionModel().getSelectedItem();
            try{
                userService.acceptOrRejectFriendReq(friendshipDTO.getId().getLeft(), friendshipDTO.getId().getRight(), FriendshipRequestStatus.REJECTED);
            }
            catch (RuntimeException exception){
                // nimic
            }
            loadFriendshipRequestsList();
            updateModelFriends();
            updateModelUsers();
        });
    }

    private TableView<FriendshipDTO> getFriendshipDTOTableView() {
        TableView<FriendshipDTO> tableView = new TableView<>();
        TableColumn<FriendshipDTO, String> firstNameColumn = new TableColumn<>("First Name");
        TableColumn<FriendshipDTO, String> lastNameColumn = new TableColumn<>("Last Name");
        TableColumn<FriendshipDTO, FriendshipRequestStatus> statusColumn = new TableColumn<>("status");
        TableColumn<FriendshipDTO, LocalDateTime> dateColumn = new TableColumn<>("date");

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(firstNameColumn, lastNameColumn, statusColumn, dateColumn);

        loadFriendshipRequestsList();
//        friendshipsModel.setAll(friendshipRequestsList);

//        friendshipsModel.addAll(userService.getFriendsOfUser(userService.authentificatedUserId));
        tableView.setItems(modelFriendships);

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("firstName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, FriendshipRequestStatus>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, LocalDateTime>("date"));
        return tableView;
    }

    private void updateModelFriends() {
//        modelFriends.setAll(userService.getFriendshipsOfUser(userService.authentificatedUserId)
//                        .stream()
//                        .filter(friendship -> friendship.getStatus().equals(FriendshipRequestStatus.APPROVED))
//                        .map(friendship -> {
//                            UserToString user;
//                            if (friendship.getId().getLeft().equals(userService.authentificatedUserId)) {
//                                User userAux = (User) (userService.getRepo().findOne(friendship.getId().getRight()));
//                                user = new UserToString(userAux);
//                            } else {
//                                User userAux = (User) (userService.getRepo().findOne(friendship.getId().getLeft()));
//                                user = new UserToString(userAux);
//                            }
//                            return user;
//                        })
//                        .collect(Collectors.toList())
//        );
        friendsList.clear();
        friendsList.addAll(userService.getFriendshipsOfUser(userService.authentificatedUserId)
                .stream()
                .filter(friendship -> friendship.getStatus().equals(FriendshipRequestStatus.APPROVED))
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
        if (modelFriends==null)
            modelFriends = FXCollections.observableArrayList(friendsList);
        else
            modelFriends.setAll(friendsList);

    }

    private void updateModelUsers() {
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

    private GridPane createTable(Stage stage) {
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
                loadUserPage();
                stage.setScene(userPageScene);
            } else
                errorDisplayer.setText("Incorrent id or password");

        });

        registerButton.setOnMouseClicked(e -> {
            errorDisplayer.setText("Butonu ala nu l-am mai facut");
        });

        return gridPane;
    }

    private void loadUserPage() {
        BorderPane userPageSceneRootPanel = createUserPagePanel();
        userPageScene = new Scene(userPageSceneRootPanel);
    }

    @Override
    public void start(Stage stage) {
        userService = Main.createService();

        functieLuataCuCopyPaste(stage);
        initializeObjects();
        initializeActions();
    }

    public static void main(String[] args) {
        launch();
    }
}