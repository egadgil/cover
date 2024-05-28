package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class CoverLetterApp {
    private JTextField nameField;
    private JTextField positionField;
    private JTextField companyField;
    private JTextField skillsField;
    private JTextField experiencesField;
    private JTextArea jobDescriptionArea;
    private JTextArea coverLetterArea;
    private JButton generateButton;
    private JButton uploadResumeButton;
    private JPanel mainPanel;
    private File selectedFile;

    public CoverLetterApp() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(8, 2));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Position:"));
        positionField = new JTextField();
        inputPanel.add(positionField);

        inputPanel.add(new JLabel("Company:"));
        companyField = new JTextField();
        inputPanel.add(companyField);

        inputPanel.add(new JLabel("Skills:"));
        skillsField = new JTextField();
        inputPanel.add(skillsField);

        inputPanel.add(new JLabel("Experiences:"));
        experiencesField = new JTextField();
        inputPanel.add(experiencesField);

        inputPanel.add(new JLabel("Job Description:"));
        jobDescriptionArea = new JTextArea(5, 20);
        JScrollPane jobDescriptionScrollPane = new JScrollPane(jobDescriptionArea);
        inputPanel.add(jobDescriptionScrollPane);

        uploadResumeButton = new JButton("Upload Resume");
        inputPanel.add(uploadResumeButton);

        generateButton = new JButton("Generate Cover Letter");
        inputPanel.add(generateButton);

        coverLetterArea = new JTextArea(10, 40);
        coverLetterArea.setLineWrap(true);
        coverLetterArea.setWrapStyleWord(true);
        coverLetterArea.setEditable(false);
        JScrollPane coverLetterScrollPane = new JScrollPane(coverLetterArea);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(coverLetterScrollPane, BorderLayout.CENTER);

        uploadResumeButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            }
        });

        generateButton.addActionListener(e -> {
            String name = nameField.getText();
            String position = positionField.getText();
            String company = companyField.getText();
            String skills = skillsField.getText();
            String experiences = experiencesField.getText();
            String jobDescription = jobDescriptionArea.getText();
            String resumeContent = "";

            if (selectedFile != null) {
                try {
                    resumeContent = OpenAICoverLetterGenerator.extractTextFromPDF(selectedFile.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    coverLetterArea.setText("Error reading resume file");
                    return;
                }
            }

            try {
                String coverLetter = OpenAICoverLetterGenerator.generateCoverLetter(name, position, company, skills, experiences, jobDescription, resumeContent);
                coverLetterArea.setText(coverLetter);
            } catch (IOException ex) {
                ex.printStackTrace();
                coverLetterArea.setText("Error generating cover letter");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cover Letter Generator");
        frame.setContentPane(new CoverLetterApp().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
