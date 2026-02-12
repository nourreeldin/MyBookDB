import java.io.Console;
import java.util.List;
import java.util.Scanner;

public class CLI {
    private static Scanner scanner = new Scanner(System.in);
    private static Console console = System.console();
    private static int loggedInUserId = -1;

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String DIM = "\u001B[2m";

    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            System.out.println("UTF-8 not supported");
        }

        printWelcomeBanner();

        while (true) {
            if (loggedInUserId == -1) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void printWelcomeBanner() {
        System.out.println(CYAN +
                "    ___  ___      ______             _    \n" +
                "    |  \\/  |      | ___ \\           | |   \n" +
                "    | .  . |_   _ | |_/ / ___   ___ | | __\n" +
                "    | |\\/| | | | || ___ \\/ _ \\ / _ \\| |/ /\n" +
                "    | |  | | |_| || |_/ / (_) | (_) |   < \n" +
                "    \\_|  |_/\\__, |\\____/ \\___/ \\___/|_|\\_\\\n" +
                "             __/ |                        \n" +
                "            |___/                         " + RESET);

        System.out.println(DIM + "\n=========================================" + RESET);
        System.out.println(GREEN + "    Database Management System v1.0" + RESET);
        System.out.println(DIM + "=========================================\n" + RESET);

        printSystemInfo();
    }

    private static void printSystemInfo() {
        String username = System.getProperty("user.name");
        String os = System.getProperty("os.name");
        String javaVersion = System.getProperty("java.version");

        System.out.println(CYAN + "OS: " + RESET + os);
        System.out.println(CYAN + "User: " + RESET + username);
        System.out.println(CYAN + "Java: " + RESET + javaVersion);
        System.out.println(CYAN + "Status: " + RESET + GREEN + "Connected" + RESET);
        System.out.println();
    }

    private static void showAuthMenu() {
        System.out.println(YELLOW + "~[" + CYAN + "MyBook" + YELLOW + "]--[" + RED + "~" + YELLOW + "]" + RESET);
        System.out.println(YELLOW + " |__ " + GREEN + "> " + RESET + "Authentication Required\n");

        System.out.println(BLUE + "  [1]" + RESET + " -> Login");
        System.out.println(BLUE + "  [2]" + RESET + " -> Register");
        System.out.println(BLUE + "  [3]" + RESET + " -> " + RED + "Exit" + RESET);

        String choice = prompt(GREEN + "~[" + CYAN + "choice" + GREEN + "]\n |__>" + RESET);

        if (choice.equals("3")) {
            System.out.println(RED + "\n[!] " + RESET + "Shutting down database connection...");
            DBConnection.closeConnection();
            System.exit(0);
        }

        System.out.println();
        String username = prompt(CYAN + " |--[" + WHITE + "username" + CYAN + "]-->" + RESET);
        String password = promptPassword(CYAN + " |__[" + WHITE + "password" + CYAN + "]-->" + RESET);

        if(username.isEmpty() || password.isEmpty()) {
            System.out.println(RED + "[-] " + RESET + "Invalid credentials!");
            return;
        }

        if (choice.equals("1")) {
            loggedInUserId = Scripts.loginUser(username, password);
            if (loggedInUserId != -1) {
                System.out.println(GREEN + "\n[+] " + RESET + "Authentication successful");
                System.out.println(GREEN + "[+] " + RESET + "Session initialized for user: " + CYAN + username + RESET);
            } else {
                System.out.println(RED + "\n[-] " + RESET + "Authentication failed");
                System.out.println(RED + "[-] " + RESET + "Invalid credentials");
            }
        } else if (choice.equals("2")) {
            User newUser = new User(username, password);
            if (Scripts.registerUser(newUser)) {
                System.out.println(GREEN + "\n[+] " + RESET + "User registered successfully");
                System.out.println(YELLOW + "[!] " + RESET + "Please login to continue");
            } else {
                System.out.println(RED + "\n[-] " + RESET + "Registration failed");
                System.out.println(RED + "[-] " + RESET + "Username may already exist");
            }
        }
    }

    private static void showMainMenu() {
        System.out.println(YELLOW + "\n~[" + CYAN + "MyBook-DB" + YELLOW + "]--[" + GREEN + "user-" + loggedInUserId + YELLOW + "]" + RESET);
        System.out.println(YELLOW + " |__ " + GREEN + "> " + RESET + "Database Operations\n");

        System.out.println(GREEN + "  [+]" + RESET + " 1 -> Add Book");
        System.out.println(CYAN + "  [=]" + RESET + " 2 -> Query Books");
        System.out.println(RED + "  [-]" + RESET + " 3 -> Delete Book");
        System.out.println(YELLOW + "  [*]" + RESET + " 4 -> Update Record");
        System.out.println(PURPLE + "  [<]" + RESET + " 5 -> Logout");

        String choice = prompt(GREEN + "\n~[" + CYAN + "operation" + GREEN + "]\n |__>" + RESET);

        switch (choice) {
            case "1":
                addBookFlow();
                break;
            case "2":
                listBooksFlow();
                break;
            case "3":
                deleteBookFlow();
                break;
            case "4":
                updateBookFlow();
                break;
            case "5":
                loggedInUserId = -1;
                System.out.println(YELLOW + "\n[!] " + RESET + "Session terminated");
                System.out.println(YELLOW + "[!] " + RESET + "Logged out successfully");
                break;
            default:
                System.out.println(RED + "[-] " + RESET + "Invalid operation");
        }
    }

    private static void addBookFlow() {
        System.out.println(GREEN + "\n[+] " + RESET + "INSERT INTO books");
        System.out.println(DIM + "    -------------------------\n" + RESET);

        String name = prompt(CYAN + " |--[" + WHITE + "book_name" + CYAN + "]-->" + RESET);
        String author = prompt(CYAN + " |--[" + WHITE + "author" + CYAN + "]---->" + RESET);

        int edition = 1;
        try {
            String edInput = prompt(CYAN + " |--[" + WHITE + "edition" + CYAN + "]--->" + RESET);
            edition = Integer.parseInt(edInput);
        } catch (NumberFormatException e) {
            System.out.println(YELLOW + "[!] " + RESET + "Invalid input, defaulting to edition 1");
        }

        String notes = prompt(CYAN + " |__[" + WHITE + "notes" + CYAN + "]----->" + RESET);

        Book newBook = new Book(name, author, edition, notes, Status.NotRead);
        Scripts.addBook(loggedInUserId, newBook);
    }

    private static void listBooksFlow() {
        List<Book> books = Scripts.listBooks(loggedInUserId);

        System.out.println(CYAN + "\n[=] " + RESET + "SELECT * FROM books WHERE user_id=" + loggedInUserId);
        System.out.println(DIM + "    --------------------------------------------------------------------------------------------" + RESET);

        if (books.isEmpty()) {
            System.out.println(YELLOW + "\n    [!] Query returned 0 rows" + RESET);
            System.out.println(DIM + "    (Execute INSERT operation to add records)\n" + RESET);
        } else {
            System.out.println(GREEN + "\n    [+] Query returned " + books.size() + " rows\n" + RESET);

            System.out.println(CYAN + "    +------+---------------------------+----------------------+--------------+----------------------+" + RESET);
            System.out.printf(CYAN + "    | %-4s | %-25s | %-20s | %-12s | %-20s |%n" + RESET,
                    "ID", "NAME", "AUTHOR", "STATUS", "NOTES");
            System.out.println(CYAN + "    +------+---------------------------+----------------------+--------------+----------------------+" + RESET);

            for (int i = 0; i < books.size(); i++) {
                Book b = books.get(i);
                String rowColor = (i % 2 == 0) ? WHITE : DIM;
                System.out.printf(rowColor + "    | " + YELLOW + "%-4d" + rowColor + " | %-25s | %-20s | %-21s | %-20s |%n" + RESET,
                        b.getId(),
                        truncate(b.getName(), 25),
                        truncate(b.getAuthor(), 20),
                        formatStatus(b.getStatus()),
                        truncate(b.getNotes(), 20));
            }

            System.out.println(CYAN + "    +------+---------------------------+----------------------+--------------+----------------------+" + RESET);
        }
    }

    private static void deleteBookFlow() {
        listBooksFlow();

        System.out.println(RED + "\n[-] " + RESET + "DELETE FROM books");
        System.out.println(DIM + "    -------------------------\n" + RESET);

        try {
            int id = Integer.parseInt(prompt(RED + " |__[" + WHITE + "record_id" + RED + "]-->" + RESET));
            Scripts.deleteBook(loggedInUserId, id);
        } catch (NumberFormatException e) {
            System.out.println(RED + "[-] " + RESET + "Invalid ID format");
        }
    }

    private static void updateBookFlow() {
        listBooksFlow();

        System.out.println(YELLOW + "\n[*] " + RESET + "UPDATE books SET");
        System.out.println(DIM + "    -------------------------\n" + RESET);

        try {
            int id = Integer.parseInt(prompt(YELLOW + " |--[" + WHITE + "record_id" + YELLOW + "]-->" + RESET));

            System.out.println();
            System.out.println(CYAN + "    Select new status:" + RESET);
            System.out.println("    " + RED + "[1]" + RESET + " NotRead");
            System.out.println("    " + YELLOW + "[2]" + RESET + " InProgress");
            System.out.println("    " + GREEN + "[3]" + RESET + " Done");

            String statusChoice = prompt(YELLOW + " |--[" + WHITE + "status" + YELLOW + "]----->" + RESET);

            Status status = Status.NotRead;
            if (statusChoice.equals("2")) status = Status.InProgress;
            if (statusChoice.equals("3")) status = Status.Done;

            String notes = prompt(YELLOW + " |__[" + WHITE + "notes" + YELLOW + "]------>" + RESET);

            Scripts.updateBookStatus(loggedInUserId, id, status, notes);
        } catch (NumberFormatException e) {
            System.out.println(RED + "[-] " + RESET + "Invalid ID format");
        }
    }

    private static String prompt(String label) {
        System.out.print(label + " ");
        return scanner.nextLine();
    }

    private static String promptPassword(String label) {
        System.out.print(label + " ");

        if (console != null) {
            char[] passwordArray = console.readPassword();
            return new String(passwordArray);
        } else {
            try {
                StringBuilder password = new StringBuilder();
                while (true) {
                    char c = (char) System.in.read();
                    if (c == '\n' || c == '\r') {
                        System.out.println();
                        break;
                    } else if (c == '\b' || c == 127) {
                        if (password.length() > 0) {
                            password.deleteCharAt(password.length() - 1);
                            System.out.print("\b \b");
                        }
                    } else {
                        password.append(c);
                        System.out.print("*");
                    }
                }
                return password.toString();
            } catch (Exception e) {
                return scanner.nextLine();
            }
        }
    }

    private static String formatStatus(Status status) {
        switch (status) {
            case Done:
                return GREEN + "DONE       " + RESET;
            case InProgress:
                return YELLOW + "IN_PROGRESS" + RESET;
            case NotRead:
                return RED + "NOT_READ   " + RESET;
            default:
                return status.toString();
        }
    }

    private static String truncate(String str, int width) {
        if (str == null) return "";
        if (str.length() > width) {
            return str.substring(0, width - 3) + "...";
        }
        return str;
    }
}