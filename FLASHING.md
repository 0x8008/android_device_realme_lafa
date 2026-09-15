# Flash Evolution X on realme GT 8 Pro

For **RMX5200 / lafa**, with an **unlocked bootloader**. Other variants are
unverified. This ROM includes firmware from RMX5200_16.0.9.402(CN01).
Use current [Google platform-tools](https://developer.android.com/tools/releases/platform-tools).

Download the ROM ZIP listed in [RELEASE.md](RELEASE.md). For a first install, also download its recovery
image and `super-reset-lafa-0x467000000-sparse.img`. Rename the ROM to `rom.zip`
and the recovery to `recovery.img`; put the files beside `adb` and `fastboot`.
Open a terminal in that folder.

## Updating an existing lafa Evolution X installation

1. Run `adb reboot recovery`.
2. Select **Apply update → Apply from ADB**, then run:
   ```sh
   adb sideload rom.zip
   ```
3. Wait for recovery to report success, decline extra packages, and select
   **Reboot system now**. **Do not format data or reset super for an update.**

## First install from stock or another ROM — erases your data

Back up everything first. Keep the bootloader unlocked and let the installer
manage slots. Stop if any command fails.

1. Enable USB debugging in Android, connect the phone, and install recovery:
   ```sh
   adb reboot bootloader
   fastboot flash recovery recovery.img
   fastboot reboot recovery
   ```
2. In recovery, select **Factory reset → Format data/factory reset**.
3. Reset the old system layout:
   ```sh
   adb reboot fastboot
   fastboot getvar partition-size:super
   ```
   Continue only if the size is **`0x467000000`**, then run:
   ```sh
   fastboot flash super super-reset-lafa-0x467000000-sparse.img
   fastboot reboot recovery
   ```
4. Select **Apply update → Apply from ADB**, then run:
   ```sh
   adb sideload rom.zip
   ```
5.  **Only if the previous step failed;** at recovery's main menu, paste this one-time command to prepare sideload:
   ```sh
   adb shell "(grep -q ' /metadata ' /proc/mounts || mount -t f2fs /dev/block/by-name/metadata /metadata) && mkdir -p /metadata/ota/snapshots && chown 0:1000 /metadata/ota /metadata/ota/snapshots && chmod 0750 /metadata/ota /metadata/ota/snapshots && restorecon -RF /metadata/ota && sync"
   ```
   and retry step 4.

6. When recovery reports success, decline extra packages and select
   **Reboot system now**. Google apps are already included.

The PC may stop around **47%**; the recovery screen must report success.
If installation fails, stay in recovery and save `adb pull /tmp/recovery.log`.
The initial recovery uses the existing boot chain; if it will not boot, stop
and report the current stock version instead of flashing unrelated images.

Default ADB stays enabled without host authorization for debugging.
