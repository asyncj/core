import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: APOPOV
 * Date: 05.10.13
 */
public class ActorManager {


    private BlockingQueue<ActorMessage> queue = new LinkedBlockingQueue<ActorMessage>();

    private Map<Object, Proxy> actorToProxy = new LinkedHashMap<Object, Proxy>();
    public ActorManager() {
        // 1. Action manager will execute the code and return
        LinkedList<BlockingQueue<ActorMessage>> queuePool = new LinkedList<BlockingQueue<ActorMessage>>();


        int availableProcessors = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < availableProcessors; i++) {
            BlockingQueue<ActorMessage> actorMessages = new LinkedBlockingQueue<ActorMessage>();
            new ActorThread(actorMessages, queue).start();
            queuePool.add(actorMessages);
        }

        new ActorManagerThread(queue, queuePool).start();
        // 2.
    }

    public <T> T createActor(Object obj) {
        MethodHandler h = new MethodHandler(obj);
        T t = (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), h);
        actorToProxy.put(obj, (Proxy) t);
        return t;
    }

    class MethodHandler implements InvocationHandler {

        private Object obj;

        public MethodHandler(Object obj) {
            this.obj = obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if ("hashCode".equals(method.getName()) ) {
                return obj.hashCode();
            }

            if ("equals".equals(method.getName()) ) {
                return obj.equals(args);
            }

            if ("toString".equals(method.getName()) ) {
                return obj.toString();
            }
            // a method invocation will send the message to a blocking queue
            int i=0;
            for (Object arg : args) {
                Proxy proxy1 = actorToProxy.get(arg);
                if (proxy1 != null) {
                    args[i] = proxy1;
                }
                i++;
            }
            queue.put(new ActorMessage(ActorMessage.METHOD_CALL, obj, method, args));

            return null;
        }
    }
}
