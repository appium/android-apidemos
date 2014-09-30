android-apidemos
================

A fork of Google's Android ApiDemos application, used for testing Appium

#### Building

> android update project --subprojects --target android-19 --path . --name ApiDemos
>
> ant clean debug
>
> adb install -r ./bin/ApiDemos-debug.apk

Or `npm run-script prepublish`, which does the same thing.

#### emma build for testing code coverage

> ant clean instrument
>
> adb install -r ./bin/ApiDemos-instrumented.apk
