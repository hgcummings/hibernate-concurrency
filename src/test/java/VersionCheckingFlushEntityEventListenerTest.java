import org.hibernate.event.def.DefaultFlushEntityEventListener;

public class VersionCheckingFlushEntityEventListenerTest extends OptimisticConcurrencyTest {
    @Override
    protected Class getFlushEntityEventListenerType() {
        return VersionCheckingFlushEntityEventListener.class;
    }
}