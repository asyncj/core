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

class Actor2Impl implements PlainJavaExample.Actor2 {

    public void ping(PlainJavaExample.Actor1 actor1, int count) {
        if (count % 100000 == 0)
            System.out.println(count + " pongs");
        actor1.pong(this, this.toString());
    }
}
