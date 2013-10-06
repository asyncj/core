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
package com.asyncj.core.api.examples.plainjava;

import org.junit.Test;

/**
 * This example uses plain Java calls between actors and it will produce a stack overflow exception.
 * Such as actors calls each other recursively in one execution thread.
 * User: APOPOV
 * Date: 05.10.13
 */
public class PlainJavaExample {

    @Test
    public void testPingPong() {

        Actor1 actor1 = new Actor1Impl(10000000);

        Actor2[] actor2Array = new Actor2[Runtime.getRuntime().availableProcessors()];

        for (int i = 0; i < actor2Array.length; i++) {
            actor2Array[i] = new Actor2Impl();
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

    public interface Actor1 {
        void startPings(Actor2 actor2);
        void pong(Actor2 actor2, String s);
    }

    public interface Actor2 {
        void ping(Actor1 actor1, int count);
    }

}
