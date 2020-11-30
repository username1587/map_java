package socialnetwork.domain;

import java.io.Serializable;

/***
 * class that stores an id
 * @param <ID>
 */
public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    private ID id;

    /**
     *
     * @return id
     */
    public ID getId() {
        return id;
    }

    /**
     *
     * @param id - new id
     */
    public void setId(ID id) {
        this.id = id;
    }
}