import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.integrator.spi.IntegratorService;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class OptimisticConcurrencyTest {

    private SessionFactory sessionFactory;

    protected Class getCustomFlushEntityEventListenerType() {
        return null;
    }

    @Before
    public void setup() throws Exception {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        StandardServiceRegistryBuilder serviceRegistryBuilder =
                new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());

        if (getCustomFlushEntityEventListenerType() != null) {
            serviceRegistryBuilder.addService(IntegratorService.class, new IntegratorService() {
                @Override
                public Iterable<Integrator> getIntegrators() {
                    return Collections.<Integrator>singleton(new TestIntegrator(getCustomFlushEntityEventListenerType()));
                }
            });
        }

        sessionFactory = configuration.buildSessionFactory(serviceRegistryBuilder.build());
    }

    @Test
    public void GivenEntityHasBeenUpdated_UpdatingOlderVersion_ThrowsStaleObjectStateException()
    {
        // Arrange
        long entityId = createEntity();
        updateEntity(entityId);

        // Act
        StaleObjectStateException exception = null;
        try {
            attemptUpdateOriginalVersion(entityId);
        }
        catch (StaleObjectStateException e)
        {
            exception = e;
        }

        // Assert
        assertNotNull(exception);
    }

    private long createEntity() {
        // An attempt to an update the entity binding back from an out-of-date version of the entity
        long entityId;
        DullEntity entity = new DullEntity();
        entity.setShortName("Original name");


        Session session = sessionFactory.openSession();
        session.save(entity);
        assertEquals(0, entity.getVersion());
        entityId = entity.getId();
        session.close();
        return entityId;
    }

    private void updateEntity(long entityId) {
        // Make a successful update to the entity
        Session session;
        session = sessionFactory.openSession();
        DullEntity winningUpdateEntity = (DullEntity)session.get(DullEntity.class, entityId);
        winningUpdateEntity.setShortName("Winning updated name");
        session.save(winningUpdateEntity);
        session.flush();
        session.close();

        // Verify the successful update
        session = sessionFactory.openSession();
        DullEntity persistedEntity = (DullEntity)session.get(DullEntity.class, entityId);
        assertEquals("Winning updated name", persistedEntity.getShortName());
        assertEquals(1, persistedEntity.getVersion());
        session.flush();
        session.close();
    }

    private void attemptUpdateOriginalVersion(long entityId) {
        Session session;
        session = sessionFactory.openSession();
        DullEntity losingUpdateEntity = (DullEntity)session.get(DullEntity.class, entityId);
        losingUpdateEntity.setShortName("Losing updated name");
        losingUpdateEntity.setVersion(0);
        session.save(losingUpdateEntity);
        session.flush();
        session.close();
    }
}
