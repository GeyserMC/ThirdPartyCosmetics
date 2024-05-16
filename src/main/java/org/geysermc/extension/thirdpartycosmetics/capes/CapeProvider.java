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

import org.geysermc.extension.thirdpartycosmetics.UrlType;

import java.util.Arrays;
import java.util.UUID;

public enum CapeProvider {
    OPTIFINE("https://optifine.net/capes/%s.png", UrlType.USERNAME),
    LABYMOD("https://dl.labymod.net/capes/%s", UrlType.UUID_DASHED),
    MINECRAFTCAPES("https://api.minecraftcapes.net/profile/%s/cape", UrlType.UUID);

    public static final CapeProvider[] VALUES = values();
    private String url;
    private UrlType type;

    CapeProvider(String url, UrlType urlType) {
        this.url = url;
        this.type = urlType;
    }

    public String getUrlFor(String type) {
        return String.format(url, type);
    }

    public String getUrlFor(UUID uuid, String username) {
        return getUrlFor(toRequestedType(type, uuid, username));
    }

    public static String toRequestedType(UrlType type, UUID uuid, String username) {
        return switch (type) {
            case UUID -> uuid.toString().replace("-", "");
            case UUID_DASHED -> uuid.toString();
            default -> username;
        };
    }
}
