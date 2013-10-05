import java.lang.reflect.Method;

/**
 * User: APOPOV
 * Date: 05.10.13
 */
public class ActorMessage {
    public static final String METHOD_CALL = "methodCall";
    public static final String WORKER_IS_READY = "workerIsReady";
    public static final String PROCESS_DELAYED_MESSAGES = "processDelayedMessages";
    public static final String POISON_PILL = "poisonPill";
    private final String type;
    private final Object actor;
    private final Method method;
    private final Object[] args;

    public ActorMessage(String type, Object actor, Method method, Object[] args) {
        this.type = type;
        this.actor = actor;
        this.method = method;
        this.args = args;
    }

    public String getType() {
        return type;
    }

    public Object getActor() {
        return actor;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
