package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;
import socialnetwork.helpers.FriendshipRequestStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {

    public FriendshipFile(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(attributes.get(2), formatter);

        Friendship friendship = new Friendship(dateTime);

        Tuple<Long, Long> tuple = new Tuple<>(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1)));
        friendship.setId(tuple);

        friendship.setStatus(FriendshipRequestStatus.valueOf(attributes.get(3)));
        // TODO: de ce nu merge asta?
//        friendship.getId().setLeft(Long.parseLong(attributes.get(0)));
//        friendship.getId().setRight(Long.parseLong(attributes.get(1)));
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship friendship) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return friendship.getId().getLeft() + ";" + friendship.getId().getRight() + ";" + friendship.getDate().format(dtf) + ";" + friendship.getStatus().toString();
    }

}
