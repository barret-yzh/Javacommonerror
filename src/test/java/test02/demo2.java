package test02;

import java.util.stream.IntStream;

/**
 * @author: yezihang
 * @date: 2025-02-06 16:42
 **/
public class demo2 {

    public int wrong() {
        Data1.reset();
        //多线程循环一定次数调用Data类不同实例的wrong方法
        IntStream.rangeClosed(1, 1000000).parallel().forEach(i -> new Data1().wrong());
        return Data1.getCounter();
    }

    public int right() {
        IntStream.rangeClosed(1, 1000000).parallel().forEach(i -> new Data2().right());
        return Data2.getCounter();
    }

    public static void main(String[] args) {
        demo2 demo2 = new demo2();
        System.out.println(demo2.wrong());
        System.out.println(demo2.right());

    }
}
