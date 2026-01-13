package com.example.songservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "songs")
public class Song {

    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String artist;

    @Column(nullable = false, length = 100)
    private String album;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false)
    private String year;

    public Song() {
    }

    public Song(Long id, String name, String artist, String album, String duration, String year) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        var that = (Song) obj;
        return Objects.equals(getId(), that.getId())
            && Objects.equals(getName(), that.getName())
            && Objects.equals(getArtist(), that.getArtist())
            && Objects.equals(getAlbum(), that.getAlbum())
            && Objects.equals(getDuration(), that.getDuration())
            && Objects.equals(getYear(), that.getYear());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getArtist(), getAlbum(), getDuration(), getYear());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Song.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("name='" + name + "'")
            .add("artist='" + artist + "'")
            .add("album='" + album + "'")
            .add("duration='" + duration + "'")
            .add("year='" + year + "'")
            .toString();
    }
}
