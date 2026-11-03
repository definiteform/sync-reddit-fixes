/*
 * Copyright 2025 Morphe.
 * https://github.com/MorpheApp/morphe-patches-template
 * Adapted to ReVanced Patcher.
 */

package util

import app.revanced.patcher.patch.Patch
import app.revanced.patcher.patch.loadPatches
import app.syncfixes.patches.syncforreddit.shared.Constants.APP_NAMES
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.io.File
import java.net.URLClassLoader
import java.util.jar.Manifest

fun main() {
    val patchesFile = File("build/libs/").listFiles { file ->
        val fileName = file.name
        !fileName.contains("javadoc") &&
                !fileName.contains("sources") &&
                fileName.endsWith(".rvp")
    }!!.first()

    val loadedPatches = loadPatches(patchesFile) { file, throwable ->
        throw IllegalStateException("Failed to load patches from $file", throwable)
    }
    val patchClassLoader = URLClassLoader(arrayOf(patchesFile.toURI().toURL()))
    val manifest = patchClassLoader.getResources("META-INF/MANIFEST.MF")

    while (manifest.hasMoreElements()) {
        Manifest(manifest.nextElement().openStream())
            .mainAttributes
            .getValue("Version")
            ?.let {
                generatePatchList(it, loadedPatches)
            }
    }
}

private fun generatePatchList(version: String, patches: Set<Patch>) {
    val listJson = File("../patches-list.json")

    val patchesMap = patches.sortedBy { it.name }.map { patch ->
        JsonPatch(
            name = patch.name!!,
            description = patch.description,
            default = patch.use,
            // Patches with null compatiblePackages are universal (apply to any app).
            compatiblePackages = patch.compatiblePackages?.map { (packageName, versions) ->
                JsonCompatibility(
                    packageName = packageName,
                    name = APP_NAMES[packageName],
                    // Null versions means every version is supported.
                    targets = versions?.map { JsonCompatibility.Target(version = it) } ?: emptyList(),
                )
            },
            dependencies = patch.dependencies.mapNotNull { it.name },
            options = patch.options.values.map { option ->
                JsonPatch.Option(
                    key = option.name,
                    title = option.name,
                    description = option.description,
                    required = option.required,
                    type = option.type.toString(),
                    default = option.default,
                    values = option.values,
                )
            }
        )
    }

    val gson = GsonBuilder()
        .serializeNulls()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()

    val jsonObject = JsonObject()
    jsonObject.addProperty(
        "NOTE",
        "Do NOT manually edit this file. This file is automatically updated when " +
                "semantic release (release.yml) runs. Manually editing this file can break " +
                "your releases and break third party tools that use this file."
    )
    jsonObject.addProperty("version", version)
    jsonObject.add("patches", gson.toJsonTree(patchesMap))

    listJson.writeText(gson.toJson(jsonObject))
}

/** JSON representation of a patch entry in patches-list.json. */
@Suppress("unused")
private class JsonPatch(
    val name: String? = null,
    val description: String? = null,
    val default: Boolean = true,
    /** Null means the patch is universal and applies to any app. */
    val compatiblePackages: List<JsonCompatibility>? = null,
    val dependencies: List<String>,
    val options: List<Option>,
) {
    class Option(
        val key: String,
        val title: String?,
        val description: String?,
        val required: Boolean,
        val type: String,
        val default: Any?,
        val values: Map<String, Any?>?,
    )
}

/** JSON representation of a compatible app entry. */
@Suppress("unused")
private class JsonCompatibility(
    /** Android package name, e.g. com.laurencedawson.reddit_sync. */
    val packageName: String,
    /** Human-readable app name, e.g. "Sync for Reddit". */
    val name: String?,
    /** Supported versions. Empty means every version is supported. */
    val targets: List<Target>,
) {
    class Target(
        val version: String,
    )
}
