/*
 * Ported from KingOfPoptart/patcheddit (https://github.com/KingOfPoptart/patcheddit), which adapted it
 * from humzakh/HK-Morphe-Patches (https://github.com/humzakh/HK-Morphe-Patches), GPLv3.
 */
package app.syncfixes.patches.syncforreddit.ultra

import app.revanced.patcher.patch.BytecodePatchContext
import app.syncfixes.patches.syncforreddit.shared.hasStrings
import app.syncfixes.patches.syncforreddit.shared.isPublicStatic
import app.syncfixes.patches.syncforreddit.shared.methodInSourceFile

/**
 * Sync's UltraHelper keeps its source file name, so methods are matched within it. Obfuscated names
 * differ between Sync and Sync Pro (uc/b and fc/b).
 */
private const val ULTRA_HELPER_SOURCE = "UltraHelper.java"

/** Whether the signed-in account has Sync Ultra. */
internal val BytecodePatchContext.ultraHelperMethod
    get() = methodInSourceFile(ULTRA_HELPER_SOURCE, "the Sync Ultra check") {
        isPublicStatic && returnType == "Z" && parameterTypes.isEmpty() && hasStrings("UltraHelper")
    }

/** Display name of the account's Sync Ultra subscription. */
internal val BytecodePatchContext.syncUltraSubTypeMethod
    get() = methodInSourceFile(ULTRA_HELPER_SOURCE, "the Sync Ultra subscription type") {
        isPublicStatic && returnType == "Ljava/lang/String;" && parameterTypes.isEmpty() &&
            hasStrings("Sync Ultra Monthly", "Sync Ultra Yearly", "Sync Ultra lifetime")
    }

/** Whether the account's Sync Ultra subscription is the lifetime one. */
internal val BytecodePatchContext.ultraLifetimeMethod
    get() = methodInSourceFile(ULTRA_HELPER_SOURCE, "the Sync Ultra lifetime check") {
        isPublicStatic && returnType == "Z" && parameterTypes.isEmpty() && hasStrings("ultra_lifetime")
    }
