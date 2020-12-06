package socialnetwork.domain;

import socialnetwork.helpers.FriendshipRequestStatus;

import java.time.LocalDateTime;

public class FriendshipDTO extends Entity<Tuple<Long, Long>>{
    private String firstName, lastName;
    private FriendshipRequestStatus status = FriendshipRequestStatus.PENDING;
    private LocalDateTime date;

    public FriendshipDTO(String firstName, String lastName, FriendshipRequestStatus status, LocalDateTime date,Tuple<Long,Long> id) {
        super.setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.date = date;
    }

    //#region getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public FriendshipRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    //#endregion
}
