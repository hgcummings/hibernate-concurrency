import org.hibernate.StaleObjectStateException;
import org.hibernate.event.internal.DefaultFlushEntityEventListener;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Flush entity event listener that checks whether the version of the entity is out-of-date. This is specifically
 * checking for the case when the version has been updated from application code.
 *
 * This is a common case for supporting optimistic concurrency in web applications/services, which typically have
 * minimal server-side state and would not keep a Hibernate session open from one request to the next.
 *
 * This class allows the original version passed out in one response to be used for an optimistic concurrency check in
 * a subsequent request. For example, the version may be rendered out to an HTML form as a hidden field, and bound back
 * to the persisted entity when the form is POSTed.
 */
public class VersionCheckingFlushEntityEventListener extends DefaultFlushEntityEventListener {
    /**
     * Check whether the entity to be flushed has a version matching the DB. If so, perform the default behaviour
     * (defined in the parent class); otherwise throw a StaleObjectStateException, as suggested in
     * http://docs.jboss.org/hibernate/orm/3.6/reference/en-US/html/transactions.html#transactions-optimistic-manual
     */
    @Override
    public void onFlushEntity(final FlushEntityEvent event) {
        EntityPersister entityPersister = event.getEntityEntry().getPersister();

        if (entityPersister.isVersioned()) {
            // This is the version at the point the EntityEntry was created (i.e., when the entity was read from the DB,
            // before the application code has a chance to change it)
            Object databaseVersion = event.getEntityEntry().getVersion();

            // This is the current version of the in-memory object, reflecting any changes made by the application code
            Object inMemoryVersion = entityPersister.getVersion(event.getEntity());

            if (!inMemoryVersion.equals(databaseVersion)) {
                throw new StaleObjectStateException(
                        event.getEntityEntry().getEntityName(),
                        event.getEntityEntry().getId());
            }
        }

        super.onFlushEntity(event);
    }
}