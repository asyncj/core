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
package com.asyncj.core.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: APOPOV
 * Date: 05.10.13
 */
public class ActorManager {


    private BlockingQueue<ActorMessage> queue = new LinkedBlockingQueue<ActorMessage>();

    private Map<Object, Proxy> actorToProxy = (new LinkedHashMap<Object, Proxy>());

    public ActorManager() {

        int availableProcessors = Runtime.getRuntime().availableProcessors() * 20;

        ActorManagerThread actorManagerThread = new ActorManagerThread(queue);
        for (int i = 0; i < availableProcessors; i++) {
            actorManagerThread.addNewActorThread();
        }

        actorManagerThread.start();
    }


    @SuppressWarnings("unchecked")
    public synchronized  <T> T createActor(Object obj) {
        MethodHandler h = new MethodHandler(obj);
        T t = (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), h);
        actorToProxy.put(obj, (Proxy) t);
        return t;
    }

    public synchronized  <T> List<T> createActors(Object obj, int count) {
        List<T> ts = new ArrayList<T>();
        for (int i = 0; i < count; i++) {
            ts.add(this.<T>createActor(obj));
        }
        return ts;
    }

    @SuppressWarnings("unchecked")
    public <T> T getReference(T actor1) {
        return (T) actorToProxy.get(actor1);
    }

    public void unblockResult(Object actor1) {
        Proxy proxy = (Proxy) getReference(actor1);
        ((MethodHandler) Proxy.getInvocationHandler(proxy)).unblockResult();
    }

    class MethodHandler implements InvocationHandler {

        private Object obj;

        private final Object lock = new Object();

        public MethodHandler(Object obj) {
            this.obj = obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if ("hashCode".equals(method.getName()) ) {
                return obj.hashCode();
            }

            if ("equals".equals(method.getName()) ) {
                return obj.equals(args);
            }

            if ("toString".equals(method.getName()) ) {
                return obj.toString();
            }
            // a method invocation will send the message to a blocking queue
            int i=0;
            if (args != null) {
                for (Object arg : args) {
                    Proxy proxy1 = actorToProxy.get(arg);
                    if (proxy1 != null) {
                        args[i] = proxy1;
                    }
                    i++;
                }
            }

            Class<?> returnType = method.getReturnType();
            if ("void".equals(returnType.getName())) {
                queue.put(new ActorMessage(ActorMessage.METHOD_CALL, obj, method, args));
                return null;
            }
            else {

                //if the return class is an actor interface then return a new instance of the proxy
                //to the user and associate it with the current proxy/actor.
                //the proxy should send all messages inside with a type METHOD_RESULT
                // and when the actor thread will execute the
                // method which will return the result of the actual actor, then the actor thread will send
                // a message to the Actor Manager Thread. The AMT will start sending messages to the new actor...
                //when the actor will be instantiated later it should receive all messages
                //that has been sent to it before

                //block the current thread and wait for the value from a future object
                synchronized (lock) {
                    lock.wait();
                    return method.invoke(obj, args);
                }
            }
        }

        public void unblockResult() {
            synchronized (lock) {
                lock.notify();
            }
        }

    }
}
