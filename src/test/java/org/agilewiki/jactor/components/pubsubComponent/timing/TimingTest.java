package org.agilewiki.jactor.components.pubsubComponent.timing;

import junit.framework.TestCase;
import org.agilewiki.jactor.Actor;
import org.agilewiki.jactor.JAFuture;
import org.agilewiki.jactor.JAMailboxFactory;
import org.agilewiki.jactor.MailboxFactory;
import org.agilewiki.jactor.components.Include;
import org.agilewiki.jactor.components.JCActor;
import org.agilewiki.jactor.parallel.JAParallel;
import org.agilewiki.jactor.pubsub.Subscribe;

public class TimingTest extends TestCase {
    public void test() {

        int c = 10;
        int b = 1;
        int s = 1000;
        int p = 1;
        int t = 4;

        //int c = 1000000;
        //int b = 1;
        //int s = 10;
        //int p = 4;
        //int t = 4;
        //4 parallel runs of 1000000 bursts of 1 requests sent to 10 subscribers
        //publications per sec = 18867924
        //response time 212 nanoseconds

        //int c = 10000;
        //int b = 1;
        //int s = 1000;
        //int p = 4;
        //int t = 4;
        //4 parallel runs of 10000 bursts of 1 requests sent to 1000 subscribers
        //publications per sec = 23852116
        //response time 168 nanoseconds

        //int c = 1000;
        //int b = 1000;
        //int s = 10;
        //int p = 4;
        //int t = 4;
        //4 parallel runs of 1000 bursts of 1000 requests sent to 10 subscribers
        //publications per sec = 31595576

        MailboxFactory mailboxFactory = JAMailboxFactory.newMailboxFactory(t);
        try {
            JAFuture future = new JAFuture();
            Actor[] drivers = new Actor[p];
            int i = 0;
            while (i < p) {
                Actor driver = new JCActor(mailboxFactory.createAsyncMailbox());
                driver.setInitialBufferCapacity(b + 10);
                future.send(driver, new Include(Driver.class));
                drivers[i] = driver;
                int j = 0;
                while (j < s) {
                    Actor subscriber = new NullSubscriber(mailboxFactory.createMailbox());
                    subscriber.setInitialBufferCapacity(b + 10);
                    Subscribe subscribe = new Subscribe(subscriber);
                    future.send(driver, subscribe);
                    j += 1;
                }
                i += 1;
            }
            JAParallel parallel = new JAParallel(mailboxFactory.createMailbox(), drivers);
            Timing timing = new Timing(c, b);
            future.send(parallel, timing);
            future.send(parallel, timing);
            long t0 = System.currentTimeMillis();
            future.send(parallel, timing);
            long t1 = System.currentTimeMillis();
            System.out.println("" + p + " parallel runs of " + c + " bursts of " + b + " requests sent to " + s + " subscribers");
            if (t1 != t0)
                System.out.println("publications per sec = " + (1000L * c * b * s * p / (t1 - t0)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mailboxFactory.close();
        }
    }
}
