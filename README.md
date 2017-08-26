# Purdue Menus Android

This is an Android app that displays menus for Purdue Univeristy dining courts.

It was created to address several issues with the offical Purdue Mobile Menus app:
- The official app requires identity, location, and phone permissions (and uses the old permission system)
- The official app uses GPS continuously, even after the app is closed, causing significant battery drain
- In the official app, it is difficult to compare menus between dining courts for a given day

The current priorities for development are:
1. Improving the way data is retrieved
    - using NetworkBoundResource or similar class to mediate local caching and decouple components
    - improved thread managment for background operations (Executors, etc)
    - deciding if data should be refreshed from network after a cache hit
2. Improving the program structure
    - implementing dependency injection
    - possibly converting menus classes to POJOs
    - moving view-related data to ViewModel
    - implementing [Android Data Binding](https://developer.android.com/topic/libraries/data-binding/index.html)
3. Implementing the favorites sytem
    1. Login (store credentials) and retrieve favorites
    2. Store favorites locally in a suitable data structure (Room database?)
    3. Add/remove favorites locally
    4. Persist favorites between local storage and the menus API
4. Updating data periodically in the background
