#include "gsamsungiap.h"
#include "gideros.h"
#include <map>
#include <string>
#include <vector>
#include <glog.h>

// some Lua helper functions
#ifndef abs_index
#define abs_index(L, i) ((i) > 0 || (i) <= LUA_REGISTRYINDEX ? (i) : lua_gettop(L) + (i) + 1)
#endif

static void luaL_newweaktable(lua_State *L, const char *mode)
{
	lua_newtable(L);			// create table for instance list
	lua_pushstring(L, mode);
	lua_setfield(L, -2, "__mode");	  // set as weak-value table
	lua_pushvalue(L, -1);             // duplicate table
	lua_setmetatable(L, -2);          // set itself as metatable
}

static void luaL_rawgetptr(lua_State *L, int idx, void *ptr)
{
	idx = abs_index(L, idx);
	lua_pushlightuserdata(L, ptr);
	lua_rawget(L, idx);
}

static void luaL_rawsetptr(lua_State *L, int idx, void *ptr)
{
	idx = abs_index(L, idx);
	lua_pushlightuserdata(L, ptr);
	lua_insert(L, -2);
	lua_rawset(L, idx);
}

static const char *PURCHASE_STATE_CHANGE = "purchaseStateChange";

static const char *OK = "ok";
static const char *ERROR = "error";

static const char *PURCHASED = "purchased";
static const char *CANCELED = "canceled";

static const char *INAPP = "inapp";
static const char *SUBS = "subs";

const char *responseCode2str(int responseCode)
{
	switch (responseCode)
	{
	case GSAMSUNGIAP_RESULT_OK:
		return OK;
	case GSAMSUNGIAP_RESULT_ERROR:
		return ERROR;
	};

	return "undefined";
}

const char *purchaseState2str(int purchaseState)
{
	switch (purchaseState)
	{
	case GSAMSUNGIAP_PURCHASED:
		return PURCHASED;
	case GSAMSUNGIAP_CANCELED:
		return CANCELED;
	};

	return "undefined";
}

static char keyWeak = ' ';

class SamsungIAP : public GEventDispatcherProxy
{
public:
    SamsungIAP(lua_State *L) : L(L)
    {
        gsamsungiap_init();
		gsamsungiap_addCallback(callback_s, this);
    }

    ~SamsungIAP()
    {
		gsamsungiap_removeCallback(callback_s, this);
		gsamsungiap_cleanup();
    }

    bool purchaseItem(const char *itemGroupId, const char* itemId)
	{
		return gsamsungiap_purchaseItem(itemGroupId,itemId);
	}

private:
	static void callback_s(int type, void *event, void *udata)
	{
		static_cast<SamsungIAP*>(udata)->callback(type, event);
	}

	void callback(int type, void *event)
	{
        dispatchEvent(type, event);
	}

	void dispatchEvent(int type, void *event)
	{
        luaL_rawgetptr(L, LUA_REGISTRYINDEX, &keyWeak);
        luaL_rawgetptr(L, -1, this);

        if (lua_isnil(L, -1))
        {
            lua_pop(L, 2);
            return;
        }

        lua_getfield(L, -1, "dispatchEvent");

        lua_pushvalue(L, -2);

        lua_getglobal(L, "Event");
        lua_getfield(L, -1, "new");
        lua_remove(L, -2);

        switch (type)
        {
            case GSAMSUNGIAP_PURCHASE_STATE_CHANGE_EVENT:
                lua_pushstring(L, PURCHASE_STATE_CHANGE);
                break;
        }

        lua_call(L, 1, 1);

        if (type == GSAMSUNGIAP_PURCHASE_STATE_CHANGE_EVENT)
		{
            gsamsungiap_PurchaseStateChangeEvent *event2 = (gsamsungiap_PurchaseStateChangeEvent*)event;

			lua_pushstring(L, purchaseState2str(event2->purchaseState));
			lua_setfield(L, -2, "purchaseState");

			lua_pushstring(L, event2->itemGroupId);
			lua_setfield(L, -2, "itemGroupId");

			lua_pushstring(L, event2->itemId);
			lua_setfield(L, -2, "itemId");
		}

		lua_call(L, 2, 0);

		lua_pop(L, 2);
	}

private:
    lua_State *L;
    bool initialized_;
};

static int destruct(lua_State* L)
{
	void *ptr = *(void**)lua_touserdata(L, 1);
	GReferenced* object = static_cast<GReferenced*>(ptr);
	SamsungIAP *samsungiap = static_cast<SamsungIAP*>(object->proxy());

	samsungiap->unref();

	return 0;
}

static SamsungIAP *getInstance(lua_State* L, int index)
{
	GReferenced *object = static_cast<GReferenced*>(g_getInstance(L, "SamsungIAP", index));
	SamsungIAP *samsungiap = static_cast<SamsungIAP*>(object->proxy());

	return samsungiap;
}

static int purchaseItem(lua_State *L)
{
    SamsungIAP *samsungiap = getInstance(L, 1);

    const char *itemGroupId = luaL_checkstring(L, 2);
    const char *itemId = luaL_checkstring(L, 3);

    lua_pushboolean(L, samsungiap->purchaseItem(itemGroupId, itemId));

    return 1;
}

static int loader(lua_State *L)
{
	const luaL_Reg functionlist[] = {
        {"purchaseItem", purchaseItem},
		{NULL, NULL},
	};

    g_createClass(L, "SamsungIAP", "EventDispatcher", NULL, destruct, functionlist);

	// create a weak table in LUA_REGISTRYINDEX that can be accessed with the address of keyWeak
    luaL_newweaktable(L, "v");
    luaL_rawsetptr(L, LUA_REGISTRYINDEX, &keyWeak);

	lua_getglobal(L, "SamsungIAP");
	lua_pushstring(L, OK);
	lua_setfield(L, -2, "OK");
	lua_pushstring(L, ERROR);
	lua_setfield(L, -2, "ERROR");
	lua_pushstring(L, PURCHASED);
	lua_setfield(L, -2, "PURCHASED");
	lua_pushstring(L, CANCELED);
	lua_setfield(L, -2, "CANCELED");
	lua_pushstring(L, INAPP);
	lua_setfield(L, -2, "INAPP");
	lua_pushstring(L, SUBS);
	lua_setfield(L, -2, "SUBS");
	lua_pop(L, 1);

	lua_getglobal(L, "Event");
	lua_pushstring(L, PURCHASE_STATE_CHANGE);
	lua_setfield(L, -2, "PURCHASE_STATE_CHANGE");
	lua_pop(L, 1);

    SamsungIAP *samsungiap = new SamsungIAP(L);
	g_pushInstance(L, "SamsungIAP", samsungiap->object());

	luaL_rawgetptr(L, LUA_REGISTRYINDEX, &keyWeak);
	lua_pushvalue(L, -2);
	luaL_rawsetptr(L, -2, samsungiap);
	lua_pop(L, 1);

	lua_pushvalue(L, -1);
	lua_setglobal(L, "samsungiap");

    return 1;
}

static void g_initializePlugin(lua_State *L)
{
    lua_getglobal(L, "package");
	lua_getfield(L, -1, "preload");

	lua_pushcfunction(L, loader);
	lua_setfield(L, -2, "samsungiap");

	lua_pop(L, 2);
}

static void g_deinitializePlugin(lua_State *L)
{

}

REGISTER_PLUGIN("Samsung IAP", "1.0")
