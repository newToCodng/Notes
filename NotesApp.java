import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class NotesApp {
    public static void main(String[] arg) {
        Scanner scanner = new Scanner(System.in);
        NotesManager manager = new NotesManager();

        while (true) {
            System.out.println("\n📝 Notes App");
            System.out.println(" 1. Add Note");
            System.out.println(" 2. View Notes");
            System.out.println(" 3. Exit");
            System.out.println(" Enter your choice below");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> manager.addNote(scanner);
                case 2 -> manager.viewNotes();
                case 3 -> {
                    System.out.println(" Exiting...");
                    System.exit(0);
                }
            }

        }



    }
}

class Note {
    private String title;
    private String content;

    Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String toFileFormat() {
        return title + "|" + content;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\nContent: " + content;
    }
}

class NotesManager {

    public void addNote (Scanner scanner) {
        System.out.println("Enter note title: ");
        String title = scanner.nextLine();

        System.out.println("Enter note content: ");
        String content = scanner.nextLine();

        Note newNote = new Note(title, content);

        try {
            NoteFileManager.saveNoteToFile(newNote);
            System.out.println("Note saved");
            System.out.println(newNote);
        } catch (IOException e) {
            System.out.println("Error saving note: " + e.getMessage());
        }
    }

    public void viewNotes(){
        try {
            NoteFileManager.displayNotesFromFile();
        } catch (IOException e) {
            System.out.println("Error displaying note: " + e.getMessage());
        }

    }
}

class NoteFileManager {
    public static void saveNoteToFile(Note note)  throws IOException {
        try (FileWriter writer = new FileWriter("notes.txt", true)){
            writer.write(note.toFileFormat() + "\n");
        }
    }

    public static void displayNotesFromFile() throws IOException {
        File file = new File("notes.txt");
        if (!file.exists() || file.length() == 0){
            System.out.println("No notes available");
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] noteParts = line.split("\\|");
                if (noteParts.length ==2) {
                    System.out.println(new Note(noteParts[0], noteParts[1]));
                } else {
                    System.out.println("Warning: Skipping invalid note entry -> " + line);
                }
            }
        }
    }
}

