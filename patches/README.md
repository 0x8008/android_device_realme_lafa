# Shared source changes for Evolution X cnb

These changes reproduce the shared-source configuration used for the
September 14, 2026 `wallpaperfix1` build. Start with the clean dependency
revisions in [lafa.xml](../manifests/lafa.xml). Run commands from the Android
source root after syncing and hydrating Git LFS files.

## Wi-Fi Display audio dependency

Apply these two existing upstream commits. The vendor commit includes
`libwfdaac_vendor.so` and its build rules; the binary stays in the vendor
repository.

```bash
(
    set -e
    git -C device/oneplus/sm8850-common fetch https://github.com/OnePlus-SM8850-Development/android_device_oneplus_sm8850-common.git 82974b692995c47a3fa882b00ca1a3a533095e4d
    git -C device/oneplus/sm8850-common cherry-pick 82974b692995c47a3fa882b00ca1a3a533095e4d
    git -C vendor/oneplus/sm8850-common fetch https://github.com/OnePlus-SM8850-Development/proprietary_vendor_oneplus_sm8850-common.git 65361dde98dc88151e1098a72122aec89b96affc
    git -C vendor/oneplus/sm8850-common cherry-pick 65361dde98dc88151e1098a72122aec89b96affc
)
```

Skip a cherry-pick if that exact change is already present. Expected encoder
SHA-256: `9df3a3da360a479799db9e755eeaa5ae2161e4ae194121db3929ac2147131b46`.

## Local compatibility patches

| Patch | Purpose |
| --- | --- |
| `0001` | Remove duplicate OPlus powercap policy already supplied by Qualcomm policy. |
| `0002`–`0004` | Select stock audio, AGM and PAL components when `lafa.use_stock_hal` is true. |
| `0005` | Select stock display components and their matching init/VINTF files. |
| `0006` | Remove the Android 16 Flex clock that crashes the Android 17 wallpaper picker. |

The stock-HAL switch defaults to the source modules for other devices.
Powercap and clock patches affect their shared repositories and must be
reviewed if those repositories are updated or used for other products.

[series](series) lists each project, its base revision and its patch. The
following commands check every base and patch before applying any patch;
patches already applied to those same revisions are accepted:

```bash
(
    set -eu
    lafa_patches="$PWD/device/realme/lafa/patches"
    while read -r project revision patch; do
        if [ "$(git -C "$project" rev-parse HEAD)" != "$revision" ]; then
            echo "Unexpected revision: $project" >&2
            exit 1
        fi
        if ! git -C "$project" apply --reverse --check "$lafa_patches/$patch" 2>/dev/null; then
            git -C "$project" apply --check "$lafa_patches/$patch"
        fi
    done < "$lafa_patches/series"
    while read -r project revision patch; do
        if ! git -C "$project" apply --reverse --check "$lafa_patches/$patch" 2>/dev/null; then
            git -C "$project" apply "$lafa_patches/$patch"
        fi
    done < "$lafa_patches/series"
)
```

These patches remain working-tree changes. Preserve them before a later
`repo sync`. Recheck them when updating any pinned dependency.
