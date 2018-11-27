package nl.robal.mandel.utility;

import java.util.Date;

public class Util {

    public static void debug(String d) {
        System.out.println("[" + Thread.currentThread().getName() + "-" + new Date() + "]:" + d);
    }
}
