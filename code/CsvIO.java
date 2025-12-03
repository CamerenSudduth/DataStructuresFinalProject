package edu.hcu.triage;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import static java.nio.file.StandardOpenOption.CREATE;

/** Minimal CSV import/export using standard IO. */
public final class CsvIO {
    // TODO: loadPatients(Path csv, PatientRegistry reg)
    // Load patients from CSV into PatientRegistry, return warnings
    //   - Expect header: id,name,age,severity
    //   - Trim fields; skip blanks; validate; warn on malformed lines with line numbers
    public static List<String> loadPatients(Path csv, PatientRegistry reg) {
        List<String> warnings = new ArrayList<>();

        if (csv == null) { // Checks if the csv path is null
            throw new IllegalArgumentException("CSV file cannot be null");
        }
        if (reg == null) { // Checks if the patient registry is null
            throw new IllegalArgumentException("PatientRegistry cannot be null");
        }
        //Opens the csv file for reading using the buffer
        try (BufferedReader reader = Files.newBufferedReader(csv)) {
            String line;
            int lineNum = 0; //Current Line tracker for warning messages

            //Reads the file line by line
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim(); //
                if (line.isEmpty()) { // skips blank lines in file
                    continue;
                }

                // Check for header (space-insensitive)
                if (lineNum == 1) {
                    // removes all the spaces and compares it to the expected header of id,name,age,severity
                    if (!line.replaceAll("\\s","").equalsIgnoreCase("id,name,age,severity")) {
                        warnings.add("Unexpected header on line 1: " + line);
                    }
                    continue; //skips past the header line
                }

                // Split CSV line, allow empty fields
                String[] fields = line.split(",", -1); //splits csv line into array by comma
                if (fields.length != 4) { // Check that only id, name, age, severity are columns
                    warnings.add("Malformed line on line: " + lineNum + ": " + line);
                    continue;
                }

                //removes any white space found around id or name in file
                String id = fields[0].trim();
                String name = fields[1].trim();

                if (id.isEmpty() || name.isEmpty()) { //checks if these attributes are null
                    warnings.add("Missing ID or name on line " + lineNum);
                    continue;
                }

                // Validate age
                int age;
                try {
                    age = Integer.parseInt(fields[2].trim()); //converts age into an integer
                    if  (age < 0 || age > 120) {//checks if age is within constraints
                        throw new NumberFormatException("Age must be between 0 and 120");
                    }
                } catch (NumberFormatException e) { // appends warning message if age is not valid
                    warnings.add("Invalid age on line " + lineNum + ": " + fields[2].trim());
                    continue; //skips bad record
                }

                // Validate severity
                int severity;
                try {
                    severity = Integer.parseInt(fields[3].trim()); //converts severity into an integer
                    if  (severity < 1 || severity > 5) { // checks if severity is within constraints
                        throw new NumberFormatException("Severity is not 1-5 on line " + lineNum + ": " + fields[3].trim());
                    }
                } catch (NumberFormatException e) { //appends warning message if severity is not valid
                    warnings.add("Invalid severity on line " + lineNum + ": " + fields[3].trim());
                    continue; //skips bad record
                }

                // Add patient to registry
                reg.registerNew(id, name, age, severity);
            }
        } catch (IOException e) {
            //captures the file read failures
            warnings.add("Error reading CSV file: " + e.getMessage());
        }
        return warnings; //returns warnings gathered during reading the file
    }

    // TODO: exportLog(Path csv, List<TreatedCase> cases)
    //   - Write ISO-8601 times; escape commas in notes if needed
    public static void exportLog(Path csv, List<TreatedCase> cases) {
        //opens file for writing and creates the file if its yet to exist
        try (BufferedWriter writer = Files.newBufferedWriter(csv, CREATE)) {

            // Write header
            writer.write("id,name,age,severity,start,end,outcome,notes");
            writer.newLine();

            // Write each treated case as a CSV row
            for (TreatedCase c : cases) {
                Patient p = c.getPatient(); //gets patient associated with this treatment

                // Get notes and escape commas by quoting the field
                String notes = c.getNotes();
                if (notes.contains(",")) {
                    // Replace embedded quotes and wrap entire field in quotes
                    notes = "\"" + notes.replace("\"", "\"\"") + "\""; // CSV escape
                }

                // Build a CSV line using comma-separated values
                writer.write(String.join(",",
                        p.getId(),
                        p.getName(),
                        String.valueOf(p.getAge()),
                        String.valueOf(p.getSeverity()),
                        c.getStart().toString(),  // ISO-8601 formatted start time
                        c.getEnd().toString(), // ISO-8601 formatted end time
                        c.getOutcome().name(), // writes enum converted into a string
                        notes
                ));

                // Move to next line in the output file
                writer.newLine();
            }

        } catch (IOException e) {
            //Throws exception if unable to write to csv file for whatever reason
            throw new RuntimeException("Error writing log: " + e.getMessage(), e);
        }
    }
}
