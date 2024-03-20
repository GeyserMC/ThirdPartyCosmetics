/*
 * Copyright (c) 2024 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/ThirdPartyCosmetics
 */

package org.geysermc.extension.thirdpartycosmetics;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static byte[] readAllBytes(String name) {
        byte[] bytes = new byte[0];
        try (FileSystem fileSystem = FileSystems.newFileSystem(new File(ThirdPartyCosmetics.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath(), Collections.emptyMap())) {
            try (InputStream input = Files.newInputStream(fileSystem.getPath(name))) {
                bytes = new byte[input.available()];

                input.read(bytes);

            }
        } catch (IOException | URISyntaxException ignored) { }

        return bytes;
    }

    public static <T> T getOrDefault(CompletableFuture<T> future, T defaultValue, int timeoutInSeconds) {
        try {
            return future.get(timeoutInSeconds, TimeUnit.SECONDS);
        } catch (Exception ignored) {}
        return defaultValue;
    }

    /**
     * Convert a byte[] to a BufferedImage
     *
     * @param imageData The byte[] to convert
     * @param imageWidth The width of the target image
     * @param imageHeight The height of the target image
     * @return The converted BufferedImage
     */
    public static BufferedImage imageDataToBufferedImage(byte[] imageData, int imageWidth, int imageHeight) {
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        int index = 0;
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                image.setRGB(x, y, getRGBA(index, imageData));
                index += 4;
            }
        }

        return image;
    }

    /**
     * Get the RGBA int for a given index in some image data
     *
     * @param index Index to get
     * @param data Image data to find in
     * @return An int representing RGBA
     */
    public static int getRGBA(int index, byte[] data) {
        return (data[index] & 0xFF) << 16 | (data[index + 1] & 0xFF) << 8 |
            data[index + 2] & 0xFF | (data[index + 3] & 0xFF) << 24;
    }

    /**
     * Convert a BufferedImage to a byte[]
     *
     * @param image The BufferedImage to convert
     * @return The converted byte[]
     */
    public static byte[] bufferedImageToImageData(BufferedImage image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(image.getWidth() * 4 + image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgba = image.getRGB(x, y);
                outputStream.write((rgba >> 16) & 0xFF);
                outputStream.write((rgba >> 8) & 0xFF);
                outputStream.write(rgba & 0xFF);
                outputStream.write((rgba >> 24) & 0xFF);
            }
        }
        return outputStream.toByteArray();
    }
}
