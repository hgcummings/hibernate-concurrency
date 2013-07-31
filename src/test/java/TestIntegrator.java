import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.FlushEntityEventListener;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TestIntegrator implements Integrator {
    private Class listenerType;

    public TestIntegrator(Class listenerType) {
        this.listenerType = listenerType;
    }

    @Override
    public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );

        EventListenerGroup<FlushEntityEventListener> listenerGroup =
                eventListenerRegistry.getEventListenerGroup(EventType.FLUSH_ENTITY);

        listenerGroup.clear();
        try {
            listenerGroup.appendListener((FlushEntityEventListener) listenerType.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void integrate(MetadataImplementor metadataImplementor, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        // Not used in Hibernate 4
        throw new NotImplementedException();
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
    }
}
