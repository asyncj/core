/**
* User: APOPOV
* Date: 05.10.13
*/
class Actor2Impl implements PingPongExample.Actor2 {

    public void ping(PingPongExample.Actor1 actor1, int count) {
        if (count % 100000 == 0)
            System.out.println(count + " pongs");
        actor1.pong(this, this.toString());
    }
}
