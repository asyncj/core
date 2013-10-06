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
package com.asyncj.core.api.examples.forkjoin;

import com.asyncj.core.api.ActorManager;

class Actor1Impl implements ForkJoinExample.Actor1 {

    private final int workLoad;
    private int n;
    private int count = 0;

    private ForkJoinExample.Actor1 parentActor1;
    private int incTimes;

    public Actor1Impl(int n, ForkJoinExample.Actor1 parentActor1, int workLoad) {
        this.n = n;
        this.parentActor1 = parentActor1;
        this.workLoad = workLoad;
    }

    public void pong(ForkJoinExample.Actor2 actor2, String s) {
        count++;

        if (count < n) {
            if (count % 100000 == 0)
                System.out.println(count + " pings from - " + s);
            actor2.ping(this, count);
        }
        else {
            System.out.println("final count = " + count);
            if (parentActor1 != null) {
                parentActor1.incCount(count);
            }
        }
    }

    @Override
    public void incCount(int count) {
        incTimes++;
        this.count += count;
        System.out.println("sub count = " + this.count);
        if (incTimes >= 5 && parentActor1 != null) {
            parentActor1.incCount(this.count);
        }
    }

    @Override
    public void startPings(ActorManager actorManager, int m) {
        if (m < workLoad) {
            ForkJoinExample.Actor2 actor2 = actorManager.createActor(new Actor2Impl());
            actor2.ping(this, m);
        }
        else {
            int m1 = m / 5;
            int m2 = m - m1 * 4;
            ForkJoinExample.Actor1 thisActor = actorManager.getReference(this);

            ForkJoinExample.Actor1 actorA = actorManager.createActor(new Actor1Impl(m1, thisActor, workLoad));
            ForkJoinExample.Actor1 actorB = actorManager.createActor(new Actor1Impl(m1, thisActor, workLoad));
            ForkJoinExample.Actor1 actorC = actorManager.createActor(new Actor1Impl(m1, thisActor, workLoad));
            ForkJoinExample.Actor1 actorD = actorManager.createActor(new Actor1Impl(m1, thisActor, workLoad));
            ForkJoinExample.Actor1 actorE = actorManager.createActor(new Actor1Impl(m2, thisActor, workLoad));
            actorA.startPings(actorManager, m1);
            actorB.startPings(actorManager, m1);
            actorC.startPings(actorManager, m1);
            actorD.startPings(actorManager, m1);
            actorE.startPings(actorManager, m2);
        }
    }

}
