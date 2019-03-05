package com.zhao.libeventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.zhao.libeventbus.annotation.Subscribe;
import com.zhao.libeventbus.bean.ThreadMode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBus {
    private static final String TAG = EventBus.class.getSimpleName();
    private static EventBus mInstance;
    private HashMap<Object, List<Method>> cacheMap;
    private Handler mHandler;
    private ExecutorService mExecutorService;

    private EventBus() {
    }

    {
        cacheMap = new HashMap();
        mHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newCachedThreadPool();
    }

    public static EventBus getDefault() {
        if (mInstance == null) {
            synchronized (EventBus.class) {
                if (mInstance == null) {
                    mInstance = new EventBus();
                }
            }
        }
        return mInstance;
    }

    public void register(Object object) {
        List<Method> methodList = cacheMap.get(object);
        if (methodList == null) {
            methodList = findMethod(object);
            cacheMap.put(object, methodList);
        }
    }

    private List<Method> findMethod(Object object) {
        List<Method> methodList = new ArrayList<>();
        Class clz = object.getClass();
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe == null) {
                continue;
            }
            Class<?>[] types = method.getParameterTypes();
            if (types.length != 1) {
                Log.e(TAG, "EventBus只能接受一个参数");
            }
            methodList.add(method);
        }
        return methodList;
    }

    public void post(final Object event) {
        Set<Object> objectSet = cacheMap.keySet();
        Iterator<Object> iterator = objectSet.iterator();
        do {
            final Object object = iterator.next();
            List<Method> methodList = cacheMap.get(object);
            for (final Method method : methodList) {
                if (method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                    Subscribe subscribe = method.getAnnotation(Subscribe.class);
                    if (subscribe.threadMode() == ThreadMode.MAIN) {
                        //主-主
                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            invoke(method, object, event);
                        } else {
                            //子-主
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(method, object, event);
                                }
                            });
                        }
                    } else {
                        //主-子
                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            mExecutorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(method, object, event);
                                }
                            });
                        } else {
                            //子-子
                            invoke(method, object, event);
                        }

                    }

                }
            }
        } while (iterator.hasNext());
    }

    private void invoke(Method method, Object object, Object event) {
        try {
            method.invoke(object, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
