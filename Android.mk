LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# Build all java files in the java subdirectory
LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_MODULE_TAGS := optional
LOCAL_SDK_VERSION := current
LOCAL_CERTIFICATE := shared

# List of static libraries to include in the package
LOCAL_STATIC_JAVA_LIBRARIES := android.support.v13

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := android.support.v13.app:libs/android-support-v13.jar

# Name of the APK to build
LOCAL_PACKAGE_NAME := net.micode.fileexplorer

include $(BUILD_PACKAGE)
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := android.support.v13:libs/android-support-v13.jar

include $(BUILD_MULTI_PREBUILT)

