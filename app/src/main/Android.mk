LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional




LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

LOCAL_RESOURCE_DIR += prebuilts/sdk/current/support/v7/appcompat/res

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v13



LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat



LOCAL_SRC_FILES := $(call all-java-files-under, java)

LOCAL_PACKAGE_NAME := BluetoothScanTest
LOCAL_CERTIFICATE := platform



LOCAL_PROGUARD_ENABLED := disabled 

include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
