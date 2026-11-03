package app.syncfixes.patches.syncforreddit.shared

import app.revanced.com.android.tools.smali.dexlib2.mutable.MutableMethod
import app.revanced.patcher.extensions.instructionsOrNull
import app.revanced.patcher.extensions.string
import app.revanced.patcher.firstMethod
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.PatchException
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.Method

/**
 * Sync keeps its source file names, so methods are matched within the classes compiled from [sourceFile].
 * Obfuscated class and method names differ between Sync and Sync Pro. The first matching method is used.
 *
 * @return The mutable method, ready to be patched.
 */
internal fun BytecodePatchContext.methodInSourceFile(
    sourceFile: String,
    description: String,
    predicate: Method.() -> Boolean,
): MutableMethod {
    val method = classDefs.asSequence()
        .filter { it.sourceFile == sourceFile }
        .flatMap { it.methods.asSequence() }
        .firstOrNull(predicate)
        ?: throw PatchException("Could not find $description in $sourceFile")

    return firstMethod(method)
}

/**
 * The method named [name] with the given signature in [definingClass].
 *
 * @return The mutable method, ready to be patched.
 */
internal fun BytecodePatchContext.methodInClass(
    definingClass: String,
    name: String,
    returnType: String? = null,
    parameters: List<String>? = null,
): MutableMethod {
    val method = classDefs[definingClass]?.methods?.firstOrNull {
        it.name == name &&
            (returnType == null || it.returnType == returnType) &&
            (parameters == null || it.hasParameters(parameters))
    } ?: throw PatchException("Could not find $definingClass->$name")

    return firstMethod(method)
}

/**
 * Fails the patch before it changes anything if the extension class [type] was not merged into the app,
 * since the code the patch adds would call it.
 */
internal fun BytecodePatchContext.requireExtensionClass(type: String) {
    if (classDefs[type] == null) {
        throw PatchException(
            "Extension class $type is missing: another patch bundle may provide a different ${Constants.EXTENSION}",
        )
    }
}

internal val Method.isStatic get() = AccessFlags.STATIC.isSet(accessFlags)

internal val Method.isPublicStatic get() = isStatic && AccessFlags.PUBLIC.isSet(accessFlags)

internal fun Method.hasParameters(parameters: List<String>) =
    parameterTypes.map(CharSequence::toString) == parameters

/** Whether the method has a string constant containing each of [strings]. */
internal fun Method.hasStrings(vararg strings: String): Boolean {
    val constants = instructionsOrNull?.mapNotNull { it.string } ?: return false
    return strings.all { string -> constants.any { it.contains(string) } }
}
