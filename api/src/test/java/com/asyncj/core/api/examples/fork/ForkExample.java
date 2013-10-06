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
package com.asyncj.core.api.examples.fork;

import com.asyncj.core.api.ActorManager;
import org.junit.Test;

/**
 * User: APOPOV
 * Date: 05.10.13
 */
public class ForkExample {

    @Test
    public void testPingPong() {

        ActorManager actorManager = new ActorManager();

        int n = 10000000;
        Actor1 actor1 = actorManager.createActor(new Actor1Impl(n));

        actor1.startPings(actorManager,  n);

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public interface Actor1 {
        void startPings(ActorManager actorManager, int n);
        void pong(Actor2 actor2, String s);
    }

    public interface Actor2 {
        void ping(Actor1 actor1, int count);
    }

}
