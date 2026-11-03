package app.syncfixes.patches.syncforreddit.fix.comments

import app.revanced.com.android.tools.smali.dexlib2.mutable.MutableMethod
import app.revanced.patcher.patch.BytecodePatchContext
import app.syncfixes.patches.syncforreddit.shared.Constants.NETWORK_RESPONSE
import app.syncfixes.patches.syncforreddit.shared.Constants.VOLLEY_RESPONSE
import app.syncfixes.patches.syncforreddit.shared.hasParameters
import app.syncfixes.patches.syncforreddit.shared.hasStrings
import app.syncfixes.patches.syncforreddit.shared.methodInClass
import app.syncfixes.patches.syncforreddit.shared.methodInSourceFile

/**
 * Sync keeps its source file names, so classes are matched by them. Obfuscated names differ between
 * Sync and Sync Pro (y8/f and j8/f for the request, ya/d.V() and ja/d.z0() for the handler).
 */
private const val MORE_REQUEST_SOURCE = "OAuthCommentsMoreRequest.java"

/**
 * OAuthCommentsMoreRequest(Context, String listId, String linkId, String moreId, String children,
 * int position, int level, String sort, Listener, ErrorListener).
 */
private val MORE_REQUEST_CONSTRUCTOR_PARAMETERS = listOf(
    "Landroid/content/Context;",
    "Ljava/lang/String;",
    "Ljava/lang/String;",
    "Ljava/lang/String;",
    "Ljava/lang/String;",
    "I",
    "I",
    "Ljava/lang/String;",
    "Lcom/android/volley/Response\$Listener;",
    "Lcom/android/volley/Response\$ErrorListener;",
)

/**
 * CommentsPresenterImpl's "load more" handler: logs "Requesting more: ", then builds the request from
 * its only parameter, the Post row of the "more" item.
 */
internal val BytecodePatchContext.requestMoreMethod
    get() = methodInSourceFile("CommentsPresenterImpl.java", "the load more handler") {
        returnType == "V" && parameterTypes.size == 1 && parameterTypes.single().startsWith("L") &&
            hasStrings("Requesting more: ")
    }

internal val BytecodePatchContext.moreRequestConstructorMethod
    get() = methodInSourceFile(MORE_REQUEST_SOURCE, "the load more request constructor") {
        name == "<init>" && returnType == "V" && hasParameters(MORE_REQUEST_CONSTRUCTOR_PARAMETERS) &&
            hasStrings("api/morechildren?always_show_media=1&profile_img=true")
    }

/** OAuthCommentsMoreRequest.parseNetworkResponse, in the class of [constructor]. */
internal fun BytecodePatchContext.moreParseResponseMethod(constructor: MutableMethod) = methodInClass(
    constructor.definingClass,
    name = "parseNetworkResponse",
    returnType = VOLLEY_RESPONSE,
    parameters = listOf(NETWORK_RESPONSE),
)
