package org.example;

import okhttp3.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class OpenAICoverLetterGenerator {
    private static final String API_KEY = "sk-proj-xxxxxxxxxxxxx";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions"; // Use the chat completions endpoint

    public static String generateCoverLetter(String name, String position, String company, String skills, String experiences, String jobDescription, String resumeContent) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", String.format(
                "Write a professional cover letter for the following job application:\n\nName: %s\nPosition: %s\nCompany: %s\nSkills: %s\nExperiences: %s\nJob Description: %s\nResume: %s\n\nCover Letter:",
                name, position, company, skills, experiences, jobDescription, resumeContent));

        JsonArray messages = new JsonArray();
        messages.add(message);

        JsonObject json = new JsonObject();
        json.addProperty("model", "gpt-3.5-turbo");
        json.add("messages", messages);
        json.addProperty("max_tokens", 500);
        json.addProperty("temperature", 0.7);

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        System.out.println("Request URL: " + API_URL);
        System.out.println("Authorization Header: Bearer " + API_KEY);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
            return responseJson.getAsJsonArray("choices").get(0).getAsJsonObject().get("message").getAsJsonObject().get("content").getAsString().trim();
        }
    }

    public static String extractTextFromPDF(String pdfPath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    public static void main(String[] args) {
        String name = "Yongyun Song";
        String position = "Software Engineer";
        String company = "OpenAI";
        String skills = "Python, Machine Learning, Data Analysis";
        String experiences = "Worked on various machine learning projects, developed data analysis tools.";
        String jobDescription = "Develop software solutions, collaborate with cross-functional teams, and apply machine learning algorithms to real-world problems.";

        // Load resume from PDF file
        String resumePath = "path/to/your/resume.pdf";
        String resumeContent = "";
        try {
            resumeContent = extractTextFromPDF(resumePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String coverLetter = generateCoverLetter(name, position, company, skills, experiences, jobDescription, resumeContent);
            System.out.println("Generated Cover Letter:\n" + coverLetter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
