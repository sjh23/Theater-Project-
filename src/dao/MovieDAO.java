package dao;

import model.Movie;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MOVIE 테이블에 대한 데이터 접근 객체
 */
public class MovieDAO {
    
    /**
     * 모든 영화를 조회합니다.
     */
    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM MOVIE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        }
        
        return movies;
    }
    
    /**
     * 영화 ID로 영화를 조회합니다.
     */
    public Movie getMovieById(Integer movieId) throws SQLException {
        String sql = "SELECT * FROM MOVIE WHERE movie_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, movieId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMovie(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 장르로 영화를 조회합니다.
     */
    public List<Movie> getMoviesByGenre(String genre) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM MOVIE WHERE genre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, genre);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapResultSetToMovie(rs));
                }
            }
        }
        
        return movies;
    }
    
    /**
     * 제목으로 영화를 검색합니다.
     */
    public List<Movie> searchMoviesByTitle(String keyword) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM MOVIE WHERE title LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapResultSetToMovie(rs));
                }
            }
        }
        
        return movies;
    }
    
    /**
     * 새 영화를 추가합니다.
     */
    public boolean insertMovie(Movie movie) throws SQLException {
        String sql = "INSERT INTO MOVIE (title, running_time, director, genre, rating, release_data) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, movie.getTitle());
            pstmt.setInt(2, movie.getRunningTime());
            pstmt.setString(3, movie.getDirector());
            pstmt.setString(4, movie.getGenre());
            pstmt.setString(5, movie.getRating());
            pstmt.setTimestamp(6, movie.getReleaseDate());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        movie.setMovieId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 영화 정보를 업데이트합니다.
     */
    public boolean updateMovie(Movie movie) throws SQLException {
        String sql = "UPDATE MOVIE SET title = ?, running_time = ?, director = ?, genre = ?, rating = ?, release_data = ? WHERE movie_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, movie.getTitle());
            pstmt.setInt(2, movie.getRunningTime());
            pstmt.setString(3, movie.getDirector());
            pstmt.setString(4, movie.getGenre());
            pstmt.setString(5, movie.getRating());
            pstmt.setTimestamp(6, movie.getReleaseDate());
            pstmt.setInt(7, movie.getMovieId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * 영화를 삭제합니다.
     */
    public boolean deleteMovie(Integer movieId) throws SQLException {
        String sql = "DELETE FROM MOVIE WHERE movie_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, movieId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * ResultSet을 Movie 객체로 변환합니다.
     */
    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setMovieId(rs.getInt("movie_id"));
        movie.setTitle(rs.getString("title"));
        movie.setRunningTime(rs.getInt("running_time"));
        movie.setDirector(rs.getString("director"));
        movie.setGenre(rs.getString("genre"));
        movie.setRating(rs.getString("rating"));
        movie.setReleaseDate(rs.getTimestamp("release_data"));
        return movie;
    }
}

