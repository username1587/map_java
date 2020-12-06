package socialnetwork.helpers;

import socialnetwork.domain.User;

/**
 * class that overrides User toString()
 * decorator design pattern - to string method is decorated
 */
public class UserToString extends User {
    public UserToString(User user) {
        super(user.getFirstName(), user.getLastName());
        super.setId(user.getId());
        super.setFriends(user.getFriends());
        super.setEmail(user.getEmail());
        super.setPassword(user.getPassword());
    }

    @Override
    public String toString() {
        return super.getFirstName() + " " + super.getLastName();
    }
}
