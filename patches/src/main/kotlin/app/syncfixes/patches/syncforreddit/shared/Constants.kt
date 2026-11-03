package app.syncfixes.patches.syncforreddit.shared

import app.revanced.patcher.patch.Package

object Constants {
    /** Sync's final release, the same version name for the free and Pro apps. */
    private const val SYNC_VERSION = "v23.06.30-13:39"

    const val SYNC_PACKAGE = "com.laurencedawson.reddit_sync"
    const val SYNC_PRO_PACKAGE = "com.laurencedawson.reddit_sync.pro"

    /** Display names of the supported apps, used for the generated patch list. */
    val APP_NAMES = mapOf(
        SYNC_PACKAGE to "Sync for Reddit",
        SYNC_PRO_PACKAGE to "Sync for Reddit Pro",
    )

    val COMPATIBILITY_SYNC_ALL: Array<Package> = arrayOf(
        SYNC_PACKAGE to setOf(SYNC_VERSION),
        SYNC_PRO_PACKAGE to setOf(SYNC_VERSION),
    )

    const val EXTENSION = "extensions/syncforreddit.rve"
    const val EXTENSION_PACKAGE = "Lapp/syncfixes/extension/syncforreddit"

    const val NETWORK_RESPONSE = "Lcom/android/volley/NetworkResponse;"
    const val VOLLEY_RESPONSE = "Lcom/android/volley/Response;"
    const val VOLLEY_REQUEST = "Lcom/android/volley/Request;"
}
