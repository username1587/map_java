package socialnetwork.domain.validators;

import socialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User user) throws ValidationException {
        if (user.getId() == null)
            throw new ValidationException("Id can not be null");
        if (user.getId()<0)
            throw new ValidationException("Id can not be negative");

            if (user.getLastName()==null)
            throw new ValidationException("Last name can't be null");
        if (user.getLastName().matches("[ ]*"))
            throw new ValidationException("Last name can't be empty or formed only by spaces");
        if (user.getLastName().matches(".*;.*"))
            throw new ValidationException("Last name can't contain semicolon");

        if (user.getFirstName()==null)
            throw new ValidationException("First name can't be null");
        if (user.getFirstName().matches("[ ]*"))
            throw new ValidationException("First name can't be empty or formed only by spaces");
        if (user.getFirstName().matches(".*;.*"))
            throw new ValidationException("First name can't contain semicolon");

    }
}
