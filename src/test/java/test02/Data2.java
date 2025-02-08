package test02;

import lombok.Getter;

class Data2 {
    @Getter
    private static int counter = 0;
    private static Object locker = new Object();

    public void right() {
        synchronized (locker) {
            counter++;
        }
    }
}