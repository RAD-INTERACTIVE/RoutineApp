import static spark.Spark.*;

import java.io.*;
import java.util.*;

public class RoutineManager {
    public static void main(String[] args) {
        // Serve static files (HTML, CSS) from the "public" folder
        staticFiles.location("/public");

        // Route for the home page
        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        // Handle form submissions to fetch routine
        post("/getRoutine", (req, res) -> {
            String batch = req.queryParams("batch");
            String section = req.queryParams("section");
            String day = req.queryParams("day");

            // Fetch routine from routine.txt
            String key = batch + "-" + section + "-" + day;
            String routine = getRoutineFromFile(key);

            if (routine != null) {
                // Split the routine into individual courses
                String[] courses = routine.split("\\|");
                StringBuilder response = new StringBuilder();
                response.append("<h2>Routine for Batch: ").append(batch).append(", Section: ").append(section).append(", Day: ").append(day).append("</h2>");

                // Loop through each course and add details to the response
                for (String course : courses) {
                    String[] details = course.split(",");
                    String courseName = details[0];
                    String teacher = details[1];
                    String time = details[2];
                    String classroom = details[3];

                    response.append("<div>")
                            .append("<p><strong>Course:</strong> ").append(courseName).append("</p>")
                            .append("<p><strong>Teacher:</strong> ").append(teacher).append("</p>")
                            .append("<p><strong>Time:</strong> ").append(time).append("</p>")
                            .append("<p><strong>Classroom:</strong> ").append(classroom).append("</p>")
                            .append("</div>");
                }

                return response.toString();
            } else {
                return "<p>No routine found for the given input.</p>";
            }
        });

        // Handle form submissions to update routine
        post("/updateRoutine", (req, res) -> {
            String batch = req.queryParams("batch");
            String section = req.queryParams("section");
            String day = req.queryParams("day");
            String routine = req.queryParams("routine");

            // Update the routine in the file
            updateRoutine(batch, section, day, routine);

            return "Routine updated successfully!";
        });
    }

    // Method to fetch routine from routine.txt
    private static String getRoutineFromFile(String key) {
        Map<String, String> routineData = loadRoutineData();
        return routineData.getOrDefault(key, null);
    }

    // Method to update routine in routine.txt
    private static void updateRoutine(String batch, String section, String day, String routine) {
        Map<String, String> routineData = loadRoutineData();
        String key = batch + "-" + section + "-" + day;
        routineData.put(key, routine);
        saveRoutineData(routineData);
    }

    // Method to load routine data from routine.txt
    private static Map<String, String> loadRoutineData() {
        Map<String, String> routineData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("routine.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    routineData.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return routineData;
    }

    // Method to save routine data to routine.txt
    private static void saveRoutineData(Map<String, String> routineData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("routine.txt"))) {
            for (Map.Entry<String, String> entry : routineData.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}