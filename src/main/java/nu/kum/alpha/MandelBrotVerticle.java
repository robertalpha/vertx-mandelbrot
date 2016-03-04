package nu.kum.alpha;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import nu.kum.alpha.utility.Util;
import org.apache.xmlbeans.impl.util.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by robert on 3/3/16.
 */
public class MandelBrotVerticle extends AbstractVerticle {

    @Override
    public void start(Future startfuture) {
        vertx.eventBus().consumer("mandelbrot.imagerequest", handler -> {
            try {
                handler.reply(getPart((JsonObject) handler.body()));
            } catch (Exception e) {
                handler.reply(new JsonObject().put("status", "error: " + e.getMessage()));
            }
        });
        Util.debug("MandelBrotVerticle started");
        startfuture.complete();
    }


    private static JsonObject getPart(JsonObject options) throws Exception {

        long startTime = System.nanoTime();


        int width = 1920, height = 1080, max = 80000;
        double zoom = 400d;
        double xmove = width / 2;
        double ymove = height / 2;
        int dim = 400;
        int rowDim = 1;
        int columnDim = 2;
        width = options.getInteger("width");
        height = options.getInteger("height");
        zoom = options.getDouble("zoom");
        xmove = options.getDouble("xmove");
        ymove = options.getDouble("ymove");
        dim = options.getInteger("dim");
        rowDim = options.getInteger("rowDim");
        columnDim = options.getInteger("columnDim");


        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i < max; i++) {
            colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
        }

        BufferedImage image = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_RGB);
        for (int row = dim * (rowDim); row < dim * (rowDim + 1); row++) {
            for (int col = dim * (columnDim); col < dim * (columnDim + 1); col++) {
                double c_re = (col - xmove) / zoom;
                double c_im = (row - ymove) / zoom;
                double x = 0, y = 0;
                int iteration = 0;
                while (x * x + y * y < 4 && iteration < max) {
                    double x_new = x * x - y * y + c_re;
                    y = 2 * x * y + c_im;
                    x = x_new;
                    iteration++;
                }
                if (iteration < max) image.setRGB(col - (dim * (columnDim)), row - (dim * (rowDim)), colors[iteration]);
                else image.setRGB(col - (dim * (columnDim)), row - (dim * (rowDim)), black);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        String encodedImage = new String(Base64.encode(baos.toByteArray()), StandardCharsets.UTF_8);

        long durationInMillis = (System.nanoTime() - startTime) / 1000000;
        String statusinfo = "took " + durationInMillis + " ms to compute on " + Thread.currentThread().getName();
        Util.debug(statusinfo);
//        byte[] encoded = Base64.encode(bytes);
        return new JsonObject().put("image64", encodedImage).put("status", statusinfo);

    }
}
