# Experimental stock lafa camera. Keep Aperture available during validation.
LAFA_CAMERA_PATH := vendor/realme/lafa-camera
include $(LAFA_CAMERA_PATH)/camera-models.mk
PRODUCT_SOONG_NAMESPACES += $(LAFA_CAMERA_PATH)
PRODUCT_PACKAGES += OplusCamera lafa-camera-compat com.oplus.camera.unit.sdk com.oplus.camera.unit.sdk.adapter
PRODUCT_COPY_FILES += \
    $(LAFA_CAMERA_PATH)/configs/lafa-camera-libraries.xml:$(TARGET_COPY_OUT_SYSTEM_EXT)/etc/permissions/lafa-camera-libraries.xml \
    $(LAFA_CAMERA_PATH)/configs/privapp-permissions-lafa-camera.xml:$(TARGET_COPY_OUT_SYSTEM_EXT)/etc/permissions/privapp-permissions-lafa-camera.xml \
    $(LAFA_CAMERA_PATH)/configs/lafa-camera-hiddenapi.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/sysconfig/lafa-camera-hiddenapi.xml

PRODUCT_PRODUCT_PROPERTIES += \
    persist.vendor.camera.privapp.list=com.oplus.camera \
    vendor.camera.aux.packagelist=com.oplus.camera,org.lineageos.aperture \
    ro.oplus.system.camera.name=com.oplus.camera

# Keep the startup flag while using foreground capture on AOSP MediaProvider.
PRODUCT_SYSTEM_EXT_PROPERTIES += \
    ro.oplus.camera.defercap.support=1 \
    ro.oplus.camera.defercap.all.quick.visible.support=0

$(call soong_config_set,camera,package_name,com.oplus.packageName)

PRODUCT_PACKAGES += \
    lafa-camera-oemlayer \
    lafa-camera-libOplusSecurity \
    lafa-camera-libiccprofile

$(call soong_config_set_bool,LAFA_CAMERA,enabled,true)

# BEGIN LAFA CAMERA NATIVE
PRODUCT_PACKAGES += \
    lafa-camera-libAPSClient-alog-jni \
    lafa-camera-libAPSClient-cmd-jni \
    lafa-camera-libAPSClient-jni \
    lafa-camera-libAncFilter_jni \
    lafa-camera-libAncHumBokeh-jni \
    lafa-camera-libAncHumanDoubleExposure-jni \
    lafa-camera-libAncHumanRetain-jni_v2 \
    lafa-camera-libAncHumanSegFigureFusion-jni \
    lafa-camera-libAncHumanVideo-jni \
    lafa-camera-libApsFaceBeautyPreviewProductJni \
    lafa-camera-libApsSuperEISPreviewJni \
    lafa-camera-libAvatarEngineRender \
    lafa-camera-libAvatarEngineRenderNative \
    lafa-camera-libCombineLut \
    lafa-camera-libCombineLutJni \
    lafa-camera-libHdrTransform-platform-jni \
    lafa-camera-libMsEffectSdk \
    lafa-camera-libOplusStringJNI \
    lafa-camera-libarcsoft_panorama_burstcapture \
    lafa-camera-libarcsoft_wideselfie \
    lafa-camera-libjni_burstpmk \
    lafa-camera-libjni_wideselfie \
    lafa-camera-liblivephoto.frc.jni \
    lafa-camera-libsingle_camera_bokeh2_native \
    lafa-camera-libsingle_camera_bokeh_native \
    lafa-camera-libst_mobile \
    lafa-camera-libst_sticker_jni \
    lafa-camera-libAPSClient-cmd-jni-extension.oplus \
    lafa-camera-libHeifEncoderWrapper \
    lafa-camera-libHeifWinBufExchg-jni \
    lafa-camera-libNativeWinBuffExchange \
    lafa-camera-libOplusStringJNI-extension.oplus \
    lafa-camera-libapssuspend-jni.oplus \
    lafa-camera-liboplusheifwriter

# Camera-specific policy and capture-job directory.
SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += $(LAFA_CAMERA_PATH)/sepolicy/private
PRODUCT_COPY_FILES += $(LAFA_CAMERA_PATH)/configs/init.lafa.camera.rc:$(TARGET_COPY_OUT_SYSTEM_EXT)/etc/init/init.lafa.camera.rc

BOARD_VENDOR_SEPOLICY_DIRS += $(LAFA_CAMERA_PATH)/sepolicy/vendor
