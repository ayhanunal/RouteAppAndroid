[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)
[![Android-Studio](https://img.shields.io/badge/Android%20Studio-4.2+-orange.svg?style=flat)](https://developer.android.com/studio/)
![Language](https://img.shields.io/badge/language-Kotlin-orange.svg)

<p> Play Store: https://play.google.com/store/apps/details?id=com.ayhanunal.routeapp </p>

<h1 align="center"> Route App </h1>

<p align="center">
You can plan travel with Route. You can sort the places you have saved by distance and specify their priorities. You can also get directions with navigation. You can record your important memories of your trip with photographs. Moreover, all data is stored in the room you have created specially for you and cannot be accessed by anyone else.
</p>

### Gradle 
Add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "androidx.navigation:navigation-fragment-ktx:2.3.2"
    implementation "androidx.navigation:navigation-ui-ktx:2.3.2"

    implementation 'com.google.android.libraries.places:places:1.1.0'

    implementation 'com.github.mukeshsolanki:Google-Places-AutoComplete-EditText:0.0.8'

    implementation platform('com.google.firebase:firebase-bom:28.1.0')
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.6'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.6'

    implementation 'com.google.android.gms:play-services-location:15.0.1'

    implementation 'com.google.firebase:firebase-storage'

    implementation 'com.github.bumptech.glide:glide:4.11.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'
}
```

## Usage

<p align="center">
  To use the app, you must log into a room. If you are not a member of a room, you can create a new room with the 'Create Room' link.
  After logging into a room, the saved places will appear.
  Saved places are sorted from near to far from the user's current location according to the location of the registered address.
  When you swipe left on any of the lines, you can get directions to the registered address. You can also delete or disable the recording.
</p>

<p align="center">
  <img src='https://github.com/ayhanunal/RouteAppAndroid/blob/main/app_ss_default/1.png' width=300 heihgt=300> 
  <img src='https://github.com/ayhanunal/RouteAppAndroid/blob/main/app_ss_default/2.png' width=300 heihgt=300>
  <img src='https://github.com/ayhanunal/RouteAppAndroid/blob/main/app_ss_default/3.png' width=300 heihgt=300>
</p>

<p align="center">
You can add a new address to your list with the add new record button above.
As you type the name of the place you want to add, Edit Text will be completed automatically.
Then you can save by entering name, description, priority and price information. After this process, the Latitude and Longitude information of the address will be recorded in the database. If you want, you can also see the total cost information by clicking the 'info' button on the homepage.
</p>

<p align="center">
  <img src='https://github.com/ayhanunal/RouteAppAndroid/blob/main/app_ss_default/4.png' width=300 heihgt=300> 
  <img src='https://github.com/ayhanunal/RouteAppAndroid/blob/main/app_ss_default/5.png' width=300 heihgt=300>
  <img src='https://github.com/ayhanunal/RouteAppAndroid/blob/main/app_ss_default/6.png' width=300 heihgt=300>
</p>

<p align="center">
Finally, on the 'memories' page, you can save the moments you do not want to forget during your trip together with the photo.
</p>

<p align="center">
  <img src='https://github.com/ayhanunal/RouteAppAndroid/blob/main/app_ss_default/7.png' width=300 heihgt=300> 
</p>

