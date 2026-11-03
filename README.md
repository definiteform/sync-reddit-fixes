# 🧩 Sync for Reddit Fixes

ReVanced patches that fix **load more comments** and **Reddit galleries** in **Sync for Reddit** and
**Sync for Reddit Pro**, which broke when Reddit blocked `/api/morechildren` and `/api/info` for
third-party clients.

These patches are ported from [notBrian2/revanced-rif-fixes](https://github.com/notBrian2/revanced-rif-fixes)
(written for rif is fun) and adapted to Sync's Volley-based networking code. Use them with the Sync patches from
[wchill/patcheddit](https://github.com/wchill/patcheddit), which handle API access (Spoof client), login and
the other fixes Sync needs.

### How to use these patches

In ReVanced Manager, add this URL as a remote patch bundle:

```
https://raw.githubusercontent.com/trulow/sync-reddit-fixes/main/patches-bundle.json
```

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.2.0](https://github.com/trulow/sync-reddit-fixes/releases/tag/v1.2.0)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`&nbsp;&nbsp;•&nbsp;&nbsp;8 patches total
<details open>
<summary>📦 Sync for Reddit&nbsp;&nbsp;•&nbsp;&nbsp;4 patches</summary>
<br>

**Package name:** `com.laurencedawson.reddit_sync`

**🎯 Supported versions:**

| v23.06.30-13:39 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Fix Reddit galleries](#fix-reddit-galleries) | Fixes Reddit image galleries failing to load ("Could not load gallery") since Reddit blocked /api/info for third-party clients. Galleries are loaded from /comments instead. |  |
| [Fix Redgifs API](#fix-redgifs-api) | Fixes "Error connecting to Redgifs": answers the Redgifs endpoints Sync relies on that were removed, uses Redgifs' temporary token, and fixes video ids taken from links like redgifs.com/watch/{id}/?92. Works on Sync and Sync Pro. Turn off Patcheddit's "Fix Redgifs API" when using this one: both change the same code. |  |
| [Fix load more comments](#fix-load-more-comments) | Fixes "Error loading more comments" since Reddit blocked /api/morechildren for third-party clients. The missing comments are loaded from /comments instead. At the bottom of very large threads Reddit's 500 comment limit can still leave some out. |  |
| [Unlock Sync Ultra](#unlock-sync-ultra) | Unlocks Sync Ultra Lifetime. |  |

</details>

<details open>
<summary>📦 Sync for Reddit Pro&nbsp;&nbsp;•&nbsp;&nbsp;4 patches</summary>
<br>

**Package name:** `com.laurencedawson.reddit_sync.pro`

**🎯 Supported versions:**

| v23.06.30-13:39 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Fix Reddit galleries](#fix-reddit-galleries) | Fixes Reddit image galleries failing to load ("Could not load gallery") since Reddit blocked /api/info for third-party clients. Galleries are loaded from /comments instead. |  |
| [Fix Redgifs API](#fix-redgifs-api) | Fixes "Error connecting to Redgifs": answers the Redgifs endpoints Sync relies on that were removed, uses Redgifs' temporary token, and fixes video ids taken from links like redgifs.com/watch/{id}/?92. Works on Sync and Sync Pro. Turn off Patcheddit's "Fix Redgifs API" when using this one: both change the same code. |  |
| [Fix load more comments](#fix-load-more-comments) | Fixes "Error loading more comments" since Reddit blocked /api/morechildren for third-party clients. The missing comments are loaded from /comments instead. At the bottom of very large threads Reddit's 500 comment limit can still leave some out. |  |
| [Unlock Sync Ultra](#unlock-sync-ultra) | Unlocks Sync Ultra Lifetime. |  |

</details>

<!-- PATCHES_END -->

All patches are enabled by default and are independent: each can be selected or deselected on its own.

### Using with Patcheddit

**Fix Redgifs API** replaces Patcheddit's patch of the same name, which fails on Sync because Redgifs removed
the `/info` endpoint Sync calls (*Error connecting to Redgifs*). Both change the same code, so turn
**Patcheddit's "Fix Redgifs API" off** when you use this one. If both are on, one of them fails to apply.

### Supported apps

| App | Package | Version |
|-----|---------|---------|
| Sync for Reddit | `com.laurencedawson.reddit_sync` | `v23.06.30-13:39` (version code 23033) |
| Sync for Reddit Pro | `com.laurencedawson.reddit_sync.pro` | `v23.06.30-13:39` (version code 23033) |

This is Sync's final release. The free and Pro builds have the same code but different obfuscated names
(for example `y8/q.b()` vs `j8/q.c()`), so the patches find their targets by the source file names Sync keeps
(`OAuthGalleryRequest.java`, `OAuthCommentsMoreRequest.java`, `CommentsPresenterImpl.java`) plus strings and
method signatures, never by obfuscated names.

### Known limitations

- **Very large threads:** Reddit returns at most 500 comments per request. Deep in very large threads the last
  few top-level comments can fall outside that window. Sync then shows *Error loading more comments* and keeps
  the "load more" row, instead of spinning or removing it.
- These patches only change *where* the two requests go. Sync still needs working API access (the patcheddit
  **Spoof client** patch with your own client ID), or no request succeeds.

## 📲 Applying the patches with ReVanced

You need your own copy of Sync for Reddit or Sync for Reddit Pro `v23.06.30-13:39`. This project doesn't
provide APKs.

### ReVanced Manager (on your phone)

1. Add this repository as a patch bundle: in ReVanced Manager open the patch bundles screen, tap **+**, choose
   **Remote** and enter `https://raw.githubusercontent.com/trulow/sync-reddit-fixes/main/patches-bundle.json`.
   The manager reads the latest release from that file and can update the bundle automatically. (Requires a
   GitHub release, see *Releasing* below.) To use a bundle you built yourself, copy `patches-<version>.rvp` to
   the phone and add it as a **Local** patch bundle instead.
2. Add the patcheddit bundle as well for **Spoof client** and its other Sync patches.
3. Select your **original, unpatched** Sync APK, then select the patches you want from this bundle along with
   the patcheddit patches you use. If you select **Fix Redgifs API** here, turn off patcheddit's patch of the
   same name.
4. Patch and install.

### ReVanced CLI

Download the latest `revanced-cli-<version>-all.jar` from
[ReVanced/revanced-cli releases](https://github.com/ReVanced/revanced-cli/releases) and run:

```bash
java -jar revanced-cli-all.jar patch -p patches-<version>.rvp -p patcheddit.rvp -o sync-patched.apk sync.apk
```

To apply only some of the patches, add `--exclusive` and `-e "<patch name>"` for each one you want.
Use `list-patches --with-packages --with-versions patches-<version>.rvp` to see the patches with their supported
packages and versions.

## 🛠️ Building

Requirements:

- JDK 17 or newer. Android Studio's bundled runtime works:
  `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`
- The Android SDK with **platform 36** (`ANDROID_HOME` set, or `sdk.dir` in `local.properties`).
- A GitHub token with the `read:packages` scope, since ReVanced publishes its Gradle plugin and patcher to
  GitHub Packages. Add it to `~/.gradle/gradle.properties`:

  ```properties
  # ReVanced patches Gradle plugin
  gpr.user = <your-github-username>
  gpr.key = <token>
  # ReVanced Patcher
  githubPackagesUsername = <your-github-username>
  githubPackagesPassword = <token>
  ```

  With the GitHub CLI, `gh auth refresh -h github.com -s read:packages` adds the scope and `gh auth token`
  prints the token.

Then build:

```bash
./gradlew :patches:buildAndroid
```

The bundle is written to `patches/build/libs/patches-<version>.rvp`. `buildAndroid` is required: it adds the
DEX that ReVanced Manager needs on Android. `./gradlew :patches:generatePatchesList` regenerates
`patches-list.json`.

### Project layout

The layout follows [patcheddit](https://github.com/wchill/patcheddit): one extension module per app under
`extensions/`, and the patches for an app under its own `syncforreddit` package.

```
extensions/syncforreddit/                    Extension module, built to extensions/syncforreddit.rve
  build.gradle.kts
  src/main/java/app/syncfixes/extension/syncforreddit/
    FixLoadMoreCommentsPatch.java            Builds the /comments request, converts its reply
    FixGalleriesPatch.java                   Rewrites the gallery URL, unwraps its reply
    Responses.java                           Shared Volley response helpers
  stub/                                      Compile-only Volley stubs (Sync ships Volley unobfuscated)
patches/src/main/kotlin/app/syncfixes/patches/syncforreddit/
  shared/Constants.kt                        Supported apps, versions, shared names
  shared/Matching.kt                         Finds methods by source file name, signature and strings
  fix/comments/Fingerprints.kt               Targets for "load more comments"
  fix/comments/FixLoadMoreCommentsPatch.kt   Bytecode hooks for "load more comments"
  fix/gallery/Fingerprints.kt                Targets for galleries
  fix/gallery/FixGalleriesPatch.kt           Bytecode hooks for galleries
```

The Kotlin patches only insert calls; the logic lives in the Java extension, which is merged into the app.

### How the fixes work

**Fix Reddit galleries** hooks `OAuthGalleryRequest`:

1. Its static URL builder returns `https://oauth.reddit.com/comments/{id}.json?limit=1` instead of
   `/api/info.json?id=t3_{id}`. This URL is also Volley's cache key, which Sync uses to remember the gallery
   position, so both stay consistent.
2. `parseNetworkResponse()` first replaces the response with one whose body is the first element of the
   `/comments` reply: the same `Listing` `/api/info` returned. Sync's parser and its cache handling are unchanged.

**Fix load more comments** hooks three places:

1. `CommentsPresenterImpl`'s "load more" handler passes the "more" row to the extension, which reads its
   `parent_id` (the request itself isn't given the parent).
2. `OAuthCommentsMoreRequest`'s constructor passes the post id, child ids and sort. Right before the super
   constructor, the `api/morechildren` POST is replaced by a GET of
   `/comments/{post}.json` (top level, `depth=2`) or `/comments/{post}/_/{parent}.json` (nested, `depth=10`),
   with `limit=500`, the sort, and a key to the child id list (hundreds of ids would make the URL too long).
3. `parseNetworkResponse()` turns the comment tree into morechildren's flat `{"json":{"data":{"things":[...]}}}`:
   the requested comments and everything under them, in pre-order (parents before replies, which Sync uses for
   indentation), with "more" items for deeper replies. If nothing could be loaded it returns Reddit's error
   shape, so Sync shows its normal error and keeps the row.

**Fix Redgifs API** (adapted from [KingOfPoptart/patcheddit](https://github.com/KingOfPoptart/patcheddit),
[wchill/patcheddit#100](https://github.com/wchill/patcheddit/pull/100)) installs an OkHttp interceptor on the
client behind Sync's Volley requests. The patch finds that client by following the getter Volley's
`BasicNetwork` calls to the field it returns, so it hooks the same client on Sync and Sync Pro.

1. Sync asks `/v2/oauth/client` for a token, then `/info` for its IP address; Redgifs removed both. The
   interceptor answers both itself, using Redgifs' current temporary token (`/v2/auth/temporary`), cached per
   user agent.
2. `/v2/gifs/{id}` goes out with that token and Sync's own user agent. If Redgifs rejects it (tokens are tied
   to the IP address that requested them, so switching networks invalidates one), the request is retried once
   with a new token.
3. `LinkHandler.getGfycatId` strips the query before the trailing slash, so links like
   `redgifs.com/watch/{id}/?92` no longer give an empty id.

## 🚀 Releasing

Commit to `dev` with semantic commit messages (`feat:`, `fix:`, `chore:`), and `release.yml` builds and
publishes releases. Merge `dev` into `main` for a stable release. Each release attaches
`patches-<version>.rvp` and updates [patches-bundle.json](patches-bundle.json), the file ReVanced Manager reads
to find the latest release. The release workflow rewrites the bundle URL in this README to the repository it
runs in; update `source` in [patches/build.gradle.kts](patches/build.gradle.kts) if the repository isn't
`trulow/sync-reddit-fixes`.

## ⚠️ Disclaimer

Not affiliated with or endorsed by Laurence Dawson, Sync for Reddit, Reddit, ReVanced or the Morphe project. This
repository contains only original patch code, no APKs and no code from Sync. You are responsible for using your
own legally obtained copy of the app and for following the terms of the services it connects to.

## 📜 License

Sync for Reddit Fixes are licensed under the [GNU General Public License v3.0](LICENSE), with the additional
conditions under section 7 in the [NOTICE](NOTICE).
