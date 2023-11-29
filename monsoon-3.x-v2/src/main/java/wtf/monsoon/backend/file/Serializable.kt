package wtf.monsoon.backend.file

import org.json.JSONObject


interface Serializable {
    fun save(): JSONObject
    fun load(obj: JSONObject)
}