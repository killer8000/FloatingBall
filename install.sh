#!/usr/bin/env bash
./gradlew build
#app-release-unsigned
#app-debug.apk
adb push /Users/ndh/Documents/workspace/FloatingBall/app/build/outputs/apk/app-release.apk /data/local/tmp/com.example.ndh.floatingball
adb shell pm install -r "/data/local/tmp/com.example.ndh.floatingball"