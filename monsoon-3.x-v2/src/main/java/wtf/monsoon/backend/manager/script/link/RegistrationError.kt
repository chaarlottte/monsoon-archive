package wtf.monsoon.backend.manager.script.link

import spritz.error.interpreting.RuntimeError
import spritz.interpreter.context.Context
import spritz.lexer.position.Position

/**
 * @author surge
 * @since 31/03/2023
 */
class RegistrationError(details: String, start: Position, end: Position, context: Context) : RuntimeError("RegistrationError", details, start, end, context)