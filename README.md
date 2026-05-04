# Android87 Photos App - GenAI Usage README

This document explains how Generative AI (Google Gemini via Android Studio) was used in the development of the Photos application, as per the assignment requirements.

## How GenAI Was Used
GenAI was used in a sequence of iterative prompts to build, refine, and bug-fix individual functional components of the app. It was not used to build the entire app from a single "vibe" prompt. Instead, it assisted with specific tasks like layout design, access modifier fixes, logic implementation, and resource extraction.

## Prompts Issued
1.  **Bug Fixing**: "calculateInSampleSize(Options,int,int) is not public in PhotoGridAdapter; cannot be accessed from outside package. Fix this issue."
2.  **Requirement Verification**: "Check if the following is applied Home screen. When the app comes up, it should load album and photo data... and list all albums with names in plain text."
3.  **Feature Implementation**: "The app should have all of these features [attached requirements image] some are there but some need to be implemented can you do those."
4.  **UI Navigation Improvements**: "i cant find the search function can you check."
5.  **UI Redesign**: "can you change the ui a little so it looks a bit more appealing like something like the image [attached UI inspiration image] where the centre button could be the add button..."
6.  **UI Bug Fixing**: "the alignment is not correct and the apps just stopping on its own."
7.  **UX Refinement**: "make a small change like in the photo when we add a tag if its type person the example should show a persons name like bob and when i change it to location it shows something like new york."
8.  **Search Logic Fixes**: "new delhi tag is not showing up in the auto completeion for tag 1 but comes up for tag 2."
9.  **Standardization**: "dont hard code anything."
10. **Customization**: "change the info to app by Nischay Agarwal and Matthew Wang."

## UI Drawings/Images Uploaded
*   **Assignment Requirements Image**: Used to verify the full list of mandatory features (Slideshow, Search logic, Persistence, etc.).
*   **UI Inspiration Image**: Used to redesign the Home Screen with a modern BottomAppBar and CardView layout.
*   **Contextual UI Screenshots**: Used to debug alignment issues and tag dialog behavior.

## AI-Generated Components
*   **Layouts**: Modernized `activity_home.xml` and `item_album.xml` based on the UI inspiration image.
*   **Adapters**: Refinements to `AlbumAdapter.java` and `SearchResultAdapter.java` for click handling and search result display.
*   **Activity Logic**: Dynamic hint logic in `PhotoActivity.java` and optimized auto-complete/filtering logic in `SearchActivity.java`.
*   **Resources**: Comprehensive extraction of strings into `strings.xml`.

## Components Written/Refined by Developer
*   **Initial Structure**: The core Model-View-Controller (MVC) structure and basic activity flow were pre-existing or developer-initiated.
*   **Data Models**: Verification of `Album`, `Photo`, and `Tag` serialization for persistence.
*   **Integration**: Gluing together the AI-suggested UI components with the back-end storage logic (`StorageManager.java`).
*   **Verification**: Running Gradle builds, inspecting Logcat for crashes, and manual testing of search disjunction/conjunction logic.

---
*This README is provided solely for the understanding of how GenAI is used by students for coding.*
