/**
 * Copyright 2020 WaterdogTEAM
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pe.waterdog.plugin;

import com.google.common.base.Preconditions;
import pe.waterdog.ProxyServer;
import pe.waterdog.logger.Logger;
import pe.waterdog.logger.PluginLogger;
import pe.waterdog.utils.Configuration;
import pe.waterdog.utils.FileUtils;
import pe.waterdog.utils.YamlConfig;
import pe.waterdog.utils.exceptions.PluginChangeStateException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class Plugin {

    private PluginYAML description;
    private ProxyServer proxy;
    private Logger logger;

    private File pluginFile;
    private File dataFolder;
    private File configFile;

    private Configuration config;

    private boolean initialized = false;
    protected boolean enabled = false;

    public Plugin() {
    }

    protected final void init(PluginYAML description, ProxyServer proxy, File pluginFile){
        Preconditions.checkArgument(!this.initialized, "Plugin has been already initialized!");
        this.initialized = true;
        this.description = description;
        this.proxy = proxy;
        this.logger = new PluginLogger(this);

        this.pluginFile = pluginFile;
        this.dataFolder = new File(proxy.getPluginPath()+"/"+description.getName().toLowerCase()+"/");
        if (!this.dataFolder.exists()){
            this.dataFolder.mkdirs();
        }
        this.configFile = new File(this.dataFolder, "config.yml");
    }

    public void onStartup(){
    }
    public abstract void onEnable();
    public void onDisable(){
    }

    public void setEnabled(boolean enabled) throws PluginChangeStateException {
        if (this.enabled == enabled){
            return;
        }
        this.enabled = enabled;
        try {
            if (enabled){
                this.onEnable();
            }else {
                this.onDisable();
            }
        }catch (Exception e){
            throw new PluginChangeStateException("Can not "+(enabled? "enable" : "disable")+" plugin "+this.description.getName()+"!", e);
        }
    }

    public InputStream getResourceFile(String filename) {
        try {
            JarFile pluginJar = new JarFile(this.pluginFile);
            JarEntry entry = pluginJar.getJarEntry(filename);
            return pluginJar.getInputStream(entry);
        }catch (IOException e){
            this.proxy.getLogger().error("Can not get plugin resource!", e);
        }
        return null;
    }

    public boolean saveResource(String filename) {
        return this.saveResource(filename, false);
    }

    public boolean saveResource(String filename, boolean replace) {
        return this.saveResource(filename, filename, replace);
    }

    public boolean saveResource(String filename, String outputName, boolean replace) {
        Preconditions.checkArgument(filename != null && !filename.trim().isEmpty(), "Filename can not be null!");

        File file = new File(this.dataFolder, outputName);
        if (file.exists() && !replace) {
            return false;
        }

        try {
            InputStream resource = this.getResourceFile(filename);
            if (resource == null) return false;

            File outFolder = file.getParentFile();
            if (!outFolder.exists()) {
                outFolder.mkdirs();
            }
            FileUtils.writeFile(file, resource);
        } catch (IOException e) {
            this.proxy.getLogger().error("Can not save plugin file!", e);
            return false;
        }
        return true;
    }

    public void loadConfig(){
        try {
            this.saveResource("config.yml");
            this.config = new YamlConfig(this.configFile);
        }catch (Exception e){
            this.proxy.getLogger().error("Can not load plugin config!");
        }
    }

    public Configuration getConfig(){
        if (this.config == null){
            this.loadConfig();
        }
        return this.config;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public PluginYAML getDescription() {
        return this.description;
    }

    public String getName(){
        return this.description.getName();
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public File getDataFolder() {
        return this.dataFolder;
    }
}
