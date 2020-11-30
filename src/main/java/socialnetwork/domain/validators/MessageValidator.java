package socialnetwork.domain.validators;

import socialnetwork.helpers.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message message) throws ValidationException {
        if (message.getTo()==null || message.getFrom()==null)
            throw new ValidationException("user fields can not be null");
        if (message.getDate()==null)
            throw new ValidationException("date can not be null");
    }
}
