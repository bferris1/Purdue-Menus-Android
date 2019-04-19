# Purdue Menus Android

This is an Android app that displays menus for Purdue University dining courts.

<a href='https://play.google.com/store/apps/details?id=com.moufee.purduemenus&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'>
<img width="200" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

It was created to address several issues with the official Purdue Mobile Menus app:
- The official app required identity, location, and phone permissions (and used the old permission system)
- The official app used GPS continuously, even after the app was closed, causing significant battery drain
- The design makes it inconvenient to compare menus between dining courts for a given day

The current priorities for development are:
1. Adding support for the locations endpoint
    - [x] Implement retrieval of relevant location information
    - [ ] Store Locations list and sorting preferences locally
    - [ ] Use locations list to retrieve menus
    - [ ] Possibly allow locations to be hidden
2. Improving the program structure (and testing)
    - [x] restructuring the app to fully utilize Android Dependency Injection with Dagger 2
    - [ ] writing tests for individual components
    - [ ] moving all view-related data to ViewModel
3. Improving the way data is retrieved
    - [ ] using NetworkBoundResource or similar class to mediate local caching and decouple components
        - maybe use library like [Store](https://github.com/NYTimes/Store)
    - [ ] improved thread management for background operations (Executors, etc)
    - [ ] deciding if data should be refreshed from network after a cache hit
    - [ ] improved exception handling for network/JSON parsing exceptions
4. Implementing the favorites system
    - [x] Login (store cookie) and retrieve favorites
    - [x] Store favorites locally in a suitable format (Room database)
    - [x] Add/remove favorites locally
    - [x] Synchronize favorites between local database and the menus API
5. Updating data periodically in the background
    - Sending notifications when selected items are served
    - Allow users to specify notification times

