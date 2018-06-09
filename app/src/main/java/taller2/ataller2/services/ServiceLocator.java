package taller2.ataller2.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {

    private static final Map<String, Object> sServicesInstances = new HashMap<>();
    private static final Map<String, Class> sServicesImplementationsMapping = new HashMap<>();

    private static final Object sServicesInstancesLock = new Object();

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static void init(@NonNull Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Return instance of desired class or object that implement desired interface.
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends CustomService> T get(@NonNull Class<T> clazz) {
        @SuppressWarnings("ResourceType")
        T instance = (T) getService(clazz.getName(), mContext);
        return instance;
    }

    /**
     * This method allows to bind a custom service implementation to an interface.
     *
     * @param interfaceClass      interface
     * @param implementationClass class which implement interface specified in first param
     */
    public static void bindCustomServiceImplementation(@NonNull Class interfaceClass, @NonNull Class implementationClass) {
        synchronized (sServicesInstancesLock) {
            sServicesImplementationsMapping.put(interfaceClass.getName(), implementationClass);
        }
    }

    @NonNull
    private static Object getService(@NonNull String name, @Nullable Context applicationContext) {
        synchronized (sServicesInstancesLock) {
            Object o = sServicesInstances.get(name);
            if (o != null) {
                return o;
            } else {
                try {
                    Object serviceInstance;
                    final Class<?> clazz;
                    if (sServicesImplementationsMapping.containsKey(name)) {
                        clazz = sServicesImplementationsMapping.get(name);
                    } else {
                        clazz = Class.forName(name);
                    }

                    try {
                        Constructor clazzConstructor = clazz.getConstructor(Context.class);
                        serviceInstance = clazzConstructor.newInstance(applicationContext);
                    } catch (NoSuchMethodException var6) {
                        Constructor constructor = clazz.getConstructor();
                        serviceInstance = constructor.newInstance();
                    }

                    if (!(serviceInstance instanceof CustomService)) {
                        throw new IllegalArgumentException("Requested service must implement CustomService interface");
                    }
                    sServicesInstances.put(name, serviceInstance);
                    return serviceInstance;
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Requested service class was not found: " + name, e);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot initialize requested service: " + name, e);
                }
            }
        }
    }
}