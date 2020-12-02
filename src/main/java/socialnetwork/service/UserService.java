package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.helpers.FriendshipRequestStatus;
import socialnetwork.helpers.Graph;
import socialnetwork.helpers.Message;
import socialnetwork.repository.Repository;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UserService {
    private Repository<Long, User> repo;
    private Repository<Tuple<Long, Long>, Friendship> repoFriendships;
    private Repository<Long, Message> repoMessages;
    Graph graph;

    private final static Long highestId = 1000L;

    /**
     * @param repo            - users repository
     * @param repoFriendships - friendships repository
     */
    public UserService(Repository<Long, User> repo, Repository<Tuple<Long, Long>, Friendship> repoFriendships, Repository<Long, Message> repoMessages) {
        this.repo = repo;
        this.repoFriendships = repoFriendships;
        this.repoMessages = repoMessages;
        populateFriends();
    }

    public void sendMessage(Long idFrom, List<Long> idTo, String messageString) throws ValidationException {
        User user1 = repo.findOne(idFrom);
        if (user1 == null)
            throw new ValidationException("invalid idFrom");

        List<User> user2 = new ArrayList<>();

        for (Long id : idTo) {
            if (repo.findOne(id) == null)
                throw new ValidationException("invalid id " + id);
            user2.add(repo.findOne(id));
        }

        for (User user : user2) {
            Long id = createIdForMessage();
            Message message = new Message(user1, user, messageString, id);
            repoMessages.save(message);
        }
    }

    /**
     * @return all messages between 2 users using a Message/ReplyMessage class
     */
    public Message getMessagesBetween2Users(Long idUser1, Long idUser2) {
        List<Message> messageList = new ArrayList<>();
        for (Long i = 0L; i < repoMessages.getSize(); i++) {
            Message message = repoMessages.findOne(i);
            if (message.getFrom().getId().equals(idUser1) && message.getTo().getId().equals(idUser2) ||
                    message.getFrom().getId().equals(idUser2) && message.getTo().getId().equals(idUser1)) {
                messageList.add(message);
            }
        }

        Message result = null;
        Message resultAux = null;

        if (messageList.isEmpty()) {
            return result;
        }

        result = messageList.get(0);
        resultAux = result;

        for (int i = 1; i < messageList.size(); i++) {
            Message currentMessage = messageList.get(i);
            resultAux.setReply(currentMessage);
            resultAux = resultAux.getReply();
        }

        return result;
    }

    /**
     * @return lowest id available for repoMessages
     */
    private Long createIdForMessage() {
        for (Long i = 0L; i < highestId; i++)
            if (repoMessages.findOne(i) == null)
                return i;
        throw new RuntimeException("no available id");
    }

    /**
     * @param userId
     * @return true if the user with id userId was delete
     * false, else
     */
    public boolean deleteUser(long userId) {
        // TODO
        try {
            repo.findOne(userId);
        } catch (IllegalArgumentException exception) {
            return false;
        }
        // delete all friendships that contains userId
        List<Friendship> friendshipsToDelete = new ArrayList<>();
        repoFriendships.findAll().forEach(x -> {
            if (x.getId().getLeft() == userId || x.getId().getRight() == userId)
                friendshipsToDelete.add(x);
        });

        for (Friendship friendship : friendshipsToDelete) {
            repoFriendships.delete(friendship.getId());
        }

        // delete user
        repo.delete(userId);

        return true;
    }

    /**
     * takes friendships from repoFriendships and adds them to users.friends from repo
     */
    private void populateFriends() {
        // TODO: aici arunc exceptie daca vreun id din lista de prietenii din friendships nu are corespondent in users
        repoFriendships.findAll().forEach(x -> {
            Long friend1 = repo.findOne(x.getId().getRight()).getId();
            Long friend2 = repo.findOne(x.getId().getLeft()).getId();
            repo.findOne(x.getId().getLeft()).getFriends().add(friend1);
            repo.findOne(x.getId().getRight()).getFriends().add(friend2);
        });
    }

    /**
     * @param user - user to add in repo
     * @return null if the entity is saved
     * user    if the entity is not saved (already exists)
     */
    public User addUser(User user) {
        User task = repo.save(user);

        return task;
    }

    /**
     * @return all users
     */
    public Iterable<User> getAll() {
        return repo.findAll();
    }

    /**
     * @return user repository
     */
    public Repository getRepo() {
        return repo;
    }

    /**
     * adds friend to user and updates files
     *
     * @param user   - user selected to add friendship
     * @param friend - friend to add to user
     * @return null         if friend already exists
     * friend      ,else
     */
    public User addFriend(User user, User friend) {
        boolean hasThisFriend = false;

        List<Long> friends = user.getFriends();
        for (Long _friend : friends)
            if (_friend.equals(friend.getId())) {
                hasThisFriend = true;
                break;
            }
        if (hasThisFriend)
            return null;

        friends.add(friend.getId());
        User newUser = new User(user.getFirstName(), user.getLastName());
        newUser.setId(user.getId());
        newUser.setFriends(friends);

        Friendship friendship = new Friendship();
        Tuple<Long, Long> tuple = new Tuple<>(user.getId(), friend.getId());
        friendship.setId(tuple);

        // TODO: Somewhere have to check if there is User A who has Friend B but User B has no Friend A (users.csv)
        // TODO: Somewhere have to check if there are 2 friendships with same ids,but different order (friendships.csv)
        List<Long> friends2 = friend.getFriends();
        friends2.add(user.getId());
        User newUser2 = new User(friend.getFirstName(), friend.getLastName());    // friend also adds user
        newUser2.setId(friend.getId());
        newUser2.setFriends(friends2);

        //TODO: asa de ce nu merge?
        /*friendship.getId().setLeft(user.getId());
        friendship.getId().setRight(friend.getId());*/

        repo.update(newUser);
        repo.update(newUser2);
        repoFriendships.save(friendship);
        return friend;
    }

    /**
     * @param user   - user to remove friendship from
     * @param friend - friend to add to user.friends
     * @return null         ,if friend is not deleted
     * friend      ,else
     */
    public User deleteFriend(User user, User friend) {
        // TODO: la asta am ramas
        // TODO: return null if friend is not a friend of user
        boolean hasThisFriend = false;

        List<Long> friends = user.getFriends();
        for (Long _friend : friends)
            if (_friend.equals(friend.getId())) {
                hasThisFriend = true;
                break;
            }
        if (!hasThisFriend)
            return null;

        // delete user1 and friendship1
        friends.remove(friend.getId());
        User newUser = new User(user.getFirstName(), user.getLastName());
        newUser.setId(user.getId());
        newUser.setFriends(friends);

        Friendship friendship = new Friendship();
        Tuple<Long, Long> tuple = new Tuple<>(user.getId(), friend.getId());
        friendship.setId(tuple);

        //TODO: asa de ce nu merge? sa ma uit de ce nu poti modifica valorile luate cu getter
        /*friendship.getId().setLeft(user.getId());
        friendship.getId().setRight(friend.getId());*/

        repo.update(newUser);
        repoFriendships.delete(friendship.getId());

        // delete user2 and friendship2

        List<Long> friends2 = friend.getFriends();

        friends2.remove(user.getId());
        User newUser2 = new User(friend.getFirstName(), friend.getLastName());
        newUser2.setId(friend.getId());
        newUser2.setFriends(friends2);

//        Friendship friendship2 = new Friendship();
//        Tuple<Long, Long> tuple2 = new Tuple<>(friend.getId(), friend.getId());
//        friendship2.setId(tuple2);

        repo.update(newUser2);
//        repoFriendships.delete(friendship2.getId());

        return friend;
    }

    /**
     * size of list, represent no of people with given name
     *
     * @param firstName - string user.firstName
     * @param lastName  - string user.lastName
     * @return list with all ppl with given first and last name
     */
    public List<User> findByName(String firstName, String lastName) {
        List<User> users = new ArrayList<>();

        for (User i : getAll())
            if (i.getFirstName().equals(firstName) && i.getLastName().equals(lastName))
                users.add(i);

        return users;
    }

    /**
     * @return lowest id available
     */
    public long getLowestId() {
        List<Long> allIDs = new ArrayList<>();
        repo.findAll().forEach(x -> allIDs.add(x.getId()));

        for (long i = 0; i < 1000; i++)
            if (!allIDs.contains(i))
                return i;
        // TODO: throw exception here
        return -1;
    }

    public int getNoCommunities() {
        graph = new Graph(repo);
        return graph.getNoCommunities();
    }

    /**
     * @return size of longest elementary path in graph
     */
    public int getFriendliestCommunitySize() {
        graph = new Graph(repo);
        return graph.getFriendliestCommunitySize();
    }

    /**
     * @return users that are part of the largest community (largest conext component)
     */
    public List<Long> getFriendliestCommunity() {
        graph = new Graph(repo);
        return graph.getFriendliestCommunity();
    }

    /**
     * @param idUser id of a user
     * @return a list of all friendships which include user who has id equal to idUser
     */
    public List<Friendship> getFriendshipsOfUser(long idUser) {
        Iterable<Friendship> friendships = repoFriendships.findAll();

        List<Friendship> result = new ArrayList<>();

        for (Friendship friendship : friendships) {
            if (friendship.getId().getLeft() == idUser || friendship.getId().getRight() == idUser)
                result.add(friendship);
        }
        return result;
    }

    /**
     * @param userId id of user
     * @param month  month when the friendship has started
     * @return all friendships as a list (not iterable) of a user in a certain month
     */
    public List<Friendship> getAllFriendshipsByUserAndMonth(Long userId, Month month) {
        List<Friendship> result = new ArrayList<>();
        repoFriendships.findAll().forEach(result::add);

        Stream<Friendship> stream = result
                .stream()
                .filter(friendship ->
                        friendship.getDate().getMonth().compareTo(month) == 0)
                .filter(friendship ->
                        friendship.getId().getLeft().equals(userId) ||
                                friendship.getId().getRight().equals(userId));

        // TODO: intrebi de ce nu merge sa stergi continutu din result si sa readaugi continutu din stream inapoi in result
//        result.clear();
        List<Friendship> result2 = new ArrayList<>();

        stream.forEach(x -> result2.add((Friendship) x));

        return result2;
    }

    /**
     * @param idUser - id of user
     * @return list of friendships represints all pending friend requests of user
     */
    public List<Friendship> getAllPendingReq(Long idUser) {
        List<Friendship> res = new ArrayList<>();
        repoFriendships.findAll().forEach(friendship -> {
            if ((friendship.getId().getRight().equals(idUser) ||
                    friendship.getId().getLeft().equals(idUser)) &&
                    friendship.getStatus().equals(FriendshipRequestStatus.PENDING))
                res.add(friendship);
        });
        return res;
    }

    public void acceptOrRejectFriendReq(Long idUserFrom, Long idUserTo, FriendshipRequestStatus desiredFriendshipRequestStatus) {
        Tuple<Long, Long> friendshipId = new Tuple<>(-1L, -1L);
        repoFriendships.findAll().forEach(friendship -> {
            if (friendship.getStatus().equals(FriendshipRequestStatus.PENDING) &&
                    (friendship.getId().getLeft().equals(idUserFrom) &&
                            friendship.getId().getRight().equals(idUserTo) ||
                            friendship.getId().getRight().equals(idUserFrom) &&
                                    friendship.getId().getLeft().equals(idUserTo))) {
                friendshipId.setLeft(friendship.getId().getLeft());
                friendshipId.setRight(friendship.getId().getRight());
            }
        });
        if (friendshipId.getLeft().equals(-1L) && friendshipId.getRight().equals(-1L)) {
            throw new RuntimeException("friendship not found");
        }

        if (desiredFriendshipRequestStatus.equals(FriendshipRequestStatus.PENDING))
            throw new RuntimeException("you can't set friendship request status to pending");

        Friendship friendship = repoFriendships.findOne(friendshipId);
        friendship.setStatus(desiredFriendshipRequestStatus);

        repoFriendships.update(friendship);
    }
}
