package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/***
 * class that represents an user
 * every user has: first name,last name and a list of friends
 */
public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private List<Long> friends;

    /**
     * default constructor, all values get default values
     */
    public User() {
        friends = new ArrayList<>();
    }

    /**
     *
     * @param firstName initializes first name
     * @param lastName initializes last name
     */
    public User(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     *
     * @return first name of user
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     *
     * @param firstName new first name for user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return last name of user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName new last name for user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return a list with id's of all friends of user
     */
    public List<Long> getFriends() {
        return friends;
    }

    /**
     *
     * @param friends - new friend list for user
     */
    public void setFriends(List<Long> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friends +
                '}';
    }

    public String toStringWithId() {
        return "Utilizator{" +
                "ID='" + getId() + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friends +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }

}