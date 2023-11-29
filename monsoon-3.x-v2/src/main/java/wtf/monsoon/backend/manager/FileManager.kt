package wtf.monsoon.backend.manager

import org.apache.commons.io.FileUtils
import org.json.JSONException
import org.json.JSONObject
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.alt.Alt
import wtf.monsoon.backend.alt.CrackedAlt
import wtf.monsoon.backend.alt.MicrosoftAlt
import wtf.monsoon.backend.alt.TokenAlt
import wtf.monsoon.backend.file.Config
import wtf.monsoon.backend.setting.Bind
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.util.function.BiConsumer


class FileManager {

    var directories: HashMap<String, File> = object : HashMap<String, File>() {
        init {
            put("root", File("monsoon"))
            put("configs", File(get("root"), "configs"))
            put("scripts", File(get("root"), "scripts"))
            put("alts", File(get("root"), "alts"))
            put("scripts", File(get("root"), "scripts"))
        }
    }

    private val bindsFile: File = File(this.directories["root"], "binds.json")

    /**
     * Constructs a `ConfigSystem` object and creates the necessary directories if they do not exist.
     */
    init {
        directories.forEach { (_, file: File) ->
            if (!file.exists()) {
                file.mkdirs()
            }
        }
    }

    fun loadConfig(configName: String): Boolean {
        val config = Config(configName)

        if (!config.file.exists()) {
            config.file.createNewFile()
            return true
        }

        return try {
            val obj: JSONObject = this.loadJSON(config.file)
            config.load(obj)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun saveConfig(configName: String): Boolean {
        this.saveBinds()

        val config = Config(configName)

        if (!config.file.exists()) {
            config.file.createNewFile()
        }

        this.save(config.file, config.save())

        return true
    }

    fun saveBinds() {
        val binds = JSONObject()

        Wrapper.monsoon.moduleManager.forEach { (_, module) ->
            binds.put(module.saveName(), module.key.getValue().code.toString() + ":" + module.key.getValue().device.toString())
        }

        this.save(this.bindsFile, binds)
    }

    fun loadBinds() {
        if (this.bindsFile.exists()) {
            val jsonBinds = this.loadJSON(this.bindsFile)
            Wrapper.monsoon.moduleManager.forEach { (_, module) ->
                try {
                    val values =
                        jsonBinds.getString(module.saveName()).split(":".toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    module.key.setValue(Bind(values[0].toInt(), Bind.Device.valueOf(values[1])))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun loadAlts() {
        this.directories["alts"]!!.listFiles()?.forEach { file: File ->
            if (file.endsWith(".alt.json")) {
                val altJson: JSONObject = this.loadJSON(file)
                val altType: String = altJson.getString("authenticator")

                when (altType) {
                    "Microsoft" -> {
                        val alt = MicrosoftAlt()
                        alt.load(altJson)
                        Wrapper.monsoon.altManager.addAlt(alt)
                    }
                    "Cracked" -> {
                        val alt = CrackedAlt()
                        alt.load(altJson)
                        Wrapper.monsoon.altManager.addAlt(alt)
                    }
                    "Tokens" -> {
                        val alt = TokenAlt()
                        alt.load(altJson)
                        Wrapper.monsoon.altManager.addAlt(alt)
                    }
                }
            }
        }
    }

    fun saveAlts() {
        Wrapper.monsoon.altManager.alts.forEach { alt: Alt ->
            val file = File(this.directories["alts"], alt.username + ".alt.json")
            this.save(file, alt.save())
        }
    }

    private fun save(file: File, obj: JSONObject) {
        try {
            if (!file.exists()) {
                file.createNewFile()
            }

            val fileWriter = FileWriter(file)
            fileWriter.write(obj.toString(4))

            // hey look its the funny function
            fileWriter.flush()
            fileWriter.close()
        } catch (exception: IOException) {
            exception.printStackTrace()
        } catch (exception: JSONException) {
            exception.printStackTrace()
        }
    }

    private fun loadJSON(file: File): JSONObject {
        return JSONObject(FileUtils.readFileToString(file))
    }

}