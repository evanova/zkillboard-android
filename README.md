# zkillboard-android
Eve Online ZKillboard Android Application


### Sideload Me

#### Have the Android SDK up to date on your computer.

#### Install the debug APK

Note: the current APK is available under app\download\

`adb install app\download\app-debug.apk`

### Build Me

#### Build with Gradle

`gradlew -x test -x lint`

#### Unload the existing APK if relevant 

`adb uninstall org.devfleet.zkillboard.zkilla`

#### Load the debug APK to your device 

`adb install app\build\outputs\apk\debug\app-debug.apk`

#### Wait for it.
