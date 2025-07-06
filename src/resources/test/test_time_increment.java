package test;
import com.romagame.core.GameEngine;
import com.romagame.core.GameDate;
public class test_time_increment {
    public static void main(String[] args) {
        System.out.println("Testing time increment functionality...");
        
        // Test GameDate advancement
        GameDate date = new GameDate(117, 1, 1);
        System.out.println("Initial date: " + date.getFormattedDate());
        
        for (int i = 0; i < 10; i++) {
            date.advance();
            System.out.println("After " + (i+1) + " updates: " + date.getFormattedDate());
        }
        
        // Test GameEngine with UI callback
        GameEngine engine = new GameEngine();
        engine.setUIUpdateCallback(eng -> {
            System.out.println("UI Update - Date: " + eng.getCurrentDate().getFormattedDate());
        });
        
        System.out.println("\nTesting GameEngine time increment...");
        System.out.println("Initial engine date: " + engine.getCurrentDate().getFormattedDate());
        
        // Simulate a few game updates
        for (int i = 0; i < 5; i++) {
            engine.getCurrentDate().advance();
            System.out.println("Engine date after " + (i+1) + " updates: " + engine.getCurrentDate().getFormattedDate());
        }
        
        System.out.println("\nTime increment test completed successfully!");
    }
} 