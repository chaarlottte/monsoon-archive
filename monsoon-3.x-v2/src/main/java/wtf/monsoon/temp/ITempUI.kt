package wtf.monsoon.temp

import wtf.monsoon.client.util.ui.NVGWrapper

interface ITempUI {

    fun render2D(nvg: NVGWrapper, width: Int, height: Int)

}