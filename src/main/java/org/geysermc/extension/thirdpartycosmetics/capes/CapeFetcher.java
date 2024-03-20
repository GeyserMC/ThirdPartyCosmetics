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

package org.geysermc.extension.thirdpartycosmetics.capes;

import org.geysermc.extension.thirdpartycosmetics.Utils;
import org.geysermc.geyser.api.skin.Cape;

import javax.imageio.ImageIO;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CapeFetcher {
    /**
     * Try and find a cape texture for a Java player
     *
     * @param currentCape The current players cape
     * @param playerId The players UUID
     * @param username The players username
     * @return The updated cape
     */
    public static CompletableFuture<Cape> request(Cape currentCape, UUID playerId, String username) {
        for (CapeProvider provider : CapeProvider.VALUES) {
            Cape cape = Utils.getOrDefault(
                requestCape(provider.getUrlFor(playerId, username), currentCape),
                currentCape, 3
            );
            if (!cape.failed() && cape != currentCape) {
                return CompletableFuture.completedFuture(cape);
            }
        }

        return CompletableFuture.completedFuture(currentCape);
    }

    private static CompletableFuture<Cape> requestCape(String capeUrl, Cape currentCape) {
        if (capeUrl == null || capeUrl.isEmpty()) return CompletableFuture.completedFuture(currentCape);

        CompletableFuture<Cape> future;
        Cape cape = supplyCape(capeUrl);
        future = CompletableFuture.completedFuture(cape);
        return future;
    }

    private static Cape supplyCape(String capeUrl) {
        byte[] cape = new byte[0];
        try {
            cape = Utils.bufferedImageToImageData(ImageIO.read(new URL(capeUrl)));
        } catch (Exception ignored) {
        } // just ignore I guess

        String[] urlSection = capeUrl.split("/"); // A real url is expected at this stage

        return new Cape(
            capeUrl,
            urlSection[urlSection.length - 1], // get the texture id and use it as cape id
            cape,
            cape.length == 0
        );
    }
}
