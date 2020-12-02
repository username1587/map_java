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
    public static void main(String[] args) {
//        String fileName= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
//        String fileName2=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendships");
//        String fileName3=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.messages");
        String fileName="data/users.csv";
        String fileName2="data/friendships.csv";
        String fileName3="data/messages.csv";
        Repository<Long, User> userFileRepository = new UserFile(fileName
                , new UserValidator());
        Repository<Tuple<Long,Long>, Friendship> friendshipFileRepository = new FriendshipFile(fileName2
                , new FriendshipValidator());
        Repository<Long, Message> repoMessages=new MessageFile(fileName3,new MessageValidator(),(UserFile)userFileRepository);
        UserService userService=new UserService(userFileRepository,friendshipFileRepository,repoMessages);
        UI ui=new UI(userService);
//        userFileRepository.findAll().forEach(System.out::println);
        //userService.getAll().forEach(x->System.out.println(x));
        ui.startMenu();

    }
}
