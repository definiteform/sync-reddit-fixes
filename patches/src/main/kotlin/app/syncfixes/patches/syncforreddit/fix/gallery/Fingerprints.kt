package app.syncfixes.patches.syncforreddit.fix.gallery

import app.revanced.com.android.tools.smali.dexlib2.mutable.MutableMethod
import app.revanced.patcher.patch.BytecodePatchContext
import app.syncfixes.patches.syncforreddit.shared.Constants.NETWORK_RESPONSE
import app.syncfixes.patches.syncforreddit.shared.Constants.VOLLEY_RESPONSE
import app.syncfixes.patches.syncforreddit.shared.hasParameters
import app.syncfixes.patches.syncforreddit.shared.hasStrings
import app.syncfixes.patches.syncforreddit.shared.isStatic
import app.syncfixes.patches.syncforreddit.shared.methodInClass
import app.syncfixes.patches.syncforreddit.shared.methodInSourceFile

/**
 * Sync keeps its source file names, so classes are matched by them. Obfuscated names differ between
 * Sync and Sync Pro (y8/q.b() and j8/q.c() for this method).
 */
private const val GALLERY_REQUEST_SOURCE = "OAuthGalleryRequest.java"

/** OAuthGalleryRequest's static URL builder (also the key Sync reads its gallery cache with). */
internal val BytecodePatchContext.galleryUrlMethod
    get() = methodInSourceFile(GALLERY_REQUEST_SOURCE, "the gallery URL builder") {
        isStatic && returnType == "Ljava/lang/String;" && hasParameters(listOf("Ljava/lang/String;")) &&
            hasStrings("https://oauth.reddit.com/api/info.json?id=t3_")
    }

/** OAuthGalleryRequest.parseNetworkResponse, in the class of [urlMethod]. */
internal fun BytecodePatchContext.galleryParseResponseMethod(urlMethod: MutableMethod) = methodInClass(
    urlMethod.definingClass,
    name = "parseNetworkResponse",
    returnType = VOLLEY_RESPONSE,
    parameters = listOf(NETWORK_RESPONSE),
)
