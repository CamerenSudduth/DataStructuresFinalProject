package edu.hcu.triage;

import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

/** Text-based UI (no GUI). Keep robust and simple. */
public class HospitalApp {

    private final PatientRegistry registry = new PatientRegistry();
    private final TriageQueue triage = new TriageQueue();
    private final TreatmentLog log = new TreatmentLog();
    private final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        new HospitalApp().run(args);
    }

    private void run(String[] args) {
        // TODO: if args.length > 0, attempt to load patients.csv
        if (args.length > 0){
            try{
                Path csvPath = Path.of("Patient.csv");
                List<String> warnings = CsvIO.loadPatients(csvPath, registry);

                if (warnings.isEmpty()) {
                    System.out.println("Patients loaded successfully.\n");
                } else {
                    System.out.println("Patients loaded with warnings:");
                    for (String w : warnings) {
                        System.out.println(" - " + w);
                    }
                    System.out.println();
                }

            }catch (Exception e){
                System.out.println("Loading Patients Failed" + e.getMessage());
                System.out.println();

            }

        }

        // TODO: main loop with menu and input validation
        // Required actions:
        //Declared Variables
        // TODO: helper methods for each menu action
        while (true){
            System.out.println("""
                    Enter Your Option:\s
                    1) Register patient
                    2) Update patient
                    3) Enqueue for triage\s
                    4) Show next patient
                    5) Admit/treat patient
                    6) Show triage order
                    7) Find patient by id
                    8) Show treatment log
                    9) Performance demo
                    10) Export log to CSV
                    0) Exit""");
            String input = in.nextLine();
            int option;

            if(input.isEmpty()){
                continue;
            }

            try{
                option = Integer.parseInt(input);

            }catch (NumberFormatException e){
                System.out.println("Invalid Option");
                continue;
            }


            switch (option){
                case 1: {
                    //  1) Register patient
                    System.out.println("Enter the patients ID: ");
                    String id = in.nextLine();
                    System.out.println("Enter the patients full name: ");
                    String name = in.nextLine();
                    System.out.println("Enter the patients age (0-120): ");
                    //Try Catch blocks allow the system not to crash from invalid user input
                    int age;
                    try {
                        age = Integer.parseInt(in.nextLine());
                        if (age < 0 || age > 120) {
                            System.out.println("Invalid Age Entered: must be between (0-120), Registration Canceled");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Age Entered: Registration Canceled");
                        break;
                    }

                    System.out.println("Enter the severity level of patient: ");
                    System.out.println("1: Immediate: life-threatening condition");
                    System.out.println("2: Emergency: high-risk, vitals in danger zone");
                    System.out.println("3: Urgent: stable condition, multiple resources needed to stabilize patient");
                    System.out.println("4: Semi-Urgent: stable condition, one resource needed");
                    System.out.println("5: Non-Urgent: minor condition, can be treated whenever");
                    //Try Catch blocks allow the system not to crash from invalid user input
                    int severity;
                    try {
                        severity = Integer.parseInt(in.nextLine());
                        if (severity < 1 || severity > 5) {
                            System.out.println("Invalid Severity, Registration Canceled");
                            break;
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Severity, Registration Canceled");
                        break;
                    }

                    registry.registerNew(id, name, age, severity);
                    System.out.println("Patient Registered!");
                    System.out.println();
                    break;
                }case 2: {
                    //  2) Update patient
                    System.out.println("Enter the ID of the patient you want to update:");
                    String id = in.nextLine();
                    if(!registry.contains(id)){
                        System.out.println("Patient is not in the system! Update Canceled");
                        System.out.println();
                        break;
                    }
                    System.out.println("Would you like to update patients name? (Y/N)");
                    String choice = in.nextLine();
                    String name;
                    if (choice.equalsIgnoreCase("Y")){
                        System.out.println("Enter patients new name: ");
                         name = in.nextLine();
                    }else{
                         name = null;
                    }


                    //Try Catch blocks allow the system not to crash from invalid user input
                    int age;
                    System.out.println("Would you like to update patients age? (Y/N)");
                    choice = in.nextLine();
                    if (choice.equalsIgnoreCase("Y")){
                        System.out.println("Enter patients new age: ");
                        try {
                            age = Integer.parseInt(in.nextLine());
                            if (age < 0 || age > 120) {
                                System.out.println("Invalid Age Entered: must be between (0-120), Update Canceled");
                                break;
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Age Entered: Update Canceled");
                            break;
                        }
                    }else{
                        age = -1;
                    }

                    //Try Catch blocks allow the system not to crash from invalid user input
                    int severity;
                    System.out.println("Would you like to update patients severity? (Y/N)");
                    choice = in.nextLine();
                    if (choice.equalsIgnoreCase("Y")){
                        System.out.println("Enter patients new severity level: ");
                        System.out.println("1: Immediate: life-threatening condition");
                        System.out.println("2: Emergency: high-risk, vitals in danger zone");
                        System.out.println("3: Urgent: stable condition, multiple resources needed to stabilize patient");
                        System.out.println("4: Semi-Urgent: stable condition, one resource needed");
                        System.out.println("5: Non-Urgent: minor condition, can be treated whenever");
                        try {
                            severity = Integer.parseInt(in.nextLine());
                            if (severity < 1 || severity > 5) {
                                System.out.println("Invalid Severity, Update Canceled");
                                break;
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Severity Entered: Update Canceled");
                            break;
                        }
                    }else{
                        severity = -1;
                    }
                    registry.updateExisting(id, name, age, severity);
                    System.out.println("Patient Updated!");
                    System.out.println();
                    break;
                }case 3: {
                    //  3) Enqueue for triage (by id)
                    System.out.println("Enter the ID of the patient you want to enqueue:");
                    String id = in.nextLine();
                    if(!registry.contains(id)){
                        System.out.println("Patient is not in the system! Enqueue Canceled");
                        System.out.println();
                        break;
                    }

                    Patient patient = registry.get(id).get();
                    if(triage.snapshotOrder().contains(patient)){
                        System.out.println("Patient is already in queue!");
                    }else{
                        triage.enqueue(patient);
                        System.out.println("Patient ID:"+id+" has been enqueued!");
                    }
                    break;
                }case 4: {
                    //  4) Peek next
                    if (triage.size() < 1) {
                        System.out.println("No more patients in line!");
                        System.out.println();
                    } else {
                        Patient nextPatient = triage.peekNext().get();
                        System.out.println("Next patient in line: [ID,Name,Age,Severity]" +
                                " -> [" + nextPatient + "]");
                        System.out.println();
                    }
                    break;
                }case 5: {
                    //  5) Admit/treat next (capture outcome + notes; append to log)

                    if (triage.size() < 1){
                        System.out.println("No patients to be admitted, line is empty! | Exiting to Menu");
                        System.out.println();
                    }else{
                        Patient currentPatient = triage.dequeueNext().get(); //creates a temp variable of patient dequeued
                        Instant start = Instant.now(); //Gets treatment start time
                        System.out.println("Patient being treated: [ID,Name,Age,Severity] -> ["+ currentPatient+"]");
                        TreatedCase.Outcome outcome = null;
                        while (outcome == null) {
                            System.out.println("""
                                    What was the outcome of the treatment?
                                    1. STABLE\s
                                    2. OBSERVE\s
                                    3. TRANSFER""");
                            String line = in.nextLine();
                            try {
                                int choice = Integer.parseInt(line);
                                if (choice == 1) {
                                    outcome = TreatedCase.Outcome.STABLE;
                                } else if (choice == 2) {
                                    outcome = TreatedCase.Outcome.OBSERVE;
                                } else if (choice == 3) {
                                    outcome = TreatedCase.Outcome.TRANSFER;
                                } else {
                                    System.out.println("Invalid choice: must be 1, 2, or 3.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input: please enter a number choice.");
                            }
                        }

                        System.out.println("Give some information on the treatment process you used");
                        String notes = in.nextLine();
                        while(notes.isBlank()){
                            System.out.println("Notes must be given, please enter notes");
                            notes = in.nextLine();
                        }
                        Instant end = Instant.now();
                        TreatedCase treated = new TreatedCase(currentPatient, start, end, outcome, notes);
                        log.append(treated);
                    }
                    break;
                }case 6:{
                    //  6) Show triage order (non-destructive)
                    if (triage.size() < 1){
                        System.out.println("No patients to be admitted, line is empty! | Exiting to Menu");
                        System.out.println();
                    }else{
                        List<Patient> triageOrder = triage.snapshotOrder();
                        System.out.println("Triage Order:");
                        int position = 1;
                        for (Patient p : triageOrder) {
                            System.out.println(position + ". [ID,Name,Age,Severity] -> [" + p + "]");
                            position++;
                        }
                    }
                    break;
                }case 7: {
                    //  7) Find patient by id
                    if (registry.size()<1){
                        System.out.println("No Patients in registry | Exiting to Menu");
                        break;
                    }else{
                        System.out.println("Search for patient:");
                        System.out.println("Enter patient ID:");
                        String id = in.nextLine();
                        if(!registry.contains(id)){
                            System.out.println("Patient is not in the system!");
                            System.out.println();
                            break;
                        }else {
                            System.out.println("Patient Found!");
                            System.out.println("[ID,Name,Age,Severity] -> ["+ registry.get(id).get()+"]");
                            System.out.println();
                        }

                    }


                    break;
                }case 8: {
                    //  8) Show treatment log
                    if (log.size()<1){
                        System.out.println("Treatment Log is Empty");
                        break;
                    }else{
                        System.out.println("""
                        How would you like the log to be ordered?
                        1. Oldest to Newest
                        2. Newest to Oldest""");
                        int choice;
                        try {
                            choice = Integer.parseInt(in.nextLine());

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Choice Entered: Action Canceled");
                            break;
                        }
                        if (choice == 1){
                            for(TreatedCase tc : log.asListOldestFirst()){
                                System.out.println("__" + tc + "__");
                            }

                        } else if (choice == 2) {
                            for(TreatedCase tc : log.asListNewestFirst()){
                                System.out.println("__" + tc + "__");
                            }
                        }else {
                            System.out.println("Invalid Choice Entered: Action Canceled");
                            break;
                        }


                    }
                    break;
                }case 9: {
                    //  9) Performance demo (use SampleWorkloads)

                    break;
                }case 10: {
                    // 10) Export log to CSV
                    if (log.size()<1){
                        System.out.println("Treatment Log is Empty");
                        System.out.println();
                        break;
                    }else{
                        System.out.println("Enter the file path to save the log (example: log.csv):");
                        String filePath = in.nextLine().trim();

                        if (filePath.isBlank()) {
                            System.out.println("Invalid file path. Export canceled.");
                            break;
                        }
                        try {
                            Path path = Path.of(filePath);


                            CsvIO.exportLog(path, log.asListOldestFirst());

                            System.out.println("Treatment log successfully exported to:");
                            System.out.println(path.toAbsolutePath());

                        } catch (Exception e) {
                            System.out.println("Failed to export CSV: " + e.getMessage());
                        }

                        System.out.println();
                        break;

                    }

                }case 0: {
                    System.out.println("Exiting Program");
                    return;

                }default: {
                    System.out.println("Invalid Option");
                }
            }

        }
    }
}
