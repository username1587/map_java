package socialnetwork.domain;

import socialnetwork.helpers.FriendshipRequestStatus;

import java.time.LocalDateTime;

/***
 * class representing the boundary/friendship between 2 users
 */
public class Friendship extends Entity<Tuple<Long, Long>> {

    LocalDateTime date;
    FriendshipRequestStatus status;

    /**
     * default constuctor
     * initializes date with current date and time
     */
    public Friendship() {
        date = LocalDateTime.now();
    }

    public Friendship(LocalDateTime localDateTime){
        this.date=localDateTime;
    }

    //#region getters and setters
    public LocalDateTime getDate() {
        return date;
    }

    public FriendshipRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipRequestStatus status) {
        this.status = status;
    }
    //#endregion
}
