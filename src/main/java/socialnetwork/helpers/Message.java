package socialnetwork.helpers;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.repository.file.UserFile;

import java.time.LocalDateTime;

public class Message extends Entity<Long> {
    private User from;
    private User to;
    private String message;
    private LocalDateTime date;

    private Message reply;

    public Tuple<Long, Long> generateId() {
        return new Tuple<>(from.getId(), to.getId());
    }

    public Message(User from, User to, String message, Long id) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        super.setId(id);
//        super.setId(new Tuple<Long,Long>(from.getId(),to.getId()));
    }

    public Message(Long idFrom, Long idTo, String message, UserFile userFile,Long id){
        this.from=userFile.findOne(idFrom);
        this.to=userFile.findOne(idTo);
        if (this.from==null || this.to==null)
            throw new RuntimeException("invalid user");
        this.message=message;
        this.date=LocalDateTime.now();
        super.setId(id);
    }

    //#region setters and getters
    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getMessageString() {
        return message;
    }

    public void setMessageString(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    //#endregion
}

