package test;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class TestProvinceMapping {
    public static void main(String[] args) {
        System.out.println("Testing province mapping...");
        
        // Test loading the province mask
        try {
            File maskFile = new File("province_mask.png");
            if (maskFile.exists()) {
                BufferedImage provinceMask = ImageIO.read(maskFile);
                System.out.println("✓ Province mask loaded: " + provinceMask.getWidth() + "x" + provinceMask.getHeight());
                
                // Test loading the CSV mapping
                Map<Integer, String> colorToProvinceId = new HashMap<>();
                try (BufferedReader br = new BufferedReader(new FileReader("resources/province_color_map.csv"))) {
                    String line;
                    boolean firstLine = true;
                    int count = 0;
                    while ((line = br.readLine()) != null) {
                        if (firstLine) {
                            firstLine = false;
                            continue; // skip header
                        }
                        String[] parts = line.split(",");
                        if (parts.length >= 3) {
                            try {
                                long longValue = Long.parseLong(parts[0].trim());
                                int argb = (int) longValue;
                                String provinceId = parts[2].trim();
                                colorToProvinceId.put(argb, provinceId);
                                count++;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid ARGB value in mapping: " + parts[0]);
                            }
                        }
                    }
                    System.out.println("✓ Loaded " + count + " province color mappings");
                    
                    // Test a few sample lookups
                    System.out.println("\nSample province mappings:");
                    int samples = 0;
                    for (Map.Entry<Integer, String> entry : colorToProvinceId.entrySet()) {
                        if (samples < 5) {
                            int argb = entry.getKey();
                            int r = (argb >> 16) & 0xFF;
                            int g = (argb >> 8) & 0xFF;
                            int b = argb & 0xFF;
                            System.out.println("  ARGB=" + argb + " RGB=(" + r + "," + g + "," + b + ") -> " + entry.getValue());
                            samples++;
                        } else {
                            break;
                        }
                    }
                    
                    // Test finding a province at a specific pixel
                    int testX = 100, testY = 100;
                    if (testX < provinceMask.getWidth() && testY < provinceMask.getHeight()) {
                        int argb = provinceMask.getRGB(testX, testY);
                        String provinceId = colorToProvinceId.get(argb);
                        if (provinceId != null) {
                            System.out.println("✓ Province at (" + testX + "," + testY + "): " + provinceId);
                        } else {
                            System.out.println("✗ No province found at (" + testX + "," + testY + ") - ARGB: " + argb);
                        }
                    }
                    
                } catch (Exception e) {
                    System.out.println("✗ Error loading CSV mapping: " + e.getMessage());
                }
                
            } else {
                System.out.println("✗ Province mask file not found: province_mask.png");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        System.out.println("\nTest completed!");
    }
} 