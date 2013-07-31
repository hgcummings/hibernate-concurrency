import org.hibernate.integrator.spi.Integrator;
import org.hibernate.integrator.spi.IntegratorService;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.Collections;

public class VersionCheckingFlushEntityEventListenerTest extends OptimisticConcurrencyTest {
    @Override
    protected Class getCustomFlushEntityEventListenerType() {
        return VersionCheckingFlushEntityEventListener.class;
    }
}