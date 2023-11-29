package wtf.monsoon.api.manager.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Session;
import wtf.monsoon.Wrapper;

import java.net.Proxy;
import java.util.UUID;

/**
 * @author Surge
 * @since 22/08/2022
 */
public class Alt {

    @Getter
    @Setter
    private String email, password, username = "";

    @Getter
    private final Authenticator authenticator;

    private Session sessionBuffer;

    public Alt(String email, String password, Authenticator authenticator) {
        this.email = email;
        this.password = password;
        this.authenticator = authenticator;
    }

    public Session getSession() throws MicrosoftAuthenticationException {
        if (sessionBuffer == null) {
            sessionBuffer = loadSession();
        }

        return sessionBuffer;
    }

    public Session loadSession() throws MicrosoftAuthenticationException {
        switch (authenticator) {
            case MICROSOFT: {
                Wrapper.getMonsoon().getAltManager().getAlteningAuthentication().updateService(AlteningServiceType.MOJANG);
                MicrosoftAuthenticator auth = new MicrosoftAuthenticator();

                MicrosoftAuthResult result = auth.loginWithCredentials(email, password);
                Session session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");
                setUsername(session.getUsername());
                return session;
            }

            case CRACKED: {
                setUsername(email);
                return new Session(email, UUID.randomUUID().toString(), "", "legacy");
            }

            case ALTENING: {
                YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
                YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
                auth.setUsername(email);
                auth.setPassword("A");
                try {
                    auth.logIn();
                    Session session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
                    setUsername(session.getUsername());
                    return session;
                } catch (AuthenticationException localAuthenticationException) {
                    localAuthenticationException.printStackTrace();
                    return null;
                }
            }

            case OAUTH: {
                MicrosoftOAuth2Login microsoftOAuth2 = new MicrosoftOAuth2Login();

                try {
                    MicrosoftAuthResult result = microsoftOAuth2.getAccessToken();
                    if(result != null) {
                        Session session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");
                        setUsername(session.getUsername());
                        return session;
                    } else return null;
                } catch (Exception exception) {
                    return null;
                }
            }
        }

        return null;
    }

    public enum Authenticator {
        /**
         * Microsoft alt
         */
        MICROSOFT,

        /**
         * Cracked (non legit) alt
         */
        CRACKED,

        /**
         * Altening token that is generated.
         */
        ALTENING,

        /**
         * Token from Microsoft OAuth2.
         */
        OAUTH
    }

}
