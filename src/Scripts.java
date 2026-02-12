import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Scripts {
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, SHA2(?,256))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.out.println("Database connection failed.");
                return false;
            }
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Error: Username already exists.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static int loginUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password_hash = SHA2(?,256)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.out.println("Database connection failed.");
                return -1;
            }
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void addBook(int userId, Book book) {
        String sql = "INSERT INTO books (user_id, name, author, edition, notes, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, book.getName());
            pstmt.setString(3, book.getAuthor());
            pstmt.setInt(4, book.getEdition());
            pstmt.setString(5, book.getNotes());
            pstmt.setString(6, book.getStatus().toString());

            pstmt.executeUpdate();
            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Book> listBooks(int userId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE user_id = ? ORDER BY created_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setName(rs.getString("name"));
                book.setAuthor(rs.getString("author"));
                book.setEdition(rs.getInt("edition"));
                book.setNotes(rs.getString("notes"));
                book.setStatus(Status.valueOf(rs.getString("status")));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static void deleteBook(int userId, int bookId) {
        String sql = "DELETE FROM books WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);
            int rows = pstmt.executeUpdate();

            if (rows > 0) System.out.println("Book deleted successfully.");
            else System.out.println("Book not found or you don't have permission.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateBookStatus(int userId, int bookId, Status newStatus, String newNotes) {
        String sql = "UPDATE books SET status = ?, notes = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus.toString());
            pstmt.setString(2, newNotes);
            pstmt.setInt(3, bookId);
            pstmt.setInt(4, userId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Book updated successfully.");
            else System.out.println("Update failed.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}