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

class Actor1Impl implements PingPongExample.Actor1 {

    int n;
    private ActorManager actorManager;
    int count = 0;
    final long startTime;


    public Actor1Impl(int i, ActorManager actorManager) {
        this.n = i;
        this.actorManager = actorManager;
        startTime = System.currentTimeMillis();
    }

    public void pong(PingPongExample.Actor2 actor2, String s) {
        count++;
        if (count % 100000 == 0)
            System.out.println(count + " pings from - " + s);

        if (count < n) {
            actor2.ping(this, count);
        }
        else {
            long endTime = System.currentTimeMillis();
            System.out.println("super mega total count = " + count + ". In " + (endTime - startTime)/1000 + " seconds");

            // how to send a final message?
            actorManager.unblockResult(this);
        }
    }

    @Override
    public Integer getCount() {
        return count;
    }

    public void startPings(PingPongExample.Actor2 actor2) {
        count++;
        actor2.ping(this, count);
    }
}
