package wtf.monsoon.client.ui.alt

import com.mojang.authlib.Agent
import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.Session
import java.net.Proxy


class AltLoginThread(private val username: String, private val password: String, private val mode: Int) :
    Thread("Alt Login Thread") {
    var status: String

    private val mc = Minecraft.getMinecraft()

    init {
        status = EnumChatFormatting.GRAY.toString() + "Waiting..."
    }

    private fun createSession(username: String, password: String): Session? {
        val service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "")
        val auth = service.createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication
        auth.setUsername(username)
        auth.setPassword(password)
        return try {
            auth.logIn()
            Session(auth.selectedProfile.name, auth.selectedProfile.id.toString(), auth.authenticatedToken, "mojang")
        } catch (localAuthenticationException: AuthenticationException) {
            localAuthenticationException.printStackTrace()
            null
        }
    }

    private fun createMicrosoftSession(username: String, password: String): Session? {
        return try {
            val authenticator = MicrosoftAuthenticator()
            var result: MicrosoftAuthResult? = null
            result = authenticator.loginWithCredentials(username, password)
            System.out.printf("Logged in with '%s'%n", result.profile.name)
            //Minecraft.getMinecraft().session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");
            Session(result.profile.name, result.profile.id, result.accessToken, "legacy")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun run() {
        if (password == "") {
            mc.session = Session(username, "", "", "mojang")
            status = EnumChatFormatting.GREEN.toString() + "Logged in. (" + username + " - offline name)"
            return
        }
        status = EnumChatFormatting.YELLOW.toString() + "Logging in..."
        val auth: Session? = createMicrosoftSession(username, password)
        if (auth == null) {
            status = EnumChatFormatting.RED.toString() + "Login failed!"
        } else {
            status = EnumChatFormatting.GREEN.toString() + "Logged in. (" + auth.username + ")"
            // this.mc.session = auth;
            mc.session = auth
        }
    }
}
