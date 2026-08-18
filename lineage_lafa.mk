#
# Copyright (C) 2026 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from those products. Most specific first.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit_only.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Inherit from lafa device
$(call inherit-product, device/realme/lafa/device.mk)

# Inherit some common Lineage stuff.
$(call inherit-product, vendor/lineage/config/common_full_phone.mk)

PRODUCT_NAME := lineage_lafa
PRODUCT_DEVICE := lafa
PRODUCT_MANUFACTURER := realme
PRODUCT_BRAND := realme
PRODUCT_MODEL := RMX5200

PRODUCT_GMS_CLIENTID_BASE := android-realme

PRODUCT_BUILD_PROP_OVERRIDES += \
    BuildDesc="RMX5200-user 16 BP2A.250605.015 B.202607180226 release-keys" \
    BuildFingerprint=realme/RMX5200/RE6030L1:16/BP2A.250605.015/B.202607180226:user/release-keys \
    DeviceName=RE6030L1 \
    DeviceProduct=RMX5200 \
    SystemDevice=RE6030L1 \
    SystemName=RMX5200

PRODUCT_PRODUCT_PROPERTIES += \
    ro.vendor.oplus.market.name=realme GT 8 Pro \
    ro.vendor.oplus.market.enname=realme GT 8 Pro
