package test02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

/**
 * @author: yezihang
 * @date: 2025-02-06 17:03
 **/
public class demo3 {

    private static final Logger log = LoggerFactory.getLogger(demo3.class.getName());
    private List data = new ArrayList<>();

    //不涉及共享资源的慢方法
    private void slow() {
        try {
            sleep(10);
        } catch (InterruptedException e) {
        }
    }

    //错误的加锁方法
    public int wrong() {
        long begin = System.currentTimeMillis();
        IntStream.rangeClosed(1, 1000).parallel().forEach(i -> {
            //加锁粒度太粗了
            synchronized (this) {
                slow();
                data.add(i);
            }
        });
        log.info("took:{}", System.currentTimeMillis() - begin);
        return data.size();
    }

    //正确的加锁方法
    public int right() {
        long begin = System.currentTimeMillis();
        IntStream.rangeClosed(1, 1000).parallel().forEach(i -> {
            slow();
            //只对List加锁
            synchronized (data) {
                data.add(i);
            }
        });
        log.info("took:{}", System.currentTimeMillis() - begin);
        return data.size();
    }

    public static void main(String[] args) {
        demo3 demo = new demo3();
        demo.wrong();
        demo.right();
    }
}
