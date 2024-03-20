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

package org.geysermc.extension.thirdpartycosmetics.ears;

import org.geysermc.extension.thirdpartycosmetics.Utils;
import org.geysermc.geyser.api.skin.Skin;
import org.geysermc.geyser.api.skin.SkinGeometry;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EarsFetcher {
    private static final String EARS_GEOMETRY;
    private static final String EARS_GEOMETRY_SLIM;

    static {
        /* Load in the normal ears geometry */
        EARS_GEOMETRY = new String(Utils.readAllBytes("geometry.humanoid.ears.json"), StandardCharsets.UTF_8);

        /* Load in the slim ears geometry */
        EARS_GEOMETRY_SLIM = new String(Utils.readAllBytes("geometry.humanoid.earsSlim.json"), StandardCharsets.UTF_8);
    }

    /**
     * Try and find an ear texture for a Java player
     *
     * @param officialSkin The current players skin
     * @param playerId The players UUID
     * @param username The players username
     * @return The updated skin with ears
     */
    public static CompletableFuture<Skin> request(Skin officialSkin, UUID playerId, String username) {
        for (EarsProvider provider : EarsProvider.VALUES) {
            Skin skin1 = Utils.getOrDefault(
                requestEars(provider.getUrlFor(playerId, username), officialSkin),
                officialSkin, 4
            );
            if (skin1 != officialSkin) {
                return CompletableFuture.completedFuture(skin1);
            }
        }

        return CompletableFuture.completedFuture(officialSkin);
    }

    /**
     * Generate basic geometry with ears
     *
     * @param isSlim Should it be the alex model
     * @return The generated geometry for the ears model
     */
    public static SkinGeometry geometry(boolean isSlim) {
        return new SkinGeometry("{\"geometry\" :{\"default\" :\"geometry.humanoid.ears" + (isSlim ? "Slim" : "") + "\"}}", (isSlim ? EARS_GEOMETRY_SLIM : EARS_GEOMETRY));
    }

    private static CompletableFuture<Skin> requestEars(String earsUrl, Skin skin) {
        if (earsUrl == null || earsUrl.isEmpty()) return CompletableFuture.completedFuture(skin);

        Skin ears = supplyEars(skin, earsUrl);
        return CompletableFuture.completedFuture(ears);
    }

    /**
     * Get the ears texture and place it on the skin from the given URL
     *
     * @param existingSkin The players current skin
     * @param earsUrl The URL to get the ears texture from
     * @return The updated skin with ears
     */
    private static Skin supplyEars(Skin existingSkin, String earsUrl) {
        try {
            // Get the ears texture
            BufferedImage ears = ImageIO.read(new URL(earsUrl));
            if (ears == null) throw new NullPointerException();

            // Convert the skin data to a BufferedImage
            int height = (existingSkin.skinData().length / 4 / 64);
            BufferedImage skinImage = Utils.imageDataToBufferedImage(existingSkin.skinData(), 64, height);

            // Create a new image with the ears texture over it
            BufferedImage newSkin = new BufferedImage(skinImage.getWidth(), skinImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) newSkin.getGraphics();
            g.drawImage(skinImage, 0, 0, null);
            g.drawImage(ears, 24, 0, null);

            // Turn the buffered image back into an array of bytes
            byte[] data = Utils.bufferedImageToImageData(newSkin);
            skinImage.flush();

            // Create a new skin object with the new information
            return new Skin(
                earsUrl,
                data
            );
        } catch (Exception ignored) { } // just ignore I guess

        return existingSkin;
    }
}
