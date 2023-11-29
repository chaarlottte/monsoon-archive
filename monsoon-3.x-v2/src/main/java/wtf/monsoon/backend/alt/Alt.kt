package wtf.monsoon.backend.alt

import fr.litarvan.openauth.microsoft.AuthTokens
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator
import net.minecraft.client.Minecraft
import net.minecraft.util.Session
import org.json.JSONObject
import wtf.monsoon.backend.file.Serializable
import java.util.*

open class Alt(open var username: String = "", var authenticator: Authenticator) : Serializable {
    open fun getSession(): Session? {
        return Minecraft.getMinecraft().session
    }

    override fun save(): JSONObject {
        val obj = JSONObject()
        obj.put("authenticator", this.authenticator.key)
        // obj.put("username", this.username)
        return obj
    }

    override fun load(obj: JSONObject) {
        // this.username = obj.getString("username")
        Authenticator.values().forEach {
            if(obj.getString("authenticator") == it.key)
                this.authenticator = it
        }
    }
}

class CrackedAlt(override var username: String = "") : Alt(username, Authenticator.CRACKED) {
    fun getSessionObj(): Session {
        return Session(username, UUID.randomUUID().toString(), "", "legacy");
    }

    override fun save(): JSONObject {
        val obj = JSONObject()
        obj.put("authenticator", this.authenticator.key)
        obj.put("username", this.username)
        return obj
    }

    override fun load(obj: JSONObject) {
        this.username = obj.getString("username")
        super.load(obj)
    }
}

class MicrosoftAlt(private var email: String = "", private var password: String = "") : Alt(email, Authenticator.MICROSOFT) {

    private lateinit var accessToken: String
    private lateinit var refreshToken: String

    fun getSessionObj(): Session {
        val auth = MicrosoftAuthenticator()

        val result = auth.loginWithCredentials(email, password)
        val session = Session(result.profile.name, result.profile.id, result.accessToken, "legacy")
        this.username = session.username
        this.accessToken = result.accessToken
        this.refreshToken = result.refreshToken
        return session
    }

    override fun save(): JSONObject {
        val obj = JSONObject()
        obj.put("authenticator", this.authenticator.key)
        obj.put("email", this.email)
        obj.put("password", this.password)

        val tokenObj = JSONObject()
        tokenObj.put("accessToken", this.accessToken)
        tokenObj.put("refreshToken", this.refreshToken)
        tokenObj.put("username", this.username)

        obj.put("tokenObj", tokenObj)
        return obj
    }

    override fun load(obj: JSONObject) {
        this.email = obj.getString("email")
        this.password = obj.getString("password")
        super.load(obj)
    }
}

class TokenAlt(override var username: String = "", private var accessToken: String = "", private var refreshToken: String = "") : Alt(username, Authenticator.TOKEN) {
    fun getSessionObj(): Session? {
        val auth = MicrosoftAuthenticator()
        val tokens = AuthTokens(accessToken, refreshToken)
        val result: MicrosoftAuthResult = try {
            auth.loginWithTokens(tokens)
        } catch (e: MicrosoftAuthenticationException) {
            try {
                auth.loginWithRefreshToken(refreshToken)
            } catch (e2: MicrosoftAuthenticationException) {
                println(e)
                return null
            }
        }
        val session = Session(result.profile.name, result.profile.id, result.accessToken, "legacy")
        this.username = session.username
        return session
    }

    override fun save(): JSONObject {
        val obj = JSONObject()
        obj.put("accessToken", this.accessToken)
        obj.put("refreshToken", this.refreshToken)
        obj.put("username", this.username)
        obj.put("authenticator", this.authenticator.key)
        return obj
    }

    override fun load(obj: JSONObject) {
        this.username = obj.getString("username")
        this.accessToken = obj.getString("accessToken")
        this.refreshToken = obj.getString("refreshToken")
        super.load(obj)
    }
}

enum class Authenticator(val key: String) {
    MICROSOFT("Microsoft"),
    CRACKED("Cracked"),
    TOKEN("Tokens")
}