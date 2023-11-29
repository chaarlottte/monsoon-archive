package wtf.monsoon.misc.protection.check.impl

import store.vantage.api.utils.os.OSUtil
import store.vantage.api.utils.os.OperatingSystem
import wtf.monsoon.Wrapper
import wtf.monsoon.client.util.network.ServerUtil
import wtf.monsoon.misc.protection.check.ProtectionCheck
import java.io.File
import java.nio.file.Files
import java.util.*


class TamperedHostsFileCheck : ProtectionCheck(Trigger.INITIALIZE) {
    override fun check(): Boolean {
        val hostsFile = File(if (OSUtil.getOS() === OperatingSystem.WINDOWS) System.getenv("WinDir") + "\\System32\\drivers\\etc\\hosts" else "/etc/hosts");
        Thread {
            try {
                while (true) {
                    if (!hostsFile.exists() || !hostsFile.canRead() || !hostsFile.isFile)
                        Wrapper.monsoon.protectionManager.crash()

                    for (line in Files.readAllLines(hostsFile.toPath())) {
                        val format = line.lowercase(Locale.ENGLISH).trim { it <= ' ' }
                        if (format.contains("vantage")
                            || format.contains("monsoon"))  // packet log
                            Wrapper.monsoon.protectionManager.crash()

                        if(ServerUtil.isConnectedToAnyServer())
                            if (format.contains(ServerUtil.getCurrentServerIP()))  // packet log
                                Wrapper.monsoon.protectionManager.crash()
                    }
                    Thread.sleep(60000L)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                Wrapper.monsoon.protectionManager.crash()
            }
        }.start()

        return false
    }
}