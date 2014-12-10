/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 jc0mm0n
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.j1024.mcommon.network;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;
import com.j1024.mcommon.util.L;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by jc0mm0n on 10/22/14.
 */
public class Http {
    private Http() {
    }

    private static final String TAG = "Http";
    private static OkHttpClient client = new OkHttpClient();
    static{
        client.setCache(null);
    }
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void cancel(String url) {
        client.cancel(url);
    }

    public static void get(final String url, final StringCallback callback) {
        L.d(TAG, "Do GET --> "+url);
        final Request request = new Request.Builder().url(url).tag(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(request, ResultCode.NetworkException, e);
                    }
                });
            }

            @Override
            public void onResponse(Response response) {
                try {
                    final String responseString = response.body().string();
                    L.v(TAG, "["+url+"] response:"+responseString);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(responseString);
                        }
                    });
                } catch (final IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(request, ResultCode.NetworkException, e);
                        }
                    });
                } catch (final JsonSyntaxException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(request, ResultCode.ServerException, e);
                        }
                    });
                }
            }
        });
    }

    public static <T> void get(final String url, final JsonArrayCallback<T> jsonArrayCallback) {
        L.d(TAG, "Do GET --> " + url);
        final Request request = new Request.Builder().url(url).tag(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        jsonArrayCallback.onFailure(request, ResultCode.NetworkException, e);
                    }
                });
            }

            @Override
            public void onResponse(Response response) {
                try {
                    final String responseString = response.body().string();
                    L.v(TAG, "["+url+"] response:"+responseString);
                    Gson gson = new Gson();
                    Type token = $Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, jsonArrayCallback.getGenericType());
                    final List<T> list = gson.fromJson(responseString, token);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            jsonArrayCallback.onResponse(list);
                        }
                    });
                } catch (final IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            jsonArrayCallback.onFailure(request, ResultCode.NetworkException, e);
                        }
                    });
                } catch (final JsonSyntaxException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            jsonArrayCallback.onFailure(request, ResultCode.ServerException, e);
                        }
                    });
                }
            }
        });
    }

    public static <T> void get(final String url, final JsonObjectCallback<T> jsonObjectCallback) {
        L.d(TAG, "Do GET --> "+url);
        final Request request = new Request.Builder().url(url).tag(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        jsonObjectCallback.onFailure(request, ResultCode.NetworkException, e);
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    final String responseString = response.body().string();
                    L.v(TAG, "["+url+"] response:"+responseString);
                    Gson gson = new Gson();
                    Class<T> clazz = jsonObjectCallback.getGenericType();
                    final T respObj = gson.fromJson(responseString, clazz);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            jsonObjectCallback.onResponse(respObj);
                        }
                    });
                } catch (final IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            jsonObjectCallback.onFailure(request, ResultCode.NetworkException, e);
                        }
                    });
                } catch (final JsonSyntaxException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            jsonObjectCallback.onFailure(request, ResultCode.ServerException, e);
                        }
                    });
                }
            }
        });
    }

    public static interface StringCallback {
        public void onFailure(Request request, ResultCode code, Exception e);

        public void onResponse(String result);
    }

    public static interface JsonArrayCallback<T> {
        public Class<T> getGenericType();

        public void onFailure(Request request, ResultCode code, Exception e);

        public void onResponse(List<T> resultList);
    }

    public abstract interface JsonObjectCallback<T> {
        public Class<T> getGenericType();

        public void onFailure(Request request, ResultCode code, Exception e);

        public void onResponse(T result);
    }
}
