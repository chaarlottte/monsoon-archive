package wtf.monsoon.backend.manager

import wtf.monsoon.backend.alt.Alt

class AltManager {
    val alts: MutableList<Alt> = ArrayList()

    fun addAlt(alt: Alt) {
        this.alts += alt
    }
}