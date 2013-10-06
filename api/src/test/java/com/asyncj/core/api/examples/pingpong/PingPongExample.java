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
package com.asyncj.core.api.examples.pingpong;

import com.asyncj.core.api.ActorManager;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

/**
 * User: APOPOV
 * Date: 05.10.13
 */
public class PingPongExample {

    @Test
    public void testPingPong() {

        ActorManager actorManager = new ActorManager();

        int pNumber = Runtime.getRuntime().availableProcessors();

        Actor1 actor1 = actorManager.createActor(new Actor1Impl(1000000, actorManager));
        List<Actor2> actor2Array = actorManager.createActors(new Actor2Impl(), pNumber);

        for (Actor2 actor2 : actor2Array) {
            actor1.startPings(actor2);
        }

        //this method call should block the current thread
        // and the Actor proxy should return the result when
        // it will be available after processing all messages passed to actor1...

        Integer count = actor1.getCount();

        // if the actor returns as a result another actor then the actor manager will return an
        // empty proxy of an actor

        System.out.println("final count = " + count);
    }

    public interface Actor1 {

        void startPings(Actor2 actor2);

        void pong(Actor2 actor2, String s);

        Integer getCount();
    }

    public interface Actor2 {

        void ping(Actor1 actor1, int count);
    }

}
