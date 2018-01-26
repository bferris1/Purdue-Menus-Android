# Purdue Menus Android

This is an Android app that displays menus for Purdue University dining courts.

It was created to address several issues with the official Purdue Mobile Menus app:
- The official app requires identity, location, and phone permissions (and uses the old permission system)
- The official app uses GPS continuously, even after the app is closed, causing significant battery drain
- Comparing menus between dining courts for a given day is inconvenient due to the design of the offical app

The current priorities for development are:
1. Improving the program structure (and testing)
    - restructuring the app to fully utilize Android Dependency Injection with Dagger 2
    - writing tests for individual decoupled components
    - possibly converting menus classes to POJOs
    - moving view-related data to ViewModel
    - implementing [Android Data Binding](https://developer.android.com/topic/libraries/data-binding/index.html)
2. Improving the way data is retrieved
    - using NetworkBoundResource or similar class to mediate local caching and decouple components
        - maybe use library like [Store](https://github.com/NYTimes/Store)
    - improved thread management for background operations (Executors, etc)
    - deciding if data should be refreshed from network after a cache hit
    - possibly storing data in a [different format](http://objectbox.io) (i.e. not in json files)
    - improved exception handling for network/JSON parsing exceptions
3. Implementing the favorites system
    1. Login (store credentials) and retrieve favorites
    2. Store favorites locally in a suitable format (Room database?)
    3. Add/remove favorites locally
    4. Persist favorites between local storage and the menus API
4. Updating data periodically in the background
    - Sending notifications when selected items are served
    - Allow users to specify notification times
