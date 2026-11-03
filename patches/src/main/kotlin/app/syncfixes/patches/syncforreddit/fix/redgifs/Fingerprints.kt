package app.syncfixes.patches.syncforreddit.fix.redgifs

import app.revanced.patcher.extensions.instructionsOrNull
import app.revanced.patcher.extensions.methodReference
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.PatchException
import app.syncfixes.patches.syncforreddit.shared.Constants.EXTENSION_PACKAGE
import app.syncfixes.patches.syncforreddit.shared.hasParameters
import app.syncfixes.patches.syncforreddit.shared.hasStrings
import app.syncfixes.patches.syncforreddit.shared.isPublicStatic
import app.syncfixes.patches.syncforreddit.shared.methodInClass
import app.syncfixes.patches.syncforreddit.shared.methodInSourceFile
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/**
 * Sync keeps its source file names, so classes are matched by them. Obfuscated names differ between
 * Sync and Sync Pro (for example LinkHandler is y7/a and j7/a, OkHttpHelper a8/a and l7/a).
 */
internal const val OKHTTP_CLIENT = "Lokhttp3/OkHttpClient;"
internal const val OKHTTP_CLIENT_BUILDER = "Lokhttp3/OkHttpClient\$Builder;"
internal const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/FixRedgifsApiPatch;"

private const val BASIC_NETWORK = "Lcom/android/volley/toolbox/BasicNetwork;"

/**
 * The OkHttp client getter called by the Volley network that sends Sync's requests (Redgifs included):
 * a static call without parameters that returns an OkHttpClient.
 */
internal val BytecodePatchContext.volleyClientGetterCall: MethodReference
    get() {
        val basicNetwork = classDefs[BASIC_NETWORK] ?: throw PatchException("Class $BASIC_NETWORK not found")

        return basicNetwork.methods.asSequence()
            .flatMap { it.instructionsOrNull?.asSequence() ?: emptySequence() }
            .filter { it.opcode == Opcode.INVOKE_STATIC || it.opcode == Opcode.INVOKE_STATIC_RANGE }
            .mapNotNull { it.methodReference }
            .firstOrNull { it.returnType == OKHTTP_CLIENT && it.parameterTypes.isEmpty() }
            ?: throw PatchException("$BASIC_NETWORK does not call an OkHttp client getter")
    }

/** AccountSingleton's user agent getter, the user agent Sync sends everywhere. */
internal val BytecodePatchContext.userAgentMethod
    get() = methodInSourceFile("AccountSingleton.java", "the user agent getter") {
        isPublicStatic && returnType == "Ljava/lang/String;" && parameterTypes.isEmpty()
    }

/** The extension's placeholder for Sync's user agent. */
internal val BytecodePatchContext.extensionUserAgentMethod
    get() = methodInClass(EXTENSION_CLASS, "defaultUserAgent")

/** LinkHandler.getGfycatId(url), which supplies the id of the /v2/gifs/{id} request. */
internal val BytecodePatchContext.getGfycatIdMethod
    get() = methodInSourceFile("LinkHandler.java", "getGfycatId") {
        isPublicStatic && returnType == "Ljava/lang/String;" && hasParameters(listOf("Ljava/lang/String;")) &&
            hasStrings("-size_restricted", "-max-1mb-poster")
    }
