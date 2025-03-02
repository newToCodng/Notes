import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class NotesApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        NotesManager manager = new NotesManager();

        while (true) {
            printMenu();
            int choice = getValidChoice(scanner);
            handleChoice(choice, scanner, manager);
        }
    }

    private static void printMenu() {
        System.out.println("\n📝 Notes App");
        System.out.println("1. Add Note");
        System.out.println("2. View Notes");
        System.out.println("3. Exit");
    }

    // Validate user input
    private static int getValidChoice(Scanner scanner) {
        while (true) {
            System.out.print("Enter choice (1-3): ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter 1-3");
            }
        }
    }

    // Route user input
    private static void handleChoice(int choice, Scanner scanner, NotesManager manager) {
        switch (choice) {
            case 1 -> manager.addNote(scanner);
            case 2 -> manager.viewNotes();
            case 3 -> {
                System.out.println("Exiting...");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice!");
        }
    }
}

class Note {
    private final String title;
    private final String content;

    Note(String title, String content) {
        this.title = title.trim();
        this.content = content.trim();
    }

    String toFileFormat() {
        return escape(title) + "|" + escape(content); // Escape pipes
    }

    private String escape(String text) {
        return text.replace("|", "\\|"); // Handle delimiter conflicts
    }

    @Override
    public String toString() {
        return "Title: " + title + "\nContent: " + content + "\n";
    }
}

class NotesManager {
    private final NoteFileManager fileManager = new NoteFileManager();

    public void addNote(Scanner scanner) {
        try {
            String title = getNonEmptyInput(scanner, "Enter note title: ");
            if (fileManager.noteExists(title)) {
                System.out.println("❌ Note with title '" + title + "' already exists!");
                return;
            }
            String content = getNonEmptyInput(scanner, "Enter note content: ");

            Note newNote = new Note(title, content);
            fileManager.saveNote(newNote);
            System.out.println("✅ Note saved successfully!");

        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public void viewNotes() {
        fileManager.displayNotes();
    }

    private String getNonEmptyInput(Scanner scanner, String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty!");
            }
        } while (input.isEmpty());
        return input;
    }
}

class NoteFileManager {
    private static final String FILE_NAME = "notes.txt";

    public void saveNote(Note note) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(note.toFileFormat() + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("⚠️ Error saving note: " + e.getMessage());
        }
    }

    public void displayNotes() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No notes available");
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("(?<!\\\\)\\|"); // Split on unescaped pipes

                if (parts.length == 2) {
                    String title = unescape(parts[0]);
                    String content = unescape(parts[1]);
                    System.out.println(new Note(title, content));
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error reading notes: " + e.getMessage());
        }
    }

    private String unescape(String text) {
        return text.replace("\\|", "|");
    }

    public boolean noteExists(String targetTitle) {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("(?<!\\\\)\\|");
                if (parts.length == 2) {
                    String existingTitle = unescape(parts[0]);
                    if (existingTitle.equalsIgnoreCase(targetTitle.trim())) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error checking notes: " + e.getMessage());
        }
        return false;
    }
}
