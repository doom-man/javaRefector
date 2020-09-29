#include <jni.h>
#include <string>
#include <android/log.h>

#define  LOGI(...) __android_log_print(ANDROID_LOG_INFO, "========= Info =========   ", __VA_ARGS__)

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, "========= Error =========   ", __VA_ARGS__)

#define  LOGD(...)  __android_log_print(ANDROID_LOG_INFO, "========= Debug =========   ", __VA_ARGS__)

#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN, "========= Warn =========   ", __VA_ARGS__)


extern "C" JNIEXPORT jstring JNICALL
Java_com_pareto_reflector_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    //获取到com/pareto/reflector/MainActivity类
    jclass mainaity = env->FindClass("com/pareto/reflector/MainActivity");
    // 在com/pareto/reflector/MainActivity 类中获取到 无参数返回值类型为List 名叫getInstalledApps 的函数。
    jmethodID PI = env->GetStaticMethodID(mainaity,"getInstalledApps" , "()Ljava/util/List;");
    //调用 com/pareto/reflector/MainActivity类的getInstalledApps方法
    jobject ret_val = env->CallStaticObjectMethod(mainaity,PI);
    // 获取到List类
    jclass list_env  = env->FindClass("java/util/List");
    //获取到list 类的get方法
    jmethodID mid_get = env->GetMethodID(list_env, "get",
                                         "(I)Ljava/lang/Object;");
    //获取到list类的size方法
    jmethodID  mid_size = env->GetMethodID(list_env , "size" , "()I");
    //调用ret_val.size() 方法
    jint size = env->CallIntMethod(ret_val,mid_size);
    for(int i = 0 ; i < size ; i++){
        //获取到PackageInfo方法
        jclass packInfo = env->FindClass("android/content/pm/PackageInfo");
        //调用ret_val.get(i) 方法
        jobject package = env->CallObjectMethod(ret_val,mid_get,i);
        //获取到PackageInfo的packageName字段
        jfieldID  PackName = env->GetFieldID(packInfo,"packageName","Ljava/lang/String;");
        //调用package->PackageName
        jstring packjstring  = (jstring)env->GetObjectField(package,PackName);
        //转换为char *
        const char *  Name = env->GetStringUTFChars(packjstring,nullptr);
        LOGD("DEBUG %s  \n",Name);
    }
    return env->NewStringUTF(hello.c_str());
}
