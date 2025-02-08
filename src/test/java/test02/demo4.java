package test02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: yezihang
 * @date: 2025-02-06 17:24
 **/
public class demo4 {

    private static final Logger log = LoggerFactory.getLogger(demo4.class.getName());
    private static Map<String, Item> items = new HashMap<>();

    private List<Item> createCart() {
        return IntStream.rangeClosed(1, 3)
                .mapToObj(i -> "item" + ThreadLocalRandom.current().nextInt(1, items.size() + 1))
                .map(name -> items.get(name)).collect(Collectors.toList());
    }

    private boolean createOrder(List<Item> order) {
        //存放所有获得的锁
        List<ReentrantLock> locks = new ArrayList<>();

        for (Item item : order) {
            try {
                //获得锁10秒超时
                if (item.lock.tryLock(10, TimeUnit.SECONDS)) {
                    locks.add(item.lock);
                } else {
                    locks.forEach(ReentrantLock::unlock);
                    return false;
                }
            } catch (InterruptedException e) {
            }
        }
        //锁全部拿到之后执行扣减库存业务逻辑
        try {
            order.forEach(item -> item.remaining--);
        } finally {
            locks.forEach(ReentrantLock::unlock);
        }
        return true;
    }

    public static void main(String[] args) {
        demo4 test = new demo4();
        Item item1 = new Item("apple");
        Item item2 = new Item("pear");
        Item item3 = new Item("banana");
        Item item4 = new Item("orange");
        Item item5 = new Item("grape");
        Item item6 = new Item("watermelon");
        Item item7 = new Item("strawberry");
        Item item8 = new Item("pineapple");
        Item item9 = new Item("mango");
        Item item10 = new Item("kiwi");
        items.put("item1", item1);
        items.put("item2", item2);
        items.put("item3", item3);
        items.put("item4", item4);
        items.put("item5", item5);
        items.put("item6", item6);
        items.put("item7", item7);
        items.put("item8", item8);
        items.put("item9", item9);
        items.put("item10", item10);
        long begin = System.currentTimeMillis();
        //并发进行100次下单操作，统计成功次数
        long success = IntStream.rangeClosed(1, 100).parallel()
                .mapToObj(i -> {
                    List<Item> cart = test.createCart();
//                    List<Item> cart = test.createCart().stream()
//                            .sorted(Comparator.comparing(Item::getName))
//                            .collect(Collectors.toList());
                    return test.createOrder(cart);
                })
                .filter(result -> result)
                .count();
        log.info("success:{} totalRemaining:{} took:{}ms items:{}",
                success,
                items.entrySet().stream().map(item -> item.getValue().remaining).reduce(0, Integer::sum),
                System.currentTimeMillis() - begin, items);
    }
}
