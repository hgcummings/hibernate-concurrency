import org.hibernate.event.def.DefaultFlushEntityEventListener;

public class DefaultFlushEntityEventListenerTest extends OptimisticConcurrencyTest {
    @Override
    protected Class getFlushEntityEventListenerType() {
        return DefaultFlushEntityEventListener.class;
    }
}