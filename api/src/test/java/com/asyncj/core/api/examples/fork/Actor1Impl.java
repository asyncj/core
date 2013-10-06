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

class Actor1Impl implements ForkExample.Actor1 {

    private final int workLoad;
    int n;
    int count = 0;
    final long startTime;

    public Actor1Impl(int n) {
        this.n = n;
        workLoad = this.n / Runtime.getRuntime().availableProcessors();

        startTime = System.currentTimeMillis();
    }

    public void pong(ForkExample.Actor2 actor2, String s) {
        count++;

        if (count < n - Runtime.getRuntime().availableProcessors()) {
            if (count % 100000 == 0)
                System.out.println(count + " pings from - " + s);
            actor2.ping(this, count);
        }
        else {
            long endTime = System.currentTimeMillis();
            System.out.println("super mega total count = " + count + ". In " + (endTime - startTime)/1000 + " seconds");
        }
    }

    @Override
    public void startPings(ActorManager actorManager, int m) {
        if (m < workLoad) {
            ForkExample.Actor2 actor2 = actorManager.createActor(new Actor2Impl());
            actor2.ping(this, m);
        }
        else {
            int m1 = m / 2;
            startPings(actorManager, m1);
            startPings(actorManager, m - m1);
        }
    }

}
