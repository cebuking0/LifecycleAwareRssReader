# LifecycleAwareRssReader

LifecycleAwareRssReader is simple app which 
- reads rss files
- converts them to json using web api 
- saves pojos to local SQLite database and 
- allows browsing articles thru simple list.

# Background

App has been created as part of [Coursera Android Programming Capstone] Project. 

# Versions

See [PlainRssReader] if you are interedted in using traditional approach to lifecycle controlling.

# Repository

Repositorys docs directory contains resources which accompany this documentation, otherwise repository contains solely application artifacts.

# External services and content conversions

Rss reading is implemented using http protocol and json payload. XML to Json conversion is done using [rss2json] service.

# Url's

Example url of background service. rss_url query parameter defines source of rss feed.

```
 https://api.rss2json.com/v1/api.json?rss_url=https%3A%2F%2Fwww.theguardian.com%2Finternational%2Frss
```
 
# Software Architecture 

"[Software architecture] is those decisions which are both important and hard to change." - Martin Fowler.

Development is done using [Android Studio], which is currently at 3.0. Newest versio is used 'cos it has [Java 8 support] and lambdas are pretty nice. Java 8 Streams are not used, since they doesn't exist before [API-level 24]. 

[Android devices] with API-level 24 are still not commonly in use, but [API-level 19] is safe choice for most users. I have used selected [Android architecture components], escpecially [Room] for persistence and [ViewModel] and [LiveData] for lifecycle. 

I try to keep in mind that [64K DEX limit] can possibly make my life hard as I'm not willing to invest in learning bytecode optimization tricks with [ProGuard] in this phase of development.

# Architecture diagrams

[PlantUml] is used to illustrate high level structure of application. UML diagrams are written using textual DSL. 

![UML component diagram of app]

It's easy to write uml models with [PlantUml] when you have practiced it a bit. Here's source of components diagram.

```
@startuml
package "PlainRssReader" {
  [SettingsActivity]
  [FeedActivity] 
  [RecyclerView]
  [PreferenceFragment]
}

package "AndroidServices" {
  [AndroidBrowser] 
}

cloud {
  [RssToJsonConverter]
}

cloud {
  [RssFeedProducer]
}

[FeedActivity] -up-> [SettingsActivity]:configure rss feeds url

[FeedActivity] - [RecyclerView]:browse feed
[SettingsActivity] - [PreferenceFragment]:edit url

[FeedActivity] --> [AndroidBrowser]:ask browser to show article 
[AndroidBrowser] --> [RssFeedProducer]:get single artice as html

[FeedActivity] --> [RssToJsonConverter]:get rss feed as json
[RssToJsonConverter] --> [RssFeedProducer]:get rss feed as xml
@enduml
```
Use [PlantUml testbench] if you want to experiment with given source.

# Design

"there's no way to design the software in advance. Instead, you must design your software based on its current needs, and evolve the software design as the requirements change. This process is called [evolutionary design]." - James Shore.

It's tempting to think that there's a way to know all details in advance and one could start work once plan in finished. I have taken different stance. Design evolves as I have more information, knowledge or time, and implementation follows design immendiately. Some call this iterative software development or emergent design in contrast to Big Front Up or Plan Driven Desing. Simply put: I try to defer decisions to last responsible moment, but still steer my work with current knowledge.

# Design Discussion 

## Why implementing offline browsing of articles

Motivation for buffering results for offline use can be seen at [Next Billion Users]

# Use cases

[yEd] was used to draw simple diagram of use cases.

![UML use case diagram of app]

# Classes

[Reverse engineering UML model with Andoid studio] was done with [SimpleUMLCe].

Model classes store retrieved articles

![UML class diagram of feed model] 

It wouldn't be necessary to store all attributes of rss feed, but it's done here for completeness.

# Mockups

Mockups were prepared with [marvel app]

List of items

![mockup-list]

Selected item

![mockup-item]

settings

![mockup-settings]

I did have trial versio in use, and for this reason I needed to take snapshots from screens instead of neatly exporting results to files.

# Implementation

Rss feed reading is proxied thru [rss2json] service, which converts feed to json on the fly.

Http requests are queued and processed using [Volley].

Feed url is read from preferences. When no feed is defined [Default feed] is used.

Storing and changing settings is implemented using PreferefencesFragment as defined in [use preferences]

[Gson] is used to marshall returned JSON to Plain Java Pojos.

Browsing items is implemented using RecyclerView as defined in [use recycler-view]

Lifecycle is controlled using [ViewModel] and [LiveData], which make programming model pretty simple.

[Room] is used for object-relational-mapping. [Room testing] explains how to test database operations.

# Usage of room

Read [Room tutorial] and work thru [Room codelab] and [Android lifecycles codelab] for more details. When you have got this far you might be interested to read [LiveData patterns and antipatterns] and play with [Android arhitecture blueprints]. And don't miss [Android architecture guide].

Due to limitations and interoperability issues with [Room] annotation processors [AutoValue] and [Lombok] aren't used to reduce boilerplate code of model classes, see [AutoValue issue] and [Room issue] for deeper discussion.

# Snapshots of current implementation

List of items

![list]

Selected item

![item]

settings

![settings]

change url dialog

![settings-url]

# Known bugs

User given url is not checked, and when trying to use wrong url during startup app will crash. Sorry. No safety net there. 
- This bug is pretty annoying, since after giving false URL one needs to manually clear setting of App to get it starting again.

[Coursera Android Programming Capstone]: https://www.coursera.org/learn/aadcapstone/home/welcome "Coursera Android Capstone"

[yEd]: https://www.yworks.com/products/yed "yEd diagramming software"

[use recycler-view]: https://willowtreeapps.com/ideas/android-fundamentals-working-with-the-recyclerview-adapter-and-viewholder-pattern/ "how to use recycler view, adapter and holder"

[use preferences]: http://www.cs.dartmouth.edu/~campbell/cs65/lecture12/lecture12.html "how to use preferences fragment"

[Background processing best practices]: https://developer.android.com/training/best-background.html "Background processing"

[Android studio]: https://developer.android.com/studio/preview/index.html "Android studio 3.0 RC1"

[Java 8 support]: https://developer.android.com/studio/write/java8-support.html "Android studio java 8 support"

[API-level 16]: https://developer.android.com/about/versions/android-4.1.html "Android 4.1 / Api-level 16"

[API-level 19]: https://developer.android.com/about/versions/android-4.4.html "Android 4.4 / Api-level 19"

[API-level 24]: https://developer.android.com/about/versions/nougat/android-7.0.html "Android 7.0 / Api-level 24"

[Android devices]: https://developer.android.com/about/dashboards/index.html "Android devices in use"

[Default feed]: http://rss.nytimes.com/services/xml/rss/nyt/Science.xml "Ny Times science feed"

[rss2json]: https://rss2json.com "rss xml to json converter"

[marvel app]: https://marvelapp.com/ "Mockups for iOs, Android, Web, etc."

[Software architecture]: https://kylecordes.com/2015/fowler-software-architecture

[evolutionary design]: http://www.jamesshore.com/In-the-News/Evolutionary-Design-Illustrated-Video.html "Evolutionary desing"

[PlantUml]: http://plantuml.com/ "Fantastic text based modeling tool"

[PlantUml testbench]: www.plantuml.com/plantuml/ "simple service to verify PlantUml markup"

[Reverse engineering UML model with Andoid studio]: https://stackoverflow.com/questions/17123384/how-to-generate-class-diagram-uml-on-android-studio/36823007#36823007 "Reverse engineering UML model with Andoid studio"

[SimpleUMLCe]: https://plugins.jetbrains.com/plugin/4946-simpleumlce "very simple uml diagramming tool"

[Android architecture components]: https://developer.android.com/topic/libraries/architecture/index.html "Android architecure components by Google"

[LiveData patterns and antipatterns]: https://medium.com/google-developers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54

[Android arhitecture blueprints]: https://github.com/googlesamples/android-architecture

[Android architecture guide]: https://developer.android.com/topic/libraries/architecture/guide.html

[Android architecture components codelab]: https://codelabs.developers.google.com/codelabs/build-app-with-arch-components/index.html#0

[Room]: https://developer.android.com/topic/libraries/architecture/room.html "Room persistence library"

[Room tutorial]: http://www.vogella.com/tutorials/AndroidSQLite/article.html

[Room codelab]: https://codelabs.developers.google.com/codelabs/android-persistence/

[Room testing]: https://commonsware.com/AndroidArch/previews/testing-room

[Android lifecycles codelab]: https://codelabs.developers.google.com/codelabs/android-lifecycles/

[ViewModel]: https://developer.android.com/topic/libraries/architecture/viewmodel.html

[LiveData]: https://developer.android.com/topic/libraries/architecture/livedata.html

[Dagger]: https://google.github.io/dagger/ "dependency injection done with generated classes"

[HttpUrlConnection]: https://stackoverflow.com/questions/8654876/http-get-using-android-httpurlconnection "low level http usage"

[okHttp documentation]: http://square.github.io/okhttp/ "documentation of okHttp"

[okHttp]: https://github.com/square/okhttp "simple api for http operations"

[okIo]: https://github.com/square/okio "extension of java io libraries"

[Retrofit]: http://square.github.io/retrofit/ "simplify http api's"

[Volley]: https://developer.android.com/training/volley/index.html "operations as queue"

[Volley tutorial]: https://www.sitepoint.com/volley-a-networking-library-for-android/

[Http get with Volley]: https://developer.android.com/training/volley/simple.html

[Android annotations]: http://androidannotations.org/ "annotation processor and code generator for boilerplate code"

[Guava]: https://github.com/google/guava "Google guava is handy toolbox"

[64K DEX limit]: https://developer.android.com/studio/build/multidex.html "Method table limit on Dalvik Executables"

[ProGuard]: https://www.guardsquare.com/en/proguard "Bytecode optimization and obfuscation tools"

[Content provider]: https://developer.android.com/guide/topics/providers/content-providers.html "Content provider documentation"

[Sample content provider]: https://github.com/googlesamples/android-architecture-components/blob/master/PersistenceContentProviderSample/app/src/main/java/com/example/android/contentprovidersample/provider/SampleContentProvider.java "Sample content provider"

[SQLite]: https://www.sqlite.org/ "Low footprint embedded database"

[GSON]: https://github.com/google/gson "Googles serialization library"

[AutoValue]: https://github.com/google/auto/tree/master/value "AutoValue for generation of Value objects"

[AutoValue issue]: https://developer.android.com/topic/libraries/architecture/room.html "AutoValue Room integration blocker"

[Lombok]: https://projectlombok.org/features/Data "Lombok data annotation"

[Lombok issue]: https://github.com/googlesamples/android-architecture-components/issues/120 "Lombok Room integration blocker"

[Configuration chages]: https://developer.android.com/guide/topics/resources/runtime-changes.html "When activity is killed and recreated"

[IntentService-vs-AsyncTask 1]: https://android.jlelse.eu/using-intentservice-vs-asynctask-in-android-2fec1b853ff4 "how to run background processes"

[IntentService-vs-AsyncTask 2]: https://medium.com/@skidanolegs/asynctask-vs-intentservice-1-example-without-code-5250bea6bdae "how to run background processes"

[AsyncTask problems]: http://bon-app-etit.blogspot.de/2013/04/the-dark-side-of-asynctask.html

[Loaders]: https://developer.android.com/guide/components/loaders.html

[Next Billion Users]: https://www.youtube.com/watch?v=70WqJxymPr8&list=PL5G-TQp5op5qVE2mKqFlBnvbwsWFEwkFs&index=8 "Online / Offline problem"

[UML component diagram of app]: https://github.com/nikkijuk/PlainRssReader/blob/master/docs/PlainRssReader-components.png "Apps components"

[UML use case diagram of app]: https://github.com/nikkijuk/PlainRssReader/blob/master/docs/PlainRssReader-UseCases.png "Apps use cases"

[UML class diagram of feed model]: https://github.com/nikkijuk/PlainRssReader/blob/master/docs/feed-model.png "Feeds model classes"

[list]: https://github.com/nikkijuk/PlainRssReader/blob/master/docs/news-list.png "List of feeds"
[item]: https://github.com/nikkijuk/PlainRssReader/blob/master/docs/news-item.png "Feed item"
[settings]: https://github.com/nikkijuk/PlainRssReader/blob/master/docs/settings.png "Settings"
[settings-url]: https://github.com/nikkijuk/PlainRssReader/blob/master/docs/settings-url.png "Set feed url"

[mockup-list]: https://github.com/nikkijuk/PlainRssReader/blob/master/docs/marvel-mockup-list.png "Mockup: List of feeds"
[mockup-item]: https://github.com/nikkijuk/PlainRssReader/blob/master/docs/marvel-mockup-item.png "Mockup: Feed item"
[mockup-settings]: https://github.com/nikkijuk/PlainRssReader/blob/master/docs/marvel-mockup-settings.png "Mockup: Settings"

[PlainRssReader]: https://github.com/nikkijuk/PlainRssReader
