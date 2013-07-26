#include <gsamsungiap.h>
#include <jni.h>
#include <stdlib.h>
#include <glog.h>

extern "C" {
JavaVM *g_getJavaVM();
JNIEnv *g_getJNIEnv();
}

class GSamsungIAP
{
public:
	GSamsungIAP()
	{
		gid_ = g_NextId();

		JNIEnv *env = g_getJNIEnv();

		jclass localClass = env->FindClass("com/giderosmobile/android/plugins/samsung/GSamsungIAP");
		cls_ = (jclass)env->NewGlobalRef(localClass);
		env->DeleteLocalRef(localClass);

		env->CallStaticVoidMethod(cls_, env->GetStaticMethodID(cls_, "init", "(J)V"), (jlong)this);
	}

	~GSamsungIAP()
	{
		JNIEnv *env = g_getJNIEnv();

		env->CallStaticVoidMethod(cls_, env->GetStaticMethodID(cls_, "cleanup", "()V"));

		env->DeleteGlobalRef(cls_);

		gevent_RemoveEventsWithGid(gid_);
	}

	bool purchaseItem(const char *itemGroupId, const char* itemId)
	{
		JNIEnv *env = g_getJNIEnv();

		jstring jitemGroupId = env->NewStringUTF(itemGroupId);
		jstring jitemId = env->NewStringUTF(itemId);

		jboolean result = env->CallStaticBooleanMethod(cls_, env->GetStaticMethodID(cls_, "purchaseItem", "(Ljava/lang/String;Ljava/lang/String;)Z"), jitemGroupId, jitemId);

		env->DeleteLocalRef(jitemGroupId);
		env->DeleteLocalRef(jitemId);

		return result;
	}

	void onPurchaseStateChange(jint purchaseState, jstring jitemGroupId, jstring jitemId)
	{
		JNIEnv *env = g_getJNIEnv();

		const char *itemGroupId = env->GetStringUTFChars(jitemGroupId, NULL);
		const char *itemId = env->GetStringUTFChars(jitemId, NULL);


		gsamsungiap_PurchaseStateChangeEvent *event = (gsamsungiap_PurchaseStateChangeEvent*)gevent_CreateEventStruct2(
			sizeof(gsamsungiap_PurchaseStateChangeEvent),
			offsetof(gsamsungiap_PurchaseStateChangeEvent, itemGroupId), itemGroupId,
			offsetof(gsamsungiap_PurchaseStateChangeEvent, itemId), itemId);

		event->purchaseState = purchaseState;

		env->ReleaseStringUTFChars(jitemGroupId, itemGroupId);
		env->ReleaseStringUTFChars(jitemId, itemId);

		gevent_EnqueueEvent(gid_, callback_s, GSAMSUNGIAP_PURCHASE_STATE_CHANGE_EVENT, event, 1, this);
	}

	g_id addCallback(gevent_Callback callback, void *udata)
	{
		return callbackList_.addCallback(callback, udata);
	}
	void removeCallback(gevent_Callback callback, void *udata)
	{
		callbackList_.removeCallback(callback, udata);
	}
	void removeCallbackWithGid(g_id gid)
	{
		callbackList_.removeCallbackWithGid(gid);
	}

private:
	static void callback_s(int type, void *event, void *udata)
	{
		((GSamsungIAP*)udata)->callback(type, event);
	}

	void callback(int type, void *event)
	{
		callbackList_.dispatchEvent(type, event);
	}

private:
	gevent_CallbackList callbackList_;

private:
	jclass cls_;
	g_id gid_;
};

extern "C" {

void Java_com_giderosmobile_android_plugins_samsung_GSamsungIAP_onPurchaseStateChange(JNIEnv *env, jclass clz, jint purchaseState, jstring itemGroupId, jstring itemId, jlong data)
{
	((GSamsungIAP*)data)->onPurchaseStateChange(purchaseState, itemGroupId, itemId);
}

}

static GSamsungIAP *s_samsungiap = NULL;

extern "C" {

void gsamsungiap_init()
{
	s_samsungiap = new GSamsungIAP;
}

void gsamsungiap_cleanup()
{
	delete s_samsungiap;
	s_samsungiap = NULL;
}

int gsamsungiap_purchaseItem(const char *itemGroupId, const char* itemId)
{
	return s_samsungiap->purchaseItem(itemGroupId,itemId);
}

g_id gsamsungiap_addCallback(gevent_Callback callback, void *udata)
{
	return s_samsungiap->addCallback(callback, udata);
}

void gsamsungiap_removeCallback(gevent_Callback callback, void *udata)
{
	s_samsungiap->removeCallback(callback, udata);
}

void gsamsungiap_removeCallbackWithGid(g_id gid)
{
	s_samsungiap->removeCallbackWithGid(gid);
}

}
