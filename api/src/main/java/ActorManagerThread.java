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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * User: APOPOV
 * Date: 05.10.13
 */
public class ActorManagerThread extends Thread {
    private BlockingQueue<ActorMessage> queue;
    private Map<Object, BlockingQueue<ActorMessage>> busyQueueMap = new LinkedHashMap<Object, BlockingQueue<ActorMessage>>();
    private Queue<BlockingQueue<ActorMessage>> queuePool;

    private Queue<ActorMessage> msgCache = new LinkedList<ActorMessage>();
    private boolean offered;


    public ActorManagerThread(BlockingQueue<ActorMessage> queue, Queue<BlockingQueue<ActorMessage>> queuePool) {
        this.queue = queue;
        this.queuePool = queuePool;
    }

    @Override
    public void run() {
        boolean process = true;
        while (process) {
            try {
                ActorMessage message = queue.take();

                if (ActorMessage.METHOD_CALL.equals(message.getType())) {
                    //1. find a queue by the actor
                    Object actor = message.getActor();
                    BlockingQueue<ActorMessage> actorQueue = busyQueueMap.get(actor);
                    if (actorQueue == null) {
                        actorQueue = queuePool.poll();

                        if (actorQueue == null && !offered) {
                            // then the pool is empty.
                            // we do not need to block the thread but rather save the message in the local cache.
                            msgCache.add(message);
                            //System.out.println("Putting messages to a cache");


                            // then put a new ActorMessage with type to process the msgCache
                            offered = queue.offer(new ActorMessage(ActorMessage.PROCESS_DELAYED_MESSAGES, null, null, null));
                            if (!offered) {
                                System.out.println("Can't offer to process delayed messages. The queue is full");
                            }
                        }
                        else {
                            busyQueueMap.put(actor, actorQueue);
                        }
                    }
                    if (actorQueue != null) {
                        actorQueue.put(message);
                    }
                    //2. and put the message into that queue
                }
                else
                if (ActorMessage.WORKER_IS_READY.equals(message.getType())) {
                    Object actor = message.getActor();
                    //System.out.println("Worker is ready");

                    BlockingQueue<ActorMessage> removed = busyQueueMap.remove(actor);
                    queuePool.add(removed);

                    //the last message is received for actor system. Let's shutdown it.
                    if (busyQueueMap.isEmpty()) {
                        ActorMessage poisonPill = new ActorMessage(ActorMessage.POISON_PILL, null, null, null);
                        for (BlockingQueue<ActorMessage> blockingQueue : queuePool) {
                            blockingQueue.add(poisonPill);
                        }
                        queue.offer(poisonPill);
                    }
                }
                else
                if (ActorMessage.PROCESS_DELAYED_MESSAGES.equals(message.getType())) {
                    ActorMessage peek = msgCache.peek();
                    while (peek != null) {
                        //try to offer to the main queue
                        boolean offer = queue.offer(peek);
                        if (!offer) {
                            System.out.println("Can't offer to process delayed message. The queue is full");
                            // the problem should be escalated
                        }
                        else {
                            msgCache.remove();
                            peek = msgCache.peek();
                        }
                    }
                    offered = false;
                }
                else
                if (ActorMessage.POISON_PILL.equals(message.getType())) {
                    System.out.println("Poison pill received... Shutting down the actor manager's thread");
                    process = false;
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
