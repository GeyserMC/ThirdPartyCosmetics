/*
 * Copyright (c) 2025 GeyserMC. http://geysermc.org
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

package org.geysermc.extension.thirdpartycosmetics.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class ConfigLoader {
    public static CosmeticConfig loadConfig(Path directory) {
        CosmeticConfig config = new CosmeticConfig();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path configFile = directory.resolve("config.json");

        try {
            // Create data directory
            Files.createDirectories(configFile.getParent());

            // Check Config file
            if (!configFile.toFile().exists()) {
                try {
                    Writer writer = new FileWriter(configFile.toFile());
                    gson.toJson(config, writer);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Load Config
            Reader reader = new FileReader(configFile.toFile());
            config = gson.fromJson(reader, CosmeticConfig.class);
            reader.close();

            // Sort the auth URLs
            config.capeUrls.sort(Comparator.comparingInt(CosmeticConfig.CosmeticProviders::priority));
            config.earsUrls.sort(Comparator.comparingInt(CosmeticConfig.CosmeticProviders::priority));
        } catch (IOException exception) {
            if (configFile.toFile().delete()) {
                return loadConfig(directory);
            }
        }

        return config;
    }
}