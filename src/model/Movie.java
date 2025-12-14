package model;

import java.sql.Timestamp;

public class Movie {
    private Integer movieId;
    private String title;
    private Integer runningTime;
    private String director;
    private String genre;
    private String rating;
    private Timestamp releaseDate;
    
    public Movie() {
    }
    
    public Movie(Integer movieId, String title, Integer runningTime, String director, 
                 String genre, String rating, Timestamp releaseDate) {
        this.movieId = movieId;
        this.title = title;
        this.runningTime = runningTime;
        this.director = director;
        this.genre = genre;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public Integer getMovieId() {
        return movieId;
    }
    
    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Integer getRunningTime() {
        return runningTime;
    }
    
    public void setRunningTime(Integer runningTime) {
        this.runningTime = runningTime;
    }
    
    public String getDirector() {
        return director;
    }
    
    public void setDirector(String director) {
        this.director = director;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public String getRating() {
        return rating;
    }
    
    public void setRating(String rating) {
        this.rating = rating;
    }
    
    public Timestamp getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(Timestamp releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", title='" + title + '\'' +
                ", runningTime=" + runningTime +
                ", director='" + director + '\'' +
                ", genre='" + genre + '\'' +
                ", rating='" + rating + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}

