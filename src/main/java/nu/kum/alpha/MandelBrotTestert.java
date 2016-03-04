package nu.kum.alpha;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * This is just a reference mandelbrot implementation. Works standalone but not used in the live application.
 */
public class MandelBrotTestert {
    public static void main(String[] args) throws Exception {

        int width = 1920, height = 1080, max = 1000;
        double zoom = 400d;
        double xmove = width / 2;
        double ymove = height / 2;

        int dim = 400;
        int rowDim = 1;
        int columnDim = 2;

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
                if (iteration < max) image.setRGB(col- (dim * (columnDim)), row - (dim * (rowDim)), colors[iteration]);
                else image.setRGB(col- (dim * (columnDim)), row - (dim * (rowDim)), black);
            }
        }

        ImageIO.write(image, "png", new File("mandelbrot5.png"));
    }
}
