import static spark.Spark.*;
import java.io.*;
import java.util.*;

public class RoutineManager {
    public static void main(String[] args) {
        staticFiles.location("/public");

        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        post("/getRoutine", (req, res) -> {
            String batch = req.queryParams("batch");
            String section = req.queryParams("section");
            String day = req.queryParams("day");

            String key = batch + "-" + section + "-" + day;
            String routine = getRoutineFromFile(key);

            StringBuilder response = new StringBuilder();
            if (routine != null) {
                response.append("<h2>Routine for Batch: ").append(batch)
                        .append(", Section: ").append(section)
                        .append(", Day: ").append(day).append("</h2>");

                String[] courses = routine.split("\\|");
                response.append("<ul>");
                for (String course : courses) {
                    String[] details = course.split(",");
                    if (details.length == 4) {
                        response.append("<li><strong>Course:</strong> ").append(details[0])
                                .append("<br><strong>Teacher:</strong> ").append(details[1])
                                .append("<br><strong>Time:</strong> ").append(details[2])
                                .append("<br><strong>Classroom:</strong> ").append(details[3])
                                .append("</li><br>");
                    }
                }
                response.append("</ul>");
            } else {
                response.append("<p>No routine found for the given input.</p>");
            }

            saveToHtml(response.toString());
            return "Routine saved to result.html";
        });
    }

    private static void saveToHtml(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.html"))) {
            writer.write("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
            writer.write("<meta charset=\"UTF-8\">\n<title>Routine Result</title>\n");
            writer.write("<style> body { font-family: Arial, sans-serif; margin: 20px; }</style>\n");
            writer.write("</head>\n<body>\n");
            writer.write(content);
            writer.write("</body>\n</html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRoutineFromFile(String key) {
        Map<String, String> routineData = loadRoutineData();
        return routineData.getOrDefault(key, null);
    }

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
}
