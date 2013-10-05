/**
 Copyright 2013 Aliaksei Papou

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
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
