package task3.task3.Load;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Dictionary {
    public ArrayList<String> words;
    public File[] languageFiles;
    private String folderPath;
    public Dictionary(){
        words = new ArrayList<>();
        folderPath = "src/dictionary/dictionary";
        languagesSelection();
    }
    private void languagesSelection() {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            languageFiles = folder.listFiles();
            if (languageFiles!= null) {
                System.out.println("Number of files in the folder: " + languageFiles.length);
            } else {
                System.out.println("No file found");
                System.exit(0);
            }
        } else {
            System.out.println("The specified folder does not exist.");
        }
    }
    public void setWords(File file){
        try{
        words = (ArrayList<String>) Files.readAllLines(Paths.get(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
