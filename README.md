SafeWalk
============
This project is an Android App that notifies Macalester College's Safewalk service where students want to be picked up. This documentation is specific to set up and run the code in IntelliJ Idea 13.

##Things you will need:
* The latest Android SDK
* The Google Play Services SDK: To download open the Android SDK Manager and find the Google Play Services option under the `extras` directory.
* Android v4 library (part of the SDK)
* Firebase library (This is already in the repository so there is no need to download again)

##Getting started to run the project
1. For the repository into your GitHub account, clone it to your computer and open the project
2. Open the project structure and under `Modules` select Safewalk. Then click on the `Dependencies` tab.
3. Make sure the project uses the Android SDK installed in your computer. If it's not in the dropdown menu click on `new...` and follow the instructions.
4. Import the `google-play-services_lib` module to the project: Click on the upper left `+` sign and navigate to your Android SDK directory and choose `extras > google > google-play-services > libsproject > google-play-services_lib`.
5. Still on the Safewalk module add the `google-play-services_lib` module as a dependency by clicking on the module's `+` sign and selecting `3. Module dependency...`
6. Click on the `+` sign again and select the `1. Jars or directories` option. Navigate to `android-sdk > extras > google > google-play-services > libproject > google-play-services_lib > libs > google-play-services.jar` and add the `.jar` file to the Safewalk module.
7. Again click on the `+` sign again and select the `1. Jars or directories` option. Navigate to `android-sdk > extras > android > support > v4 > android-support-v4.jar` and add the `.jar` file to the Safewalk module.
8. Click `Ok` 

These steps can be a little confusing so make sure that at the end you have the following dependencies on the Safewalk module:
* `google-play-services_lib`
* `google-play-services.jar` 
* `android-support-v4.jar` 
* `firebase-client-jvm-LATEST`

##Firebase
This project uses Firebase to allow the Safewalk workers to update in real time the availability of their service. They can use a simple website interface to change the databese that is located at in http://safewalk.firebaseio.com. If the firebase library appears red or doesn't appear at all under the dependencies of the second module follow the following steps:
1. Delete the non-working (red) dependency if it appears under the dependency tab
2. Go to the module directory on the main window of IntelliJ and go to `Safewalk > libs > firebase-client-jvm-LATEST.jar`. Right click on the `.jar` file and select `Add as library`.
