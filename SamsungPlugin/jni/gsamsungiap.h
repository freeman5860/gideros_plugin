#ifndef GSAMSUNG_IAP
#define GSAMSUNG_IAP

#include <gglobal.h>
#include <gevent.h>

enum{
    GSAMSUNGIAP_RESULT_OK = 0,
    GSAMSUNGIAP_USER_CANCELED = 1,
    GSAMSUNGIAP_RESULT_ERROR = 2,
};

enum{
    GSAMSUNGIAP_PURCHASED = 0,
    GSAMSUNGIAP_CANCELED = 1,
};

enum{
    GSAMSUNGIAP_PURCHASE_STATE_CHANGE_EVENT,
};

typedef struct gsamsungiap_PurchaseStateChangeEvent{
    int purchaseState;
    const char * itemGroupId;
    const char * itemId;
}gsamsungiap_PurchaseStateChangeEvent;

#ifdef __cplusplus
extern "C" {
#endif

G_API void gsamsungiap_init();
G_API void gsamsungiap_cleanup();

G_API int gsamsungiap_purchaseItem(const char *itemGroupId, const char* itemId);

G_API g_id gsamsungiap_addCallback(gevent_Callback callback, void *udata);
G_API void gsamsungiap_removeCallback(gevent_Callback callback, void *udata);
G_API void gsamsungiap_removeCallbackWithGid(g_id gid);

#ifdef __cplusplus
}
#endif

#endif
