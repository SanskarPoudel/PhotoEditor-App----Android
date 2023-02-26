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


JNIEXPORT void JNICALL
Java_com_example_photoeditor_MainActivity_negative(JNIEnv *env, jclass clazz, jintArray pixels,
                                                   jint width, jint height) {
    jint *c_pixels = (*env)->GetIntArrayElements(env, pixels, NULL);
    if (c_pixels == NULL) {
        return;
    }

    for (int i = 0; i < width * height; i++) {
        jint pixel = c_pixels[i];
        jint alpha = (pixel >> 24) & 0xff;
        jint red = (pixel >> 16) & 0xff;
        jint green = (pixel >> 8) & 0xff;
        jint blue = pixel & 0xff;

        red = 255 - red;
        green = 255 - green;
        blue = 255 - blue;

        pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
        c_pixels[i] = pixel;
    }

    (*env)->ReleaseIntArrayElements(env, pixels, c_pixels, 0);
}
