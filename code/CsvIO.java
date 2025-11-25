package edu.hcu.triage;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Minimal CSV import/export using standard IO. */
public final class CsvIO {
    // TODO: loadPatients(Path csv, PatientRegistry reg)
    // Load patients from CSV into PatientRegistry, return warnings
    public static List<String> loadPatients(Path csv, PatientRegistry reg) {
        List<String> warnings = new ArrayList<>();

        if (csv == null) { // Checks if the csv path is null
            throw new IllegalArgumentException("CSV file cannot be null");
        }
        if (reg == null) { // Checks if the patient registry is null
            throw new IllegalArgumentException("PatientRegistry cannot be null");
        }
        try (BufferedReader reader = Files.newBufferedReader(csv)) {
            String line;
            int lineNum = 0;

            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // Check for header (space-insensitive)
                if (lineNum == 1) {
                    if (!line.replaceAll("\\s","").equalsIgnoreCase("id,name,age,severity")) {
                        warnings.add("Unexpected header on line 1: " + line);
                    }
                    continue;
                }

                // Split CSV line, allow empty fields
                String[] fields = line.split(",", -1);
                if (fields.length != 4) { // Check that only id, name, age, severity are columns
                    warnings.add("Malformed line on line: " + lineNum + ": " + line);
                    continue;
                }

                String id = fields[0].trim();
                String name = fields[1].trim();

                if (id.isEmpty() || name.isEmpty()) {
                    warnings.add("Missing ID or name on line " + lineNum);
                    continue;
                }

                // Validate age
                int age;
                try {
                    age = Integer.parseInt(fields[2].trim());
                    if  (age < 0 || age > 120) {
                        throw new NumberFormatException("Age must be between 0 and 120");
                    }
                } catch (NumberFormatException e) {
                    warnings.add("Invalid age on line " + lineNum + ": " + fields[2].trim());
                    continue;
                }

                // Validate severity
                int severity;
                try {
                    severity = Integer.parseInt(fields[3].trim());
                    if  (severity < 1 || severity > 5) {
                        throw new NumberFormatException("Severity is not 1-5 on line " + lineNum + ": " + fields[3].trim());
                    }
                } catch (NumberFormatException e) {
                    warnings.add("Invalid severity on line " + lineNum + ": " + fields[3].trim());
                    continue;
                }

                // Add patient to registry
                reg.registerNew(id, name, age, severity);
            }
        } catch (IOException e) {
            warnings.add("Error reading CSV file: " + e.getMessage());
        }
        return warnings;
    }
    //   - Expect header: id,name,age,severity
    //   - Trim fields; skip blanks; validate; warn on malformed lines with line numbers
    // TODO: exportLog(Path csv, List<TreatedCase> cases)
    //   - Write ISO-8601 times; escape commas in notes if needed
    public void exportLog(Path csv, List<TreatedCase> cases){
        String s = ""; //Initializes String variable
        try{
            OutputStream output = new BufferedOutputStream(Files.newOutputStream(csv,CREATE)); // creates output stream
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output)); // creates writer for stream

            for(TreatedCase c: cases){ //iterates through number of cases in the LinkedList
                // Calls patient toString method to make it possible to write to file
                s =  String.valueOf(c.getPatient());
                writer.write(s,0,s.length()); //Writes string s to file
                writer.newLine(); // moves to the next line in the file
            }
            writer.close(); //closes the file to not cause data leaks
        }catch(Exception e){
            throw new RuntimeException(e); // throws exception if there is a problem

        }

    }
}
}
