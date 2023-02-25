#include <jni.h>

JNIEXPORT void JNICALL
Java_com_example_photoeditor_MainActivity_blackAndWhite(JNIEnv *env, jclass clazz, jintArray pixels,
                                                        jint width, jint height) {
    jint *colors = (*env)->GetIntArrayElements(env, pixels, NULL);

    int pixelCount = width * height;

    for (int i = 0; i < pixelCount; i++) {
        int alpha = (colors[i] >> 24) & 0xFF;
        int red = (colors[i] >> 16) & 0xFF;
        int green = (colors[i] >> 8) & 0xFF;
        int blue = colors[i] & 0xFF;

        int average = (red + green + blue) / 3;

        colors[i] = (alpha << 24) | (average << 16) | (average << 8) | average;
    }

    (*env)->ReleaseIntArrayElements(env, pixels, colors, 0);
}
