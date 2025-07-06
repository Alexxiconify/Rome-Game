import com.romagame.map.ProvinceData;
import com.romagame.map.ProvinceDataLoader;
import java.util.Map;

public class test_province_data {
    public static void main(String[] args) {
        System.out.println("Testing province data loading from JSON...");
        
        try {
            Map<String, ProvinceData> provinceData = ProvinceDataLoader.loadProvinceData("province_data.json");
            Map<String, String> maskColorToProvinceId = ProvinceDataLoader.buildMaskColorToProvinceId(provinceData);
            Map<String, String> ownerColorToNation = ProvinceDataLoader.buildOwnerColorToNation(provinceData);
            
            System.out.println("Successfully loaded " + provinceData.size() + " provinces");
            System.out.println("Mask color mappings: " + maskColorToProvinceId.size());
            System.out.println("Owner color mappings: " + ownerColorToNation.size());
            
            // Show first few provinces as examples
            int count = 0;
            for (Map.Entry<String, ProvinceData> entry : provinceData.entrySet()) {
                if (count >= 5) break;
                
                String provinceId = entry.getKey();
                ProvinceData data = entry.getValue();
                
                System.out.println("\nProvince: " + provinceId);
                System.out.println("  Mask Color: [" + data.mask_color[0] + ", " + data.mask_color[1] + ", " + data.mask_color[2] + "]");
                System.out.println("  Owner Color: [" + data.owner_color[0] + ", " + data.owner_color[1] + ", " + data.owner_color[2] + "]");
                System.out.println("  Nation: " + data.nation);
                
                count++;
            }
            
            System.out.println("\nProvince data loading test completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error loading province data: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 