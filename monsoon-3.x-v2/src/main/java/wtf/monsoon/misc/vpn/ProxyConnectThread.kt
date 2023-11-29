package wtf.monsoon.misc.vpn

import wtf.monsoon.Wrapper.monsoon
import java.net.Authenticator
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy


class ProxyInitThread : Thread {
    private var username = ""
    private var password = ""
    private val host: String
    private val port: Int
    var status: String

    constructor(host: String, port: Int, username: String, password: String) : super() {
        this.host = host
        this.port = port
        this.username = username
        this.password = password
        status = "Waiting..."
    }

    constructor(host: String, port: Int) : super() {
        this.host = host
        this.port = port
        status = "Waiting..."
    }

    private fun createSession(): Proxy {
        if (username != "" && password != "") {
            Authenticator.setDefault(object : Authenticator() {
                public override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(username, password.toCharArray())
                }
            })
        } else {
            Authenticator.setDefault(null)
        }
        return Proxy(Proxy.Type.SOCKS, InetSocketAddress(host, port))
    }

    override fun run() {
        try {
            monsoon.proxy = createSession()
            status = "Success! IP: " + host + ", Port: " + port
        } catch (e: Exception) {
            e.printStackTrace()
            status = "Failed."
        }
        println(status)
    }
}