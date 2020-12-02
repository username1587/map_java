package socialnetwork.ui;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.helpers.FriendshipRequestStatus;
import socialnetwork.helpers.Message;
import socialnetwork.service.UserService;

import java.time.DateTimeException;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UI {
    // final in java e diferit de const din c++ cand e vorba de clase
    private final UserService userService;

    public UI(UserService userService) {
        this.userService = userService;
    }

    /**
     * function that starts executing the menu
     */
    public void startMenu() {
        while (true) {
            showOptions();
            int option = readOption();
            executeOption(option);
            if (option == 0)
                break;
        }
    }

    /**
     * function that executes given option in menu
     *
     * @param option - int from 0 to 8
     *               represents option to execute
     */
    private void executeOption(int option) {
        if (option == 0) {
            return;
        } else if (option == 1) {
            printUsers();
        } else if (option == 2) {
            User user = readUser();
            addUser(user);
        } else if (option == 3) {
            deleteUser();
        } else if (option == 4) {
            addFriend();
        } else if (option == 5) {
            deleteFriend();
        } else if (option == 6) {
            getNoCommunities();
        } else if (option == 7) {
            getFriendliestCommunitySize();
        } else if (option == 8) {
            printFriendliestCommunity();
        } else if (option == 9) {
            printFriendshipsOfUser();
        } else if (option == 10) {
            printFriendshipsOfUserAndMonth();
        } else if (option == 11) {
            sendMessage();
        } else if (option == 12) {
            printAllMessagesBetween2Users();
        } else if (option == 13) {
            manageFriendReq();
        } else if (option == 14) {
            showPendingFriendReq();
        } else
            invalidOption();
    }

    private void manageFriendReq() {
        //TODO
        Scanner scanner = new Scanner(System.in);

        System.out.println("user id=");
        Long userId = scanner.nextLong();

        System.out.println("friend id=");
        Long friendId = scanner.nextLong();

        System.out.println("accept friendship request? Answer with: 1-true or 0-false");
        int friendshipStatus = scanner.nextInt();
        FriendshipRequestStatus _friendshipStatus;

        if (friendshipStatus == 0)
            _friendshipStatus = FriendshipRequestStatus.REJECTED;
        if (friendshipStatus == 1)
            _friendshipStatus = FriendshipRequestStatus.APPROVED;
        else {
            System.out.println("invalid option");
            return;
        }

        userService.acceptOrRejectFriendReq(userId, friendId, _friendshipStatus);
    }

    private void showPendingFriendReq() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("user id=");
        Long userId = scanner.nextLong();

        List<Friendship> friendshipList = userService.getAllPendingReq(userId);

        friendshipList.forEach(friendship -> {
            Long otherUserId;
            if (friendship.getId().getLeft().equals(userId))
                otherUserId = friendship.getId().getRight();
            else
                otherUserId = friendship.getId().getLeft();

            System.out.println(otherUserId + " " + friendship.getStatus() + "");
        });

    }

    private void invalidOption() {
        System.out.println("-----");
        System.out.println("invalid option");
        System.out.println("-----");
    }

    private void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("id user from=");
        Long idUserFrom = scanner.nextLong();
        scanner.nextLine();//skips the enter

        System.out.println("id user to (use space for multiple users)=");
        List<Long> idUsersTo = new ArrayList<>();
        String line=scanner.nextLine();
        String lineRegex= "([0-9]+[ ]?)+";
        if (!line.matches(lineRegex)){
            System.out.println("invalid users");
            return ;
        }
        try{
            String[] args=line.split(" ");
            for (String arg:args)
                idUsersTo.add(Long.parseLong(arg));
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
//        idUsersTo.add(scanner.nextLong());

//        while (scanner.nextLine().equals(" ")) { // this skips the \n after the 2nd id
//            idUsersTo.add(scanner.nextLong());
//        }
        System.out.println("mesage=");
        String message = scanner.nextLine();

        try{
            userService.sendMessage(idUserFrom, idUsersTo, message);
        }
        catch (ValidationException exception){
            System.out.println(exception.getMessage());
        }
    }

    private void printAllMessagesBetween2Users() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("id user 1 =");
        Long idUser = scanner.nextLong();

        System.out.println("id user 2 =");
        Long idUser2 = scanner.nextLong();

        Message message = userService.getMessagesBetween2Users(idUser, idUser2);

        if (message == null) {
            System.out.println("nu exista mesaje intre cei 2 utilizatori");
            return;
        }

        while (message != null) {
            System.out.println(message.getMessageString() + "   from:" + message.getFrom().getId() + " to:" + message.getTo().getId());
            message = message.getReply();
        }
    }

    /**
     * read a user and a month and prints all friendships of that user that have started in that month
     */
    private void printFriendshipsOfUserAndMonth() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("user id=");
        long userId = scanner.nextLong();
        System.out.println("month (from 1 to 12)=");
        int monthId = scanner.nextShort();
        Month month;
        try {
            month = Month.of(monthId);
        } catch (DateTimeException exception) {
            System.out.println(exception.getMessage());
            return;
        }

        List<Friendship> friendships = userService.getAllFriendshipsByUserAndMonth(userId, month);
        friendships.forEach(friendship -> {
            Long friendId;
            if (!friendship.getId().getLeft().equals(userId))
                friendId = friendship.getId().getLeft();
            else
                friendId = friendship.getId().getRight();

            User friend = (User) userService.getRepo().findOne(friendId);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            System.out.println(friend.getFirstName() + " " + friend.getLastName() + " " + friendship.getDate().format(dtf) + " " + friendship.getStatus());
        });
    }

    /**
     * read a user by id and print his friendships
     */
    private void printFriendshipsOfUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("user id=");
        long userID = scanner.nextLong();
        if (userService.getRepo().findOne(userID) == null) {
            System.out.println("invalid id");
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        userService.getFriendshipsOfUser(userID)
                .forEach(friendship -> {
                    long idOfMyUser;
                    if (friendship.getId().getLeft() != userID)
                        idOfMyUser = friendship.getId().getLeft();
                    else
                        idOfMyUser = friendship.getId().getRight();
                    User friend = (User) userService.getRepo().findOne(idOfMyUser);
                    if (friend == null)
                        throw new RuntimeException("user not found");
                    System.out.println(friend.getFirstName() + " " + friend.getLastName() + " " + friendship.getStatus() + " " + friendship.getDate().format(dtf));
                });
    }

    /**
     * reads a user and a friend and then delets them
     */
    private void deleteFriend() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("user's id=");
        long userId = scanner.nextLong();

        User user = (User) userService.getRepo().findOne(userId);

        scanner.nextLine();  // this skips the enter after id

        System.out.print("friend's first name=");
        String firstName = scanner.nextLine();

        System.out.print("friend's last name=");
        String lastName = scanner.nextLine();

        User friendWithoutId = new User(firstName, lastName);
        List<User> peopleWithGivenName = userService.findByName(friendWithoutId.getFirstName(), friendWithoutId.getLastName());

        if (peopleWithGivenName.size() == 0) {
            System.out.println("There are no people with the selected name");
            return;
        }

        if (peopleWithGivenName.size() == 1) {
            if (userService.deleteFriend(user, peopleWithGivenName.get(0)) != null)
                System.out.println("Friend deleted");
            else
                System.out.println("Selected friend is not a friend of user");
//            userService.addFriend(user,peopleWithGivenName.get(0));
            return;
        }

        // case when there are more ppl with given name
        System.out.println("There are more people with the given name. Select one by id:");
        for (User i : peopleWithGivenName)
            System.out.println(i.toStringWithId());

        // TODO: check for invalid id's
        long newUsedId = scanner.nextLong();
        User selectedFriend = (User) userService.getRepo().findOne(newUsedId);

        userService.deleteFriend(user, selectedFriend);
        System.out.println("Friend deleted");
    }

    /**
     * reads a user and a friend and adds friend to user
     */
    private void addFriend() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("user's id=");
        long userId = scanner.nextLong();

        User user = (User) userService.getRepo().findOne(userId);

        scanner.nextLine();  // this skips the enter after id

        System.out.print("friend's first name=");
        String firstName = scanner.nextLine();

        System.out.print("friend's last name=");
        String lastName = scanner.nextLine();

        User friendWithoutId = new User(firstName, lastName);
        List<User> peopleWithGivenName = userService.findByName(friendWithoutId.getFirstName(), friendWithoutId.getLastName());

        if (peopleWithGivenName.size() == 0) {
            System.out.println("There are no people with the selected name");
            return;
        }

        if (peopleWithGivenName.size() == 1) {
            userService.addFriend(user, peopleWithGivenName.get(0));
            System.out.println("Friend added");
            return;
        }

        // case when there are more ppl with given name
        System.out.println("There are more people with the given name. Select one by id:");
        for (User i : peopleWithGivenName)
            System.out.println(i.toStringWithId());

        long newUsedId = scanner.nextLong();
        User selectFriend = (User) userService.getRepo().findOne(newUsedId);

        userService.addFriend(user, selectFriend);
        System.out.println("Friend added");
    }

    /**
     * reads a user and returns it
     *
     * @return user read
     */
    private User readUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("first name=");
        String firstName = scanner.nextLine();

        System.out.print("last name=");
        String lastName = scanner.nextLine();

        User user = new User(firstName, lastName);
        user.setId(userService.getLowestId());
        return user;
    }

    /**
     * prints all available options
     */
    private void showOptions() {
        System.out.println("0-exit");
        System.out.println("1-print users");
        System.out.println("2-add user");
        System.out.println("3-delete user");
        System.out.println("4-add friend");
        System.out.println("5-delete friend");
        System.out.println("6-print no communities");
        System.out.println("7-print most friendly community size");
        System.out.println("8-print most friendly community users");
        System.out.println("9-all friendships of a certain user");
        System.out.println("10-all friendships of a user from a given month");
        System.out.println("11-send message");
        System.out.println("12-print all messages between 2 users");
        System.out.println("13-manage friend requests");
        System.out.println("14-show pending friend requests of user");
    }

    /**
     * prints all users and their friends
     */
    private void printUsers() {
        System.out.println("-----");
//        userService.getAll().forEach(System.out::println);
        userService.getAll().forEach(x -> System.out.println(x.toStringWithId()));
        System.out.println("-----");
    }

    /**
     * reads an user and adds him
     *
     * @param user - user to add
     */
    private void addUser(User user) {
        try {
            userService.addUser(user);
        } catch (Exception r) {
            System.out.println(r.getMessage());
            return;
        }
        System.out.println("User succesfully added !");
    }

    /**
     * reads an user and delets it
     */
    private void deleteUser() {
        // read first and last name
        Scanner scanner = new Scanner(System.in);
        System.out.print("first name=");
        String firstName = scanner.nextLine();
        System.out.print("last name=");
        String lastName = scanner.nextLine();

        // check if there is 0,1 or more people with the given name
        Iterable<User> listPeople = userService.getAll();
        int noPeople = 0;
        long personID = -1;
        for (User i : listPeople) {
            if (i.getFirstName().startsWith(firstName) && i.getLastName().startsWith(lastName)) {
                noPeople++;
                personID = i.getId();
            }
        }

        if (noPeople == 0) {   // there are no people with the given name
            System.out.println("There are no people with the given name");
            return;
        }

        if (noPeople == 1) {
            try {
                if (!userService.deleteUser(personID)) {
                    System.out.println("Error, key not found or key value was null");
                    return;
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println("Deletion error occured");
                return;
            }
            System.out.println("Deletion succeed");
            return;
        }

//        // case if there are 2 or more ppl with the same name
//        List<User> pplWithSameName = new ArrayList<>();
//        for (User i : listPeople)
//            if (i.getLastName().equals(lastName) && i.getFirstName().equals(firstName))
//                pplWithSameName.add(i);
        // case if there are 2 or more ppl with the same name
        List<User> pplWithSameName = new ArrayList<>();
        for (User i : listPeople)
            if (i.getLastName().startsWith(lastName) && i.getFirstName().startsWith(firstName))
                pplWithSameName.add(i);


        System.out.println("Choose a person to delete, by id:");
        for (User i : pplWithSameName)
            System.out.println(i.toStringWithId());

        long IDToDelete = scanner.nextLong();
        userService.deleteUser(IDToDelete);
    }

    /**
     * reads an option and returns it
     *
     * @return option read
     */
    private int readOption() {
        System.out.print("Selected option = ");

        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

//        BufferedReader my_reader = new BufferedReader(new InputStreamReader(System.in));
//        String optionAsString;
//        try{
//            optionAsString=my_reader.readLine();
//        }
//        catch(IOException exception){
//            optionAsString="-1";
//            System.out.println("could not read your input. The Selected option was set to 1");
//        }
//        return Integer.parseInt(optionAsString);

        return option;

    }

    /**
     * prints number of communities
     */
    private void getNoCommunities() {
        System.out.println(userService.getNoCommunities() + " communities");
    }

    /**
     * prints size of the friendliest community
     */
    private void getFriendliestCommunitySize() {
        System.out.println("length of max road is " + userService.getFriendliestCommunitySize());
    }

    /**
     * prints users that are part of the friendliest community
     */
    private void printFriendliestCommunity() {
        List<Long> users = userService.getFriendliestCommunity();
        System.out.println("Friendliest community users are:");
        System.out.println(users);
    }
}
