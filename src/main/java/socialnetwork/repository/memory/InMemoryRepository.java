package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private Validator<E> validator;
    protected Map<ID, E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<ID, E>();
    }

    @Override
    public E findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if (entities.get(entity.getId()) != null) {
            return entity;
        } else entities.put(entity.getId(), entity);
        return null;
    }

    @Override
    public E delete(ID id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("id is null");
        return entities.remove(id);
    }

    @Override
    public E update(E entity) {

        if (entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(), entity);

        if (entities.get(entity.getId()) != null) {
            entities.put(entity.getId(), entity);
            return null;
        }
        return entity;

    }

    @Override
    public int getSize(){
        return entities.size();
    }

//    @Override
//    public ID getLowestId() {
//        // TODO: map type should be ID not long
//        // TODO: this function should be implemented in
//        for (int i = 0; i < 1000; i++)
//            if (!entities.containsKey(i))
//                return i;
//        // TODO: throw exception here
//        return -1;
//    }

}
