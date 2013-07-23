#include "gideros.h"
#include "lua.h"
#include "lauxlib.h"
#define LUA_LIB
#include <jni.h>
//this is for debugginh purpose
//and should be commented out before deployment
//you can log using
//__android_log_print(ANDROID_LOG_DEBUG, "tag", "Output String");
#include <android/log.h>

//some configurations of our plugin
static const char* pluginName = "samsungiap";
static const char* pluginVersion = "1.0";
static const char* javaClassName = "com/giderosmobile/android/plugins/samsung/SamsungIAP";

//Store Java Environment reference
static JNIEnv *ENV;
//Store our main class, what we will use as plugin
static jclass cls;
// store modifyString method ID
static jmethodID jPurchaseItem;

//modify string method
static int purchaseItem(lua_State *L){
	__android_log_print(ANDROID_LOG_DEBUG,"SamsungIAP","purchaseItem method called");

	// if no Java Env, exit
	if(ENV == NULL) return 0;

	//if no class,try to retrieve it
	if(cls == NULL){
		cls = ENV->FindClass(javaClassName);
		if(!cls) return 0;
	}

	//if we don't have modifyString method yet, try to retrieve it
	if(jPurchaseItem == NULL){
		jPurchaseItem = ENV->GetStaticMethodID(cls,"purchaseItem","(Ljava/lang/String;Ljava/lang/String;)V");
		if(!jPurchaseItem) return 0;
	}

	//next we get 1 argument passed from lua, which we know is a string
    const char * itemGroupId = lua_tostring(L,1);

    const char * itemId = lua_tostring(L,2);

   ENV->CallStaticObjectMethod(cls,jPurchaseItem,ENV->NewStringUTF(itemGroupId),ENV->NewStringUTF(itemId));

   // const char * modifiedString = ENV->GetStringUTFChars(jstr,0);

    //lua_pushlstring(L, modifiedString, strlen(modifiedString));

    return 1;
}

//here we register all functions we could call from lua
//lua function name as key and C function as value
static const struct luaL_Reg funcs[] = {
  { "purchaseItem",	purchaseItem },
  { NULL, NULL }//don't forget nulls at the end
};

//here we register all the C functions for lua
//so lua engine would know they exists
LUALIB_API int luaopen_plugin(lua_State *L)
{
  luaL_register(L, pluginName, funcs);
  return 1;
}

//here we do all our stuff needs to be done on initialization
static void g_initializePlugin(lua_State *L)
{
	//get java environment reference
	ENV = g_getJNIEnv();

	//get global package object
	lua_getglobal(L, "package");
	lua_getfield(L, -1, "preload");

	//put our plugin name inside with a callback to
	//registering C functions
	lua_pushcfunction(L, luaopen_plugin);
	lua_setfield(L, -2, pluginName);

	lua_pop(L, 2);
}

//and here we free everything we need to free
static void g_deinitializePlugin(lua_State *L)
{

}

//register our plugin with Gideros lib
REGISTER_PLUGIN(pluginName, pluginVersion)
