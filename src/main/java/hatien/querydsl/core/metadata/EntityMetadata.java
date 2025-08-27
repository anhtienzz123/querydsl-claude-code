package hatien.querydsl.core.metadata;

import hatien.querydsl.core.path.EntityPath;
import hatien.querydsl.core.path.StringPath;
import hatien.querydsl.core.path.NumberPath;
import hatien.querydsl.core.path.Path;

import java.util.Map;
import java.util.HashMap;

/**
 * Abstract base class for entity metadata that provides path creation and management
 * for entity properties. This class serves as a foundation for generated query types
 * that need to create and manage path expressions for entity properties.
 *
 * @param <T> the entity type this metadata represents
 */
public abstract class EntityMetadata<T> {
    protected final EntityPath<T> entityPath;
    protected final Map<String, Path<?>> paths;
    
    /**
     * Constructs new EntityMetadata for the specified entity class and alias.
     *
     * @param entityClass the Class object representing the entity type
     * @param alias the alias to use for this entity in queries
     */
    public EntityMetadata(Class<T> entityClass, String alias) {
        this.entityPath = new EntityPath<>(entityClass, alias);
        this.paths = new HashMap<>();
    }
    
    /**
     * Returns the main EntityPath for this metadata's entity.
     *
     * @return the EntityPath representing this entity
     */
    public EntityPath<T> getEntityPath() {
        return entityPath;
    }
    
    /**
     * Creates and registers a StringPath for a string property of this entity.
     *
     * @param propertyName the name of the string property
     * @return a StringPath representing the property
     */
    protected StringPath createString(String propertyName) {
        StringPath path = new StringPath(propertyName, entityPath);
        paths.put(propertyName, path);
        return path;
    }
    
    /**
     * Creates and registers a NumberPath for a numeric property of this entity.
     *
     * @param <N> the numeric type that extends Number and Comparable
     * @param propertyName the name of the numeric property
     * @param type the Class object representing the numeric type
     * @return a NumberPath representing the property
     */
    protected <N extends Number & Comparable<N>> NumberPath<N> createNumber(String propertyName, Class<N> type) {
        NumberPath<N> path = new NumberPath<>(type, propertyName, entityPath);
        paths.put(propertyName, path);
        return path;
    }
    
    /**
     * Creates and registers an EntityPath for an entity-valued property of this entity.
     * This is used for associations/relationships to other entities.
     *
     * @param <E> the type of the associated entity
     * @param propertyName the name of the entity-valued property
     * @param type the Class object representing the associated entity type
     * @return an EntityPath representing the property
     */
    protected <E> EntityPath<E> createEntity(String propertyName, Class<E> type) {
        EntityPath<E> path = new EntityPath<>(type, propertyName, entityPath);
        paths.put(propertyName, path);
        return path;
    }
    
    /**
     * Retrieves a path by property name.
     *
     * @param propertyName the name of the property to retrieve
     * @return the Path for the specified property, or null if not found
     */
    public hatien.querydsl.core.path.Path<?> getPath(String propertyName) {
        return paths.get(propertyName);
    }
    
    /**
     * Returns a copy of all registered paths for this entity.
     *
     * @return a Map containing all property names and their corresponding paths
     */
    public Map<String, Path<?>> getAllPaths() {
        return new HashMap<>(paths);
    }
}