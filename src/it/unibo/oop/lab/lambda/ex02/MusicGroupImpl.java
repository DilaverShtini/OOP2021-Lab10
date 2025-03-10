package it.unibo.oop.lab.lambda.ex02;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    /**
     * @return all the songs for this group ordered by their name
     */
    @Override
    public Stream<String> orderedSongNames() {
    	return this.songs.stream()
    				.map(s -> s.songName)
    				.sorted();
    }

    /**
     * @return all the album of this group
     */
    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    /**
     * @param year
     * 			the year
     * @return the albums in this specific year
     */
    @Override
    public Stream<String> albumInYear(final int year) {
        return albums.entrySet().stream()
        						.filter(y -> y.getValue() == year)
        						.map(y -> y.getKey());
    }

    /**
     * @param albumName
     * 				name of the album
     * @return the number of songs in this album
     */
    @Override
    public int countSongs(final String albumName) {
        return (int) songs.stream()
        				.filter(l -> l.getAlbumName().isPresent())	
        				.filter(l -> l.getAlbumName().get().equals(albumName))
        				.distinct()
        				.count();
    }

    /**
     * @return the number of songs with no album
     */
    @Override
    public int countSongsInNoAlbum() {
        return (int) songs.stream()
        			.filter(l -> l.getAlbumName().isEmpty())
        			.count();
    }

    /**
     * @param albumName 
     * 			the name of the album
     * @return average track length for the album
     */
    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
    	return songs.stream()
    				.filter(l -> l.getAlbumName().isPresent())
    				.filter(l -> l.getAlbumName().get().equals(albumName))
    				.mapToDouble(l -> l.getDuration())
    				.average();
    }

    /**
     * @return the longest song
     */
    @Override
    public Optional<String> longestSong() {
    	return songs.stream()
    				.max((x, y) -> Double.compare(x.getDuration(), y.getDuration()))
    				.map(s -> s.songName);
    }

    /**
     * @return the longest album
     */
	@Override
    public Optional<String> longestAlbum() {
    	return songs.stream()
    				.filter(a -> a.getAlbumName().isPresent())
    				.collect(Collectors.groupingBy(a -> a.getAlbumName(), Collectors.summingDouble(d -> d.getDuration())))
    				.entrySet()
    				.stream()
    				.collect(Collectors.maxBy(Comparator.comparingDouble(a -> a.getValue())))
    				.map(a -> a.getKey().get());
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
