package wtf.monsoon.misc.vpn

import io.netty.bootstrap.ChannelFactory
import io.netty.channel.socket.oio.OioSocketChannel
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.Socket
import java.net.SocketImpl


class ProxyAdapter(private val proxy: Proxy) : ChannelFactory<OioSocketChannel> {
    override fun newChannel(): OioSocketChannel {
        if (proxy.address() != null && (proxy.address() as InetSocketAddress).port == 8080) {
            val rolf = Socket(proxy)
            return OioSocketChannel(rolf)
        }
        val socks = Socket(proxy)
        val clazzSocks: Class<*> = socks.javaClass
        var setSockVersion: Method? = null
        var sockImplField: Field? = null
        var socksimpl: SocketImpl? = null
        try {
            sockImplField = clazzSocks.getDeclaredField("impl")
            sockImplField.isAccessible = true
            socksimpl = sockImplField[socks] as SocketImpl
            if (socksimpl.javaClass.simpleName.equals("PlainSocketImpl", ignoreCase = true)) {
                return OioSocketChannel(socks)
            }
            val clazzSocksImpl: Class<out SocketImpl> = socksimpl.javaClass
            setSockVersion = clazzSocksImpl.getDeclaredMethod("setV4")
            setSockVersion.isAccessible = true
            // setSockVersion.invoke(socksimpl);
            sockImplField[socks] = socksimpl
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return OioSocketChannel(socks)
    }
}