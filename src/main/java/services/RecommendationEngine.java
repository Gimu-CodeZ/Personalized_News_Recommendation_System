package services;

import database.DatabaseConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationEngine {

    DatabaseConnection databaseConnection = new DatabaseConnection();

    public List<String> recommendCategories(String username){
        List<String> userPreferences = databaseConnection.userPreferences(username);

        // Check if preferences are empty
        if (userPreferences == null || userPreferences.isEmpty()) {
            System.out.println("No preferences found for user: " + username);
        }

        // Parse preferences into a map of category and score
        Map<String, Integer> userPreferencesMap = new HashMap<>();
        for (int i = 0; i < userPreferences.size(); i += 2) {
            String category = userPreferences.get(i);
            int score = Integer.parseInt(userPreferences.get(i + 1));
            userPreferencesMap.put(category, score);
        }

        // Sort categories based on scores in descending order
        List<Map.Entry<String, Integer>> sortedCategories = new ArrayList<>(userPreferencesMap.entrySet());
        sortedCategories.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        List<String> topCategories = new ArrayList<>();

        // Recommend the top 3 categories
        for (int i = 0; i < Math.min(3, sortedCategories.size()); i++) {
            System.out.println((i + 1) + ". " + sortedCategories.get(i).getKey());
            topCategories.add(sortedCategories.get(i).getKey());
        }

        System.out.println("Top 3 recommended categories for user: " + username);
        System.out.println(topCategories);

        return topCategories;

    }

}
