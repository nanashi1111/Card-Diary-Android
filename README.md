# Card diary 

A free diary app that can help you save the beautiful moments in your life

Design idea: https://www.behance.net/gallery/59120015/Card-Diary-iOS-App


## Architecture:
- Built with Modern Android Development practices
- Utilized Usecase, Repository pattern for data
- Includes unit tests for Use cases, Repository, ViewModels, API Service response.

## Features
- Write diary posts
- Design your diary book
- Export / Import your diary posts

## Screens

![ac009859120015 5a259260646b6](https://user-images.githubusercontent.com/4631151/196031817-2d8bfdaf-f2d0-4d0d-a716-be3c95bfaea5.gif)


![fcb96759120015 5a26f4f8d1c87](https://user-images.githubusercontent.com/4631151/196031916-e74581c9-48fd-47ab-9ff6-0341e3be766b.png)

## Build with

- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) - A cold asynchronous data stream that sequentially emits values and completes normally or with an exception.
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [Paging 3.0](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) -The Paging library helps you load and display pages of data from a larger dataset from local storage or over network.
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Data objects that notify views when the underlying database changes.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes.
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.
  - [DataBinding](https://developer.android.com/topic/libraries/data-binding) - Support library that allows you to bind UI components in your layouts to data sources in your app using a declarative format rather than programmatically.
- [Dependency Injection](https://developer.android.com/training/dependency-injection)
  - [Hilt](https://dagger.dev/hilt) - Easier way to incorporate Dagger DI into Android apps.
- [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java.
- [Glide](https://github.com/bumptech/glide) - An image loading and caching library for Android focused on smooth scrolling.
- [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html) - For writing Gradle build scripts using Kotlin.
