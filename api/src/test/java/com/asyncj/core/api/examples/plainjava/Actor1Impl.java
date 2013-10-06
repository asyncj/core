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


class Actor1Impl implements PlainJavaExample.Actor1 {

    int n;
    int count = 0;
    final long startTime;


    public Actor1Impl(int i) {
        this.n = i;

        startTime = System.currentTimeMillis();
    }

    public void pong(PlainJavaExample.Actor2 actor2, String s) {
        count++;
        if (count < n) {
            if (count % 100000 == 0)
                System.out.println(count + " pings from - " + s);
            actor2.ping(this, count);
        }
        else {
            long endTime = System.currentTimeMillis();
            System.out.println("super mega total count = " + count + ". In " + (endTime - startTime)/1000 + " seconds");
        }
    }

    public void startPings(PlainJavaExample.Actor2 actor2) {
        count++;
        actor2.ping(this, count);
    }
}
