package nu.kum.alpha.utility;

import java.util.Date;

/**
 * Robert on 13-6-2015.
 */
public class Util {

    public static void debug(String d) {
        System.out.println("[" + Thread.currentThread().getName() + "-" + new Date() + "]:" + d);
    }
}
