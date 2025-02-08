package test02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: yezihang
 * @date: 2025-02-06 16:03
 **/
public class demo1 {

    private static final Logger log = LoggerFactory.getLogger(demo1.class.getName());

    volatile int a = 1;
    volatile int b = 1;

    public synchronized void add() {
        log.info("add start");
        for (int i = 0; i < 1000000; i++) {
            a++;
            b++;
        }
        log.info("add done");
    }

    public synchronized void compare() {
        log.info("compare start");
        for (int i = 0; i < 1000000; i++) {
            //a始终等于b吗？
            if (a < b) {
                log.info("a:{},b:{},{}", a, b, a > b);
                //最后的a>b应该始终是false吗？
            }
        }
        log.info("compare done");
    }

    public static void main(String[] args) {
        demo1 test = new demo1();
        new Thread(test::add).start();
        new Thread(test::compare).start();
    }
}
