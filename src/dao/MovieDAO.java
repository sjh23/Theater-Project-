package dao;

import model.Movie;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM MOVIE";
        
        System.out.println("[MovieDAO] 쿼리 실행: " + sql);
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            while (rs.next()) {
                try {
                    movies.add(mapResultSetToMovie(rs));
                    count++;
                } catch (SQLException e) {
                    System.err.println("[MovieDAO] 영화 데이터 변환 오류 (행 " + count + "): " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            }
            System.out.println("[MovieDAO] 조회된 영화 개수: " + count);
        } catch (SQLException e) {
            System.err.println("[MovieDAO] SQL 오류 발생: " + e.getMessage());
            System.err.println("SQL 상태: " + e.getSQLState());
            System.err.println("에러 코드: " + e.getErrorCode());
            throw e;
        }
        
        return movies;
    }

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

    public boolean deleteMovie(Integer movieId) throws SQLException {
        String sql = "DELETE FROM MOVIE WHERE movie_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, movieId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

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

