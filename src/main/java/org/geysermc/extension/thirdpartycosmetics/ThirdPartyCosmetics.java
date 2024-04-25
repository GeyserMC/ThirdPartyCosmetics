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

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.extension.thirdpartycosmetics.capes.CapeFetcher;
import org.geysermc.extension.thirdpartycosmetics.capes.CapeProvider;
import org.geysermc.extension.thirdpartycosmetics.ears.EarsFetcher;
import org.geysermc.extension.thirdpartycosmetics.ears.EarsProvider;
import org.geysermc.geyser.api.event.bedrock.SessionSkinApplyEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.skin.Cape;
import org.geysermc.geyser.api.skin.Skin;

public class ThirdPartyCosmetics implements Extension {
    @Subscribe
    public void onSkinApplyEvent(SessionSkinApplyEvent event) {
        // Not a bedrock player apply cosmetics
        if (!event.bedrock()) {
            handleCapes(event);
            handleEars(event);
        }
    }

    private void handleCapes(SessionSkinApplyEvent event) {
        Cape cape = Utils.getOrDefault(CapeFetcher.request(
            event.skinData().cape(), event.uuid(), event.username()
        ), event.skinData().cape(), CapeProvider.VALUES.length * 3);

        if (!cape.failed() && cape != event.skinData().cape()) {
            this.logger().debug("Applied cape texture for " + event.username() + " (" + event.uuid() + ")");
            event.cape(cape);
        }
    }

    private void handleEars(SessionSkinApplyEvent event) {
        // Let deadmau5 have his ears
        if ("deadmau5".equals(event.username())) {
            event.geometry(EarsFetcher.geometry(event.slim()));
            return;
        }

        // Get the ears texture for the player
        Skin skin = Utils.getOrDefault(EarsFetcher.request(
            event.skinData().skin(), event.uuid(), event.username()
        ), event.skinData().skin(), EarsProvider.VALUES.length * 3);

        // Does the skin have an ears texture
        if (skin != event.skinData().skin()) {
            this.logger().debug("Applied ears texture for " + event.username() + " (" + event.uuid() + ")");
            event.geometry(EarsFetcher.geometry(event.slim()));
            event.skin(skin);
        }
    }
}
