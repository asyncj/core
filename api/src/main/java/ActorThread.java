import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * User: APOPOV
 * Date: 05.10.13
 */
public class ActorThread extends Thread {
    private BlockingQueue<ActorMessage> actorMessages;
    private BlockingQueue<ActorMessage> parentQueue;
    private Object actor;

    public ActorThread(BlockingQueue<ActorMessage> actorMessages, BlockingQueue<ActorMessage> parentQueue) {
        this.actorMessages = actorMessages;
        this.parentQueue = parentQueue;
    }

    @Override
    public void run() {
        boolean process = true;
        while (process) {
            try {
                ActorMessage msg = actorMessages.poll(5, TimeUnit.MILLISECONDS);
                if (msg == null) {
                    if (actor != null) {
                        //System.out.println("Ready...");
                        parentQueue.put(new ActorMessage(ActorMessage.WORKER_IS_READY, actor, null, null));
                        actor = null;
                    }
                    msg = actorMessages.take();
                }

                if (ActorMessage.METHOD_CALL.equals(msg.getType())) {
                    Method m = msg.getMethod();
                    actor = msg.getActor();
                    Object invoke = m.invoke(actor, msg.getArgs());
                }
                else
                if (ActorMessage.POISON_PILL.equals(msg.getType())) {
                    System.out.println("Poison pill received... Shutting down the actor's thread");
                    process = false;
                }



            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
