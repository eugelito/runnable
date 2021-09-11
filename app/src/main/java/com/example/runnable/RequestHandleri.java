package com.example.runnable;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class RequestHandleri {

    private static RequestHandleri mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private RequestHandleri(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }


    public static synchronized RequestHandleri getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestHandleri(context);
        }
        return mInstance;
    }

    /*ET - Singleton pattern which creates an instance of the request queue object,
      Live while the app is running
    */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
                /* ET - getApplicationContext() is key, it keeps you from leaking the
                  Activity or BroadcastReceiver if someone passes one in. */
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    //ET - Method will add the request object to the request queue
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
