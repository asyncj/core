import org.junit.Test;

/**
 * User: APOPOV
 * Date: 05.10.13
 */
public class PingPongExample {

    @Test
    public void testPingPong() {

        ActorManager actorManager = new ActorManager();

        Actor1 actor1 = actorManager.createActor(new Actor1Impl(10000000));
        Actor2[] actor2Array = new Actor2[Runtime.getRuntime().availableProcessors()*10000];

        for (int i = 0; i < actor2Array.length; i++) {
            actor2Array[i] = actorManager.createActor(new Actor2Impl());
        }

        for (int i = 0; i < actor2Array.length; i++) {
            actor1.startPings(actor2Array[i]);
        }

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    interface Actor1 {
        void startPings(Actor2 actor2);
        void pong(Actor2 actor2, String s);
    }

    interface Actor2 {
        void ping(Actor1 actor1, int count);
    }

}
