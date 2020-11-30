package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship friendship) throws ValidationException {
        if (friendship.getId()==null || friendship.getId().getLeft()==null || friendship.getId().getRight()==null)
            throw new ValidationException("Id can not be null");
        if (friendship.getDate()==null)
            throw new ValidationException("Date can not be null");
    }
}
