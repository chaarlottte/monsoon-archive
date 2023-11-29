package wtf.monsoon.backend

/**
 * Not inside the `module` package because this could be used in the future
 * for other features, e.g. category dependent commands
 * @author surge
 * @since 09/02/2023
 */
enum class Category(val iconCode: Char) {

    COMBAT('A'),
    MOVEMENT('B'),
    PLAYER('D'),
    VISUAL('C'),
    EXPLOIT('E'),
    GHOST('F'),
    CLIENT('G'),
    HUD('G'),
    SCRIPT('H')

}