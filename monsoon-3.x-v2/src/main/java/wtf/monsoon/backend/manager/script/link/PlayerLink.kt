package wtf.monsoon.backend.manager.script.link

import spritz.api.annotations.Identifier

/**
 * @author surge
 * @since 27/03/2023
 */
class PlayerLink : Link() {

    @Identifier("get_position")
    fun getPosition(): Vector3 {
        return Vector3(mc.thePlayer.posX.toFloat(), mc.thePlayer.posY.toFloat(), mc.thePlayer.posZ.toFloat())
    }

    @Identifier("send_message")
    fun sendMessage(message: String) {
        mc.thePlayer.sendChatMessage(message)
    }

    @Identifier("jump")
    fun jump() {
        mc.thePlayer.jump()
    }

    @Identifier("on_ground")
    fun onGround(): Boolean {
        return mc.thePlayer.onGround
    }

    // @Identifier("Vector3") // TODO
    class Vector3(@JvmField val x: Float, @JvmField val y: Float, @JvmField val z: Float)

}