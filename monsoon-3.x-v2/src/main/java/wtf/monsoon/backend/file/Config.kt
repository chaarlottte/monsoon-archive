package wtf.monsoon.backend.file

import org.json.JSONObject
import wtf.monsoon.Wrapper
import java.io.File
import java.io.IOException

class Config(val name: String) : Serializable {

    var file: File = File(Wrapper.monsoon.fileManager.directories["configs"], "$name.json")

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    override fun save(): JSONObject {
        val obj = JSONObject()
        val moduleObj = JSONObject()
        val dataObj = JSONObject()

        Wrapper.monsoon.moduleManager.forEach { (_, module) ->
            moduleObj.put(module.saveName(), module.save())
        }

        // also add stuff like author later on when users exist
        dataObj.put("version", Wrapper.monsoon.version)

        obj.put("data", dataObj)
        obj.put("modules", moduleObj)

        return obj
    }

    override fun load(obj: JSONObject) {
        if (obj.has("modules")) {
            val moduleObj: JSONObject = obj.getJSONObject("modules")

            Wrapper.monsoon.moduleManager.forEach { (_, module) ->
                if (moduleObj.has(module.saveName())) {
                    module.load(moduleObj.getJSONObject(module.saveName()))
                }
            }
        }
    }

}