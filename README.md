# GlobalWaves Documentation - Stage 1

---

### Implementation Details:

**Classes**  
In the initial setup, I created a `Command` class to store commands from input files. These commands are stored in a vector, which is iterated through for each test case.  
Classes `Song`, `Podcast`, `User`, and `Episode` were also created, each with specific methods to facilitate searches and modifications as required by the input commands. Additionally, a `Playlist` class was implemented to manage the song lists for each user, and a `Player` class acts as the parent class for all entities (Song, Podcast, Playlist).

---

### Command Functionalities

**Search Command**  
The `search` command searches across the entire library of objects, applying any specified filters. Results (up to five) are stored in lists (`songsForSelection`, `podcastsForSelection`, `playlistsForSelection`), which are then reused by the `select` command.

**Load Command**  
When a `load` command is encountered, the player loads the selected entity. Since each user has a unique player, this action varies from one user to another. The field `crtLoaded` stores the loaded entity, which can be of type `Song`, `Podcast`, or `Playlist`.

**PlayPause Command**  
For `playPause`, I utilized command timestamps to recalculate the remaining time of the entity currently playing. This recalculation occurs whenever `playPause` or `status` commands are encountered. In the case of podcasts, episodes and their timestamps are calculated based on the `load/status/playPause` command timestamps and the `time` and `loadTS` fields of each episode. Here, the `time` field represents the duration (in seconds) that has already played, and the `loadTS` field marks the timestamp of the last `load` command. This field is updated when `playPause` or `status` commands are issued.

**CreatePlaylist Command**  
`createPlaylist` allows each user to add new playlists to their personal playlist collection.

**Like Command**  
The `like` command adds or removes a song from the user’s playlist of favorite songs. Each `User` object has a list to store their favorite songs.

**Repeat Command**  
I implemented different repeat types and functionality for toggling between them, though full integration within the rest of the program has not yet been completed. The same is true for the `shuffle` feature.

**Follow Command**  
The `follow` command updates the `followers` field for each playlist followed and adds the playlist to the user’s list of followed playlists.

---

# <span style="color: red;">GlobalWaves - Stage 2</span>

---

### <span style="color:green;">Implementation:</span>

#### <span style="color:white;">Design Pattern:</span>
* I chose to implement the Singleton design pattern for the `Admin` class (this class can be associated with a database, with a unique admin).

#### <span style="color:white;">Packages / Classes:</span>
* Compared to the initial version of the code, I added new packages: `Merch`, `Pages`, `SpecialUsersEvents`, each containing new classes as specified in the requirements.
* In the `User` package, I added two new classes: `Artist` and `Host`. Instances of these classes are registered in the database’s user list (`_users` field in the `Admin` class).
* In the `Pages` package, the `Page` class contains functions that display the appropriate messages for the user’s current page.
* The `SpecialUsersEvents` package contains classes with specific events for each special user (artist or host). This includes the `Announcement` (for Host) and `Event` (for Artist) classes.

#### <span style="color:white;">New Commands:</span>
* <span style="color:green;">switchConnectionStatus</span>
&#8594;
In the `User` class, I added a `connection` field with values "online" or "offline" based on the user’s previous status. When a user disconnects, the player’s timestamp is no longer updated. As a result, I modified many functions so they execute only when the user running the command is online.<br>

* <span style="color:green;">getOnlineUsers</span>
&#8594;
Displays all online users at the time the command runs by iterating through the database user list (`users` field in the `Admin` class).<br>

* <span style="color:green;">addUser</span>
&#8594;
In the `User` class, I added a new `role` field with values of `user`, `artist`, or `host`, depending on the new user being created. These users are added to the database’s user list (`_users` field in the `Admin` class). Regular users are set online upon addition, while artists and hosts are set offline.<br>

* <span style="color:green;">addAlbum</span>
&#8594;
Checks all conditions for creating a new album. If all conditions are met, an `Album` instance is created, with its songs added to the database via the `addSongsToDatabase` command. The album is then added both to the artist’s album list and to the database album list (`albums` field in the `Admin` class).<br>

* <span style="color:green;">showAlbums</span>
&#8594;
Displays all of an artist’s albums using the `getAlbumsFromArtist` function in the `Artist` class.<br>

* <span style="color:green;">printCurrentPage</span>
&#8594;
Depending on the user’s `_currentPage` field ("Home" / "LikedContent" / other), it calls the appropriate page printing function. The display functions are in the `Page` class.<br>

* <span style="color:green;">addEvent</span>
&#8594;
Verifies all conditions for creating a new event. To ensure date input matches standard format, a regular expression is used (`validDate` function). Afterward, the event is created and added to the creating artist’s event list.<br>

* <span style="color:green;">addMerch</span>
&#8594;
Checks all conditions for creating a new merch item. The `Merch` class contains a function that generates the message returned by the `addMerch` function. After creation, the merch is added to the respective artist’s merch list.<br>

* <span style="color:green;">getAllUsers</span>
&#8594;
Displays all platform users in the following order: regular users, artists, hosts. The user’s role is determined using the `role` field getter in the `User` class.<br>

* <span style="color:green;">deleteUser</span>
&#8594;
A user cannot be deleted if another user has loaded their song/podcast/album/playlist in the player or if another user is on their page. When a user is deleted, their database connections and connections to other users must also be removed. All their songs/podcasts/albums are also removed from the database. If the user follows other playlists, their follower count is decremented. If others follow their playlist, it’s removed from other users’ `followedPlaylists` lists. If others liked one of their songs, the song is removed from the `likedSongs` list.<br>

* <span style="color:green;">addPodcast</span>
&#8594;
Checks all conditions for creating a new podcast. If conditions are met, the podcast is added to both the host’s podcast list and the database (`podcasts` list in the `Admin` class).<br>

* <span style="color:green;">addAnnouncement</span>
&#8594;
Verifies all conditions for creating a new announcement. If conditions are met, the announcement is added to the respective host’s announcement list.<br>

* <span style="color:green;">removeAnnouncement</span>
&#8594;
Checks all conditions for deleting an announcement. If conditions are met, the announcement is removed from the respective host’s announcement list.<br>

* <span style="color:green;">showPodcasts</span>
&#8594;
After verifying that the user exists and is a host, displays all their podcasts.<br>

* <span style="color:green;">removeAlbum</span>
&#8594;
Checks all conditions for deleting an album. If conditions are met, it’s deleted from both the artist’s album list and the database. Additionally, upon deletion, the album’s songs are also removed from the database.<br>

* <span style="color:green;">changePage</span>
&#8594;
Determines the new desired page based on the `_nextPage` field and displays the appropriate message. To retain the page the user is on after the command, the user’s `_crtPage` field is updated to "Home", "LikedContent", or the name of the viewed user’s page.<br>

* <span style="color:green;">removePodcast</span>
&#8594;
Checks all conditions for deleting a podcast. If conditions are met, the podcast is removed from both the host’s podcast list and the database.<br>

* <span style="color:green;">removeEvent</span>
&#8594;
Checks all conditions for deleting an event. If conditions are met, the event is removed from the respective artist’s event list.<br>

* <span style="color:green;">getTop5Albums</span>
&#8594;
Sorts albums by total number of likes and displays the top 5 if the size is > 5, or the top x albums, where x is between 0 and 5.<br>

* <span style="color:green;">getTop5Artists</span>
&#8594;
Similar to the previous function, it sorts artists by total likes received on their songs, displaying the top 5 if the size is > 5, or the top x, where x is between 0 and 5.

---

# <span style="color: cyan;">GlobalWaves - Stage 3</span>

---

### <span style="color: purple;">Implementation:</span>

#### <span style="color:white;">Design Pattern:</span>
* I chose to implement the Singleton pattern for the `Admin` class because `Admin` can be associated with a database, and the admin instance should be unique.<br>

* I implemented the Factory design pattern for `User` creation. Since there are three types ("user", "artist", and "host"), the `UserFactory` class has a function that returns a new object of the corresponding type based on the parameter received (`type`). The function call can be found in the `User` class’s `createUser` function.

* I used the Builder pattern for `WrappedEntity` to create objects based on required fields (for example, a podcast does not have `genre/artist/album`).

* I implemented the Visitor pattern for printing an artist/host’s page, as each entity (Album, Event, Merch, Podcast, Announcement) has a unique format. With a visitor, each object calls its own display method.

#### <span style="color:white;">Program Flow:</span>

* For displaying statistics using the `wrapped` command, I created the `WrappedEntity` class. This class helps count listens for each instance (album, song, podcast). Each user has an `ArrayList` of `SongHelper`, where new songs (recently listened to) are added, or the listen count for an already-listened-to song is incremented.
* Entity listens are updated in several cases: during a load command (for songs), when a user’s player pauses while executing a search command, or when the `wrapped` command is run. This is achieved by storing the timestamp of the load command and calculating the difference between the search/wrapped timestamp and the load timestamp. The difference represents the time interval between the two commands, during which the player’s entity (e.g., an album) is checked to count how many songs were listened to in that interval, adding them to the `listenedEntities` field in the `User` class.
* To generate user statistics, the program iterates through the user’s `listenedEntities` and categorizes them by type, creating `SongHelper`, `AlbumHelper`, or `GenreHelper` objects, which are added to lists that are then sorted and truncated to a maximum size of 5 (if their size exceeds 5).
* To generate an artist's statistics, it goes through the `listenedEntities` of all users, adding new listeners or newly-listened-to songs of the artist who calls the `wrapped` command. This process is similar to a regular user's wrapped command, but in the artist's case, all user lists need to be traversed.
* Additionally, when an artist calls the `wrapped` command, the wrapped statistics of all users are updated to capture songs that may have been listened to in the meantime.
* The program always ends with the `endProgram` command, which is initialized with default values and called in the main function.

