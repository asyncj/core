/**
* User: APOPOV
* Date: 05.10.13
*/
class Actor1Impl implements PingPongExample.Actor1 {

    int n;
    int count = 0;

    public Actor1Impl(int i) {
        this.n = i;
    }

    public void pong(PingPongExample.Actor2 actor2, String s) {
        count++;
        if (count <= n) {
            if (count % 100000 == 0)
                System.out.println(count + " pings from - " + s);
            actor2.ping(this, count);
        }
    }

    public void startPings(PingPongExample.Actor2 actor2) {
        count++;
        actor2.ping(this, count);
    }
}
