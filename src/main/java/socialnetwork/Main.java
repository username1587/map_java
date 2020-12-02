package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.helpers.Message;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.FriendshipFile;
import socialnetwork.repository.file.MessageFile;
import socialnetwork.repository.file.UserFile;
import socialnetwork.service.UserService;
import socialnetwork.ui.UI;

public class Main {

    public static UserService createService() {
//        String fileName= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        String fileName = "data/users.csv";
        String fileName2 = "data/friendships.csv";
        String fileName3 = "data/messages.csv";
        Repository<Long, User> userFileRepository = new UserFile(fileName
                , new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipFileRepository = new FriendshipFile(fileName2
                , new FriendshipValidator());
        Repository<Long, Message> repoMessages = new MessageFile(fileName3, new MessageValidator(), (UserFile) userFileRepository);
        UserService userService = new UserService(userFileRepository, friendshipFileRepository, repoMessages);
        return userService;
    }

    // run this main for console app
    public static void main(String[] args) {
        UserService userService = createService();
        UI ui = new UI(userService);
        ui.startMenu();
    }
}
