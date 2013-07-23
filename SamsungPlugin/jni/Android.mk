LOCAL_PATH := $(call my-dir)
 
#
# Gideros Shared Library
#
include $(CLEAR_VARS)
 
LOCAL_MODULE            := gideros
LOCAL_SRC_FILES         := ../../libs/$(TARGET_ARCH_ABI)/libgideros.so
 
include $(PREBUILT_SHARED_LIBRARY)
 
#
# Plugin
#
include $(CLEAR_VARS)
 
LOCAL_MODULE           := samsungiap
LOCAL_ARM_MODE         := arm
LOCAL_CFLAGS           := -O2
LOCAL_C_INCLUDES       += $(LOCAL_PATH)/../gideros/ $(LOCAL_PATH)/
LOCAL_SRC_FILES        := samsungiap.cpp
LOCAL_LDLIBS           := -ldl -llog
LOCAL_SHARED_LIBRARIES := gideros
 
include $(BUILD_SHARED_LIBRARY)