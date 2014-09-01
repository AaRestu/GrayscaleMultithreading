/*
 * Copyright 2014 aarestu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grayscalemultithreading;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author aarestu
 */
public class GrayScale {

    public final static int GRAY_AVERAGING = 1;
    public final static int GRAY_LUMINOSITY = 2;
    public final static int GRAY_DESATURATION = 3;
    public final static int GRAY_MAX = 4;
    public final static int GRAY_MIN = 5;
    public final static int GRAY_RED = 6;
    public final static int GRAY_GREEN = 7;
    public final static int GRAY_BLUE = 8;

    private CountDownLatch latch;
    private ExecutorService executor;
    private BufferedImage src;
    private BufferedImage grayscale;
    private int grayType;

    private void doConvert(int start, int end) {
        int x;
        int y;
        int gray;

        int cols = grayscale.getWidth();

        for (int i = start; i < end; i++) {
            //hitung kordinat x, y
            x = i % cols;
            y = i / cols;

            //if (!cekOffsideKordinat(x, y)) {
            gray = calculate(new Color(src.getRGB(x, y)), grayType);
            grayscale.setRGB(x, y, gray);
            //}
        }

        latch.countDown();
    }

    /*private boolean cekOffsideKordinat(int x, int y) {
     if (x >= src.getWidth() || x < 0 || y >= src.getHeight() || y < 0) {
     System.out.println("Terdeteksi offside kordinat ( x: " + x + "; y: " + y + " )");
     return true;
     }

     return false;
     }*/
    
    private static int calculate(Color color, int grayType) {

        int gray;

        switch (grayType) {
            case GRAY_AVERAGING:
                //( R + G + B ) / 3
                gray = (int) (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                break;

            case GRAY_DESATURATION:
                // (max(r,g,b) + min (r, g, b)) / 2
                gray = (int) ((Math.max(Math.max(color.getRed(), color.getGreen()), color.getBlue())
                        + Math.min(Math.min(color.getRed(), color.getGreen()), color.getBlue()))
                        / 2);
                break;

            case GRAY_MAX:
                // max(r,g,b)
                gray = (int) Math.max(Math.max(color.getRed(), color.getGreen()), color.getBlue());
                break;

            case GRAY_MIN:
                // min(r,g,b)
                gray = (int) Math.min(Math.min(color.getRed(), color.getGreen()), color.getBlue());
                break;

            case GRAY_RED:
                gray = color.getRed();
                break;

            case GRAY_GREEN:
                gray = color.getGreen();
                break;

            case GRAY_BLUE:
                gray = color.getBlue();
                break;

            default:
                //case GRAY_LUMINOSITY:
                //R * 0.2126 + G * 0.7152 + B * 0.0722
                gray = (int) (color.getRed() * 0.2126 + color.getGreen() * 0.7152 + color.getBlue() * 0.0722);
                break;
        }

        return new Color(gray, gray, gray).getRGB();
    }

    public Image convert(Image src, int grayType, int numWorker) throws Exception {
        this.src = (BufferedImage) src;
        this.grayType = grayType;

        int jmlPixel = (this.src.getHeight() * this.src.getWidth()) - 1;
        int handlePerWorker = jmlPixel / numWorker;
        int startInx = 0;

        //validasi jml pixel < numWorker
        if (jmlPixel < numWorker) {
            throw new IOException("Jumlah Pixel < Jumlah Worker");
        }

        latch = new CountDownLatch(numWorker);
        executor = Executors.newFixedThreadPool(numWorker);

        this.grayscale = new BufferedImage(this.src.getWidth(), this.src.getHeight(), this.src.getType());

        for (int i = 0; i < numWorker; i++) {
            //hitung tanggung jawab setiap worker
            final int startInxF = startInx;
            final int endInxF = (i != numWorker - 1)
                    ? startInx + handlePerWorker - 1 : jmlPixel;

            startInx += handlePerWorker;

            //sumbit pool thread
            executor.submit(new Thread(new Runnable() {

                @Override
                public void run() {
                    doConvert(startInxF, endInxF);
                }

            }));
        }

        //tunggu CountDownLatch == 0
        latch.await();

        //hentikan pool thread
        executor.shutdown();

        return grayscale;
    }
}
