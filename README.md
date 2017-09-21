## Tools

- https://developer.android.com/studio/index.html - Android Studio
- https://kotlinlang.org/ - Kotlin
- https://kotlinlang.org/docs/tutorials/kotlin-android.html - Kotlin Plugin
- https://github.com/cloudant/sync-android - Cloudant Sync Android

# Shopping List - Android with Kotlin and Cloudant Sync

Shopping List is an Offline First demo [Progressive Web App | hybrid mobile app | native mobile app | desktop app] built using [Android Studio](https://developer.android.com/studio/index.html), [Kotlin](https://kotlinlang.org/), and [Cloudant Sync Android](https://github.com/cloudant/sync-android).
[This app is part of a series of Offline First demo apps, each built using a different stack.](https://github.com/ibm-watson-data-lab/shopping-list) 

## Quick Start

1. Install [Android Studio](https://developer.android.com/studio/index.html) and the [Kotlin Plugin](https://kotlinlang.org/docs/tutorials/kotlin-android.html) (included in Android Studio 3.0 and up)
2. Clone this repo

`git clone https://github.com/ibm-watson-data-lab/shopping-list-kotlin-cloudant-sync`

3. In Android Studio choose **_File_** -> **_New_** -> **_Import Project..._** (if running IntelliJ IDEA choose **_Project from Existing Sources_**)
4. Select the **_shopping-list-kotlin-cloudant-sync_** folder
5. Create and start an AVD (or a 3rd party emulator like [Genymotion](https://www.genymotion.com/))
6. Run the app from Android Studio

## Troubleshooting

If a build configuration is not created for you automatically (typically the case in IntelliJ IDEA) create a new build configuration:

1. Click **_Edit Configurations_** > **_+_** > **_Android App_**
2. Specify a name
3. Choose the **_app_** module 
4. Click **_OK_**


If you are having trouble building the project you may need to create a local.properties file:

1. Create a new file called local.properties in the root of the project
2. Add the following line to the file (substitute with your Android SDK directory)

`sdk.dir=/Users/me/dev/android-sdk-macosx`