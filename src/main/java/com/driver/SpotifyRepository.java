package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile)
    {
        User user = new User(name, mobile);
        users.add(user); // added in list

        return user;
    }


    public Artist createArtist(String name)
    {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName)
    {
        Artist artistDemo =null;
        for(Artist artist : artists){
            if(artist.getName().equals(artistName)){
                artistDemo=artist;
                break;
            }
        }

        if(artistDemo==null){
            artistDemo = createArtist(artistName);

            Album alb = new Album();
            alb.setTitle(title);
            alb.setReleaseDate(new Date());

            albums.add(alb);

            List<Album> listAlb = new ArrayList<>();

            listAlb.add(alb);

            artistAlbumMap.put(artistDemo,listAlb);

            return alb;

        }else{
            Album alb = new Album();
            alb.setTitle(title);
            alb.setReleaseDate(new Date());
            albums.add(alb);

            List<Album> listAlb =  artistAlbumMap.get(artistDemo);

            if(listAlb==null){
                listAlb = new ArrayList<>();
            }

            listAlb.add(alb);
            artistAlbumMap.put(artistDemo, listAlb);

            return alb;
        }

    }

    public Song createSong(String title, String albumName, int length) throws Exception
    {
        Album newAlbum=null;
        for(Album alb : albums){
            if(alb.getTitle().equals(albumName)){
                newAlbum=alb;
                break;
            }
        }

        if(newAlbum==null){
            throw new Exception("Album does not exist");
        }else{
            Song newSong = new Song();
            newSong.setTitle(title);
            newSong.setLength(length);
            newSong.setLikes(0);

            songs.add(newSong);

            if(albumSongMap.containsKey(newAlbum)){
                List<Song> songList = albumSongMap.get(newAlbum);

                songList.add(newSong);
                albumSongMap.put(newAlbum,songList);
            }
            else
            {
                List<Song>songList = new ArrayList<>();
                songList.add(newSong);
                albumSongMap.put(newAlbum,songList);

            }

            return newSong;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception
    {
        User newUser = null;
        for(User userOne : users){
            if(userOne.getMobile().equals(mobile)){
                newUser=userOne;
                break;
            }
        }

        if(newUser==null){
            throw new Exception("User does not exist");
        }
        else{
            Playlist playList = new Playlist();
            playList.setTitle(title);
            playlists.add(playList);

            List<Song>list = new ArrayList<>();
            for(Song song : songs){
                if(song.getLength()==length){
                    list.add(song);
                }
            }

            playlistSongMap.put(playList,list);

            List<User> userList = new ArrayList<>();
            userList.add(newUser);
            playlistListenerMap.put(playList, userList);

            creatorPlaylistMap.put(newUser, playList);

            if(userPlaylistMap.containsKey(newUser)){
                List<Playlist> userPlayList = userPlaylistMap.get(newUser);
                userPlayList.add(playList);
                userPlaylistMap.put(newUser, userPlayList);
            }else{
                List<Playlist> userPlayList = new ArrayList<>();
                userPlayList.add(playList);
                userPlaylistMap.put(newUser, userPlayList);
            }
            return playList;
        }

    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        User newUser = null;
        for(User userOne : users){
            if(userOne.getMobile().equals(mobile)){
                newUser=userOne;
                break;
            }
        }

        if(newUser==null){
            throw new Exception("User does not exist");
        }
        else{
            Playlist playList = new Playlist();
            playList.setTitle(title);
            playlists.add(playList);

            List<Song>list = new ArrayList<>();
            for(Song song : songs){
                if(songTitles.contains(song.getTitle())){
                    list.add(song);
                }
            }

            playlistSongMap.put(playList,list);

            List<User> userList = new ArrayList<>();
            userList.add(newUser);
            playlistListenerMap.put(playList, userList);

            creatorPlaylistMap.put(newUser, playList);

            if(userPlaylistMap.containsKey(newUser)){
                List<Playlist> userPlayList = userPlaylistMap.get(newUser);
                userPlayList.add(playList);
                userPlaylistMap.put(newUser, userPlayList);
            }else{
                List<Playlist> userPlayList = new ArrayList<>();
                userPlayList.add(playList);
                userPlaylistMap.put(newUser, userPlayList);
            }
            return playList;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User newUser = null;
        for(User userOne : users){
            if(userOne.getMobile().equals(mobile)){
                newUser = userOne;
                break;
            }
        }

        if(newUser==null){
            throw new Exception("User does not exist");
        }
        Playlist playList = null;
        for(Playlist playListOne : playlists){
            if(playListOne.getTitle()==playlistTitle){
                playList = playListOne;
                break;
            }
        }
        if(playList==null){
            throw new Exception("Playlist does not exist");
        }

        if(creatorPlaylistMap.containsKey(newUser)){
            return playList;
        }
        List<User> listener = playlistListenerMap.get(playList);
        for(User userOne : listener){
            if(userOne==newUser){
                return playList;
            }
        }

        listener.add(newUser);
        playlistListenerMap.put(playList, listener);

        List<Playlist> playListOne = userPlaylistMap.get(newUser);
        if(playListOne==null){
            playListOne = new ArrayList<>();
        }
        playListOne.add(playList);
        userPlaylistMap.put(newUser, playListOne);

        return playList;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {

        User newUser = null;
        for(User userOne : users){
            if(userOne.getMobile().equals(mobile)){
                newUser=userOne;
                break;
            }
        }

        if(newUser==null){
            throw new Exception("User does not exist");
        }

        Song song = null;

        for(Song sng : songs){
            if(sng.getTitle().equals(songTitle)){
                song=sng;
                break;
            }
        }
        if(song==null){
            throw new Exception("Song does not exist");
        }

        if(songLikeMap.containsKey(song)){
            List<User> list = songLikeMap.get(song);
            if(list.contains(newUser)){
                return song;
            }else {
                int likes = song.getLikes() + 1;
                song.setLikes(likes);
                list.add(newUser);
                songLikeMap.put(song,list);

                Album album=null;
                for(Album albumOne:albumSongMap.keySet()){
                    List<Song> songList = albumSongMap.get(albumOne);
                    if(songList.contains(song)){
                        album = albumOne;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist artistOne:artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(artistOne);
                    if (albumList.contains(album)){
                        artist = artistOne;
                        break;
                    }
                }
                int likesOne = artist.getLikes()+1;
                artist.setLikes(likesOne);
                artists.add(artist);
                return song;
            }
        }else {
            int likes = song.getLikes() + 1;
            song.setLikes(likes);
            List<User> list = new ArrayList<>();
            list.add(newUser);
            songLikeMap.put(song,list);

            Album album=null;
            for(Album albumOne:albumSongMap.keySet()){
                List<Song> songList = albumSongMap.get(albumOne);
                if(songList.contains(song)){
                    album = albumOne;
                    break;
                }
            }
            Artist artist = null;
            for(Artist artistOne:artistAlbumMap.keySet()){
                List<Album> albumList = artistAlbumMap.get(artistOne);
                if (albumList.contains(album)){
                    artist = artistOne;
                    break;
                }
            }
            int likesOne = artist.getLikes() +1;
            artist.setLikes(likesOne);
            artists.add(artist);

            return song;
        }
    }

    public String mostPopularArtist() {
        int maximum = 0;
        Artist artistOne = null;

        for(Artist ar : artists){
            if(ar.getLikes()>=maximum){
                artistOne = ar;
                maximum = ar.getLikes();
            }
        }
        if(artistOne ==null){
            return null;
        }else{
            return artistOne.getName();
        }
    }

    public String mostPopularSong() {
        int maximum = 0;
        Song song = null;

        for(Song sng : songLikeMap.keySet()){
            if(sng.getLikes()>=maximum){
                song=sng;
                maximum = sng.getLikes();
            }
        }

        if(song==null){
            return null;
        }else{
            return song.getTitle();
        }

    }
}
