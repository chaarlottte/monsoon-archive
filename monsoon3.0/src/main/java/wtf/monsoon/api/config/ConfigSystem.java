package wtf.monsoon.api.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.luaj.vm2.ast.Str;
import org.lwjgl.Sys;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.alt.Alt;
import wtf.monsoon.api.manager.alt.AltManager;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.AES256;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * The `ConfigSystem` class manages a collection of configuration files and scripts.
 * It creates a directory structure with a root folder called "monsoon" and two subfolders:
 * "configs" for storing configuration files and "scripts" for storing scripts.
 * If the directories do not exist, they will be created when the `ConfigSystem` object is instantiated.
 *
 * @author Surge
 * @since 02/01/2023
 */
public class ConfigSystem {

    private final HashMap<String, File> directories;

    /**
     * Constructs a `ConfigSystem` object and creates the necessary directories if they do not exist.
     */
    public ConfigSystem() {
        directories = new HashMap(){{
            put("root", new File("monsoon"));
            put("configs", new File((File) get("root"), "configs"));
            put("scripts", new File((File) get("root"), "scripts"));
        }};

        directories.forEach((identifier, file) -> {
            if (!file.exists()) {
                file.mkdirs();
            }
        });
    }

    /**
     * Saves configuration data for the client to the specified file.
     * This method creates a new Thread and within that thread, it initializes a Map called binds
     * and populates it with data about keybindings for the client's modules.
     * It then calls the save() method, passing it the binds Map and a File object for the file "binds.json" in the client's root directory.
     * Next, the method initializes another Map called moduleData and adds a JSON object containing the client version to it.
     * It then iterates through the client's modules, creating a JSON object for each one containing data about its enabled/disabled state and visibility,
     * as well as all the module's settings. This data is then added to the moduleData Map, using the module's name as the key.
     * Finally, the method calls the save() method again, passing it the moduleData Map and a File object for a file with the name specified by the name parameter
     * and the file extension ".json" in the client's configs directory.
     * @param name the name of the file to save the configuration data to
     */
    public void save(String name) {
        new Thread(() -> {
            Map<String, JSONObject> binds = new HashMap<>();

            Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
                JSONObject object = new JSONObject();

                try {
                    object.put("Keybinding", module.getKey().getValue().getButtonCode() + ":" + module.getKey().getValue().getDevice().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                binds.put(module.getName(), object);
            });

            save(new File(directories.get("root"), "binds.json"), binds);

            Map<String, JSONObject> moduleData = new HashMap<>();

            try {
                moduleData.put("monsoon-data", new JSONObject().put("client-version", Wrapper.getMonsoon().getVersion()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
                JSONObject object = new JSONObject();

                try {
                    object.put("enabled", module.isEnabled());
                    object.put("visible", module.isVisible());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                module.getSettingHierarchy().forEach(setting -> {
                    if (setting == module.getKey()) {
                        return;
                    }

                    try {
                        // ColorSetting needs to be saved differently
                        if (setting.getValue() instanceof Color) {
                            object.put(setting.getPath(), ((Color) setting.getValue()).getRed() + ":" + ((Color) setting.getValue()).getGreen() + ":" + ((Color) setting.getValue()).getBlue());
                        }

                        // Same with bind
                        else if (setting.getValue() instanceof Bind) {
                            object.put(setting.getPath(), ((Bind) setting.getValue()).getButtonCode() + ":" + ((Bind) setting.getValue()).getDevice().name());
                        }

                        // Enum setting value set thing
                        else if (setting.getValue() instanceof Enum<?>) {
                            object.put(setting.getPath(), ((Enum<?>) setting.getValue()).name());
                        }

                        // We can just add the value for the rest
                        else {
                            object.put(setting.getPath(), setting.getValue());
                        }
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                });

                moduleData.put(module.getName(), object);
            });

            save(new File(directories.get("configs"), name + ".json"), moduleData);
        }).start();
    }

    /**
     * Loads the given configuration, with a catch.
     * @param name the name of the file to load the configuration data from
     * @see ConfigSystem#loadNoCatch(String, boolean)
     */
    public void load(String name) {
        try {
            loadNoCatch(name, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the given configuration, with a catch. Has an option to not load old files.
     * @param name the name of the file to load the configuration data from
     * @param ignoreOld whether to ignore old configs
     * @see ConfigSystem#loadNoCatch(String, boolean)
     */
    public void load(String name, boolean ignoreOld) {
        try {
            loadNoCatch(name, ignoreOld);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads configuration data for the client from the specified file.
     * This method creates a new Thread and within that thread, it attempts to load JSON data from a file named "binds.json" in the client's root directory
     * and from a file with the name specified by the name parameter and the file extension ".json" in the client's configs directory.
     * It then uses this data to update the client's keybindings and module settings, respectively.
     * If the given config file is for an old version, a ConfigForOldVersionException will be thrown.
     * @param name the name of the file to load the configuration data from
     * @param ignoreOld a boolean value indicating whether to ignore old configuration data
     * @throws ConfigForOldVersionException if the given config file is for an old version
     */
    public void loadNoCatch(String name, boolean ignoreOld) throws ConfigForOldVersionException {
        new Thread(() -> {
            System.out.println("Loading config " + name);

            File binds = new File(directories.get("root"), "binds.json");

            if (binds.exists()) {
                JSONObject jsonBinds = loadJSON(binds);

                if (jsonBinds == null) {
                    System.out.println("Failed to load JSON object for " + name);
                    return;
                }

                Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
                    try {
                        String[] values = jsonBinds.getJSONObject(module.getName()).getString(module.getKey().getName()).split(":");
                        module.getKey().setValue(new Bind(Integer.parseInt(values[0]), Bind.Device.valueOf(values[1])));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            File data = new File(directories.get("configs"), name + ".json");

            if (!data.exists()) {
                System.out.println("Config " + name + " does not exist!");
                return;
            }

            JSONObject jsonData = loadJSON(data);

            try {
                String configVersion = jsonData.getJSONObject("monsoon-data").getString("client-version");
                String clientVersion = Wrapper.getMonsoon().getVersion();

                // There is literally no reason to have this
                /*if (!configVersion.equalsIgnoreCase(clientVersion) && !ignoreOld) {
                    throw new ConfigForOldVersionException("This config was made for a " + (Integer.parseInt(clientVersion) > Integer.parseInt(configVersion) ? "older" : "newer") + " version of Monsoon (" + configVersion + ").");
                }*/

                Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
                    try {
                        JSONObject moduleData = jsonData.getJSONObject(module.getName());

                        for(Setting setting : module.getSettingHierarchy()) {
                            if (setting.getValue() instanceof Boolean) {
                                ((Setting<Boolean>) setting).setValue(moduleData.getBoolean(setting.getPath()));
                            } else if (setting.getValue() instanceof Double) {
                                ((Setting<Double>) setting).setValue(moduleData.getDouble(setting.getPath()));
                            } else if (setting.getValue() instanceof Enum<?>) {
                                try {
                                    Enum<?> enumuration = (Enum<?>) setting.getValue();
                                    Enum<?> value = Enum.valueOf(enumuration.getClass(), moduleData.getString(setting.getPath()));

                                    ((Setting<Enum<?>>) setting).setValue(value);
                                } catch (IllegalArgumentException iae) {
                                    iae.printStackTrace();
                                    PlayerUtil.sendClientMessage("A setting for " + module.getName() + " couldn't be loaded.");
                                    continue;
                                }
                            } else if (setting.getValue() instanceof Color) {
                                String[] values = moduleData.getString(setting.getPath()).split(":");

                                ((Setting<Color>) setting).setValue(new Color(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])));
                            } else if (setting.getValue() instanceof Bind && setting != module.getKey()) {
                                String[] values = moduleData.getString(setting.getPath()).split(":");

                                ((Setting<Bind>) setting).setValue(new Bind(Integer.parseInt(values[0]), Bind.Device.valueOf(values[1])));
                            }
                        }

                        module.setEnabledSilent(moduleData.getBoolean("enabled"));
                        module.setVisible(moduleData.getBoolean("visible"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean configExists(String name) {
        return new File(directories.get("configs"), name + ".json").exists();
    }

    public void saveAlts(AltManager altManager) {
        new Thread(() -> {
            Map<String, JSONObject> altData = new HashMap<>();

            try {
                altData.put("client-data", new JSONObject().put("ALTENING-API-KEY", altManager.getApiKey()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            altManager.getAlts().forEach(alt -> {
                try {
                    altData.put(alt.getEmail(), new JSONObject().put("password", alt.getPassword()).put("authenticator", alt.getAuthenticator().name()).put("username", alt.getUsername()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            save(new File(directories.get("root"), "alts.json"), altData);
        }).start();
    }

    public void loadAlts(AltManager altManager) {
        new Thread(() -> {
            JSONObject json = loadJSON(new File(directories.get("root"), "alts.json"));

            if (json != null) {
                try {
                    JSONObject clientData = json.getJSONObject("client-data");
                    altManager.setApiKey(clientData.getString("ALTENING-API-KEY"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray keyNames = json.names();

                for (int i = 0; i < keyNames.length(); i++) {
                    try {
                        String name = keyNames.getString(i);

                        if (name.equals("client-data")) {
                            continue;
                        }

                        JSONObject data = json.getJSONObject(name);

                        Alt alt = new Alt(name, data.getString("password"), Alt.Authenticator.valueOf(data.getString("authenticator")));

                        if (data.getString("username") != null && !data.getString("username").equals("")) {
                            alt.setUsername(data.getString("username"));
                        }

                        altManager.addAlt(alt);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public File getDirectory(String name) {
        return directories.get(name);
    }

    private void save(File file, Map<String, JSONObject> objects) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            JSONObject object = new JSONObject();

            objects.forEach((name, json) -> {
                try {
                    object.put(name, json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(object.toString(4));

            // hey look its the funny function
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
    }

    private JSONObject loadJSON(File file) {
        try {
            return new JSONObject(FileUtils.readFileToString(file));
        } catch (JSONException | IOException e) {
            return null;
        }
    }

    public static void saveUsernamePassword(String username, String password) {
        File file = new File("monsoon_credentials");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            String combo = username + ":" + password;
            AES256 aes256 = new AES256(System.getProperty("user.name"), System.getProperty("user.name"));
            String encryptedString = aes256.encrypt(combo);

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(encryptedString);
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String[] getUsernamePassword() {
        String[] output = new String[] { };
        File file = new File("monsoon_credentials");
        try {
            if(!file.exists()) return output;
            String encryptedString = FileUtils.readFileToString(file);

            AES256 aes256 = new AES256(System.getProperty("user.name"), System.getProperty("user.name"));
            String decryptedString = aes256.decrypt(encryptedString);

            String username = decryptedString.split(":")[0];
            String password = decryptedString.split(":")[1];
            output = new String[] { username, password };
        } catch (Exception exception) {
            exception.printStackTrace();
            return output;
        }
        return output;
    }

    public static class ConfigForOldVersionException extends Exception {

        @Getter
        @Setter
        private String message;

        public ConfigForOldVersionException(String message) {
            setMessage(message);
        }
    }

}