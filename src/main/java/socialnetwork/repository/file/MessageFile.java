package socialnetwork.repository.file;

import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;
import socialnetwork.helpers.Message;
import socialnetwork.repository.memory.InMemoryRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessageFile extends AbstractFileRepository<Long, Message> {
    private UserFile userFile;  // get reference to userFile so, I can store only Id for Users

    public MessageFile(String fileName, Validator<Message> validator, UserFile userFile) {
//        super(fileName, validator);
//        this.userFile=userFile;
        super(fileName, validator, true);
        this.userFile = userFile;
        super.loadData();
    }

    @Override
    public Message extractEntity(List<String> attributes) {
        Message message = new Message(Long.parseLong(attributes.get(1)), Long.parseLong(attributes.get(2)), attributes.get(3), userFile, Long.parseLong(attributes.get(0)));

        // set date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(attributes.get(4), formatter);
        message.setDate(dateTime);

        return message;
    }

    @Override
    protected String createEntityAsString(Message message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return message.getId() + ";" + message.getFrom().getId() + ";" + message.getTo().getId() + ";" + message.getMessageString() + ";" + message.getDate().format(dtf);
    }

//    @Override
//    public Message save(Message message) {
//        Message oldMessage=this.findOne(message.getId());
//        if (oldMessage == null)
//            return super.save(message);
//        else
//            return super.save(new ReplyMessage(oldMessage.getFrom(), oldMessage.getTo(), message.getMessageString(), oldMessage));
//    }
}
