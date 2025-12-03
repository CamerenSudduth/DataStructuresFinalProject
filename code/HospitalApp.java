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
        if (args.length > 0){ //Checks if any other arguments are included in the running of the code
            try{
                Path csvPath = Path.of("Patient.csv"); //Defaults to run the patient.csv file
                // calls csvIO warning method to get possible format warnings
                List<String> warnings = CsvIO.loadPatients(csvPath, registry);


                if (warnings.isEmpty()) {
                    System.out.println("Patients loaded successfully.\n");
                } else {
                    System.out.println("Patients loaded with warnings:");
                    for (String w : warnings) { //Prints out all warnings line by line
                        System.out.println(" - " + w);
                    }
                    System.out.println();
                }

            }catch (Exception e){ //If loading patients didn't work at all (Eg. csv file doesnt exist)
                System.out.println("Loading Patients Failed" + e.getMessage()); // gets error message of why it didnt load
                System.out.println();

            }

        }

        // TODO: main loop with menu and input validation
        // Required actions:
        //Declared Variables
        // TODO: helper methods for each menu action
        while (true){ // list options for user to select
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
            //gets user choice of input
            String input = in.nextLine();
            int option;

            //if choice is empty just continues through loop
            if(input.isEmpty()){
                continue;
            }
            //tries option received if invalid then system just continues through as states invalid input
            //used to avoid menu from crashing if the input is invalid
            try{
                option = Integer.parseInt(input);

            }catch (NumberFormatException e){
                System.out.println("Invalid Option");
                continue;
            }


            switch (option){
                case 1: {
                    //  1) Register patient
                    // ask for id and name from user
                    System.out.println("Enter the patients ID: ");
                    String id = in.nextLine();
                    System.out.println("Enter the patients full name: ");
                    String name = in.nextLine();
                    System.out.println("Enter the patients age (0-120): ");
                    //Try Catch blocks allow the system not to crash from invalid user input
                    // Converts string to int instead of using int right away to avoid scanner from crashing
                    int age;
                    try {
                        age = Integer.parseInt(in.nextLine());
                        //still checks for input  to make sure they're within parameter
                        if (age < 0 || age > 120) {
                            System.out.println("Invalid Age Entered: must be between (0-120), Registration Canceled");
                            break;//Exits back to main menu
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Age Entered: Registration Canceled");
                        break;//Exits back to main menu
                    }

                    System.out.println("Enter the severity level of patient: ");
                    System.out.println("1: Immediate: life-threatening condition");
                    System.out.println("2: Emergency: high-risk, vitals in danger zone");
                    System.out.println("3: Urgent: stable condition, multiple resources needed to stabilize patient");
                    System.out.println("4: Semi-Urgent: stable condition, one resource needed");
                    System.out.println("5: Non-Urgent: minor condition, can be treated whenever");

                    //Try Catch blocks allow the system not to crash from invalid user input
                    // Converts string to int instead of using int right away to avoid scanner from crashing
                    int severity;
                    try {
                        severity = Integer.parseInt(in.nextLine());
                        //still checks for input  to make sure they're within parameter
                        if (severity < 1 || severity > 5) {
                            System.out.println("Invalid Severity, Registration Canceled");
                            break;//Exits back to main menu
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Severity, Registration Canceled");
                        break; //Exits back to main menu
                    }

                    registry.registerNew(id, name, age, severity);
                    System.out.println("Patient Registered!");
                    System.out.println();
                    break;
                }case 2: {
                    //  2) Update patient
                    // asks user for ID of patient they want to update
                    System.out.println("Enter the ID of the patient you want to update:");
                    String id = in.nextLine();
                    //Checks if id has not been registered already
                    if(!registry.contains(id)){
                        System.out.println("Patient is not in the system! Update Canceled");
                        System.out.println();
                        break;//Exits back to main menu
                    }

                    System.out.println("Would you like to update patients name? (Y/N)");
                    String choice = in.nextLine();
                    String name;
                    //Checks users input for 'Y' and is not case-sensitive all other input is considered no
                    if (choice.equalsIgnoreCase("Y")){
                        System.out.println("Enter patients new name: ");
                        name = in.nextLine();
                    }else{ //sets input to null cause 'Y' wasnt inputted
                        name = null;
                    }


                    //Try Catch blocks allow the system not to crash from invalid user input
                    int age;
                    System.out.println("Would you like to update patients age? (Y/N)");
                    choice = in.nextLine();
                    //Checks users input for 'Y' and is not case-sensitive all other input is considered no
                    if (choice.equalsIgnoreCase("Y")){
                        System.out.println("Enter patients new age: ");
                        try {// Converts string to int instead of using int right away to avoid scanner from crashing
                            age = Integer.parseInt(in.nextLine());
                            if (age < 0 || age > 120) {
                                System.out.println("Invalid Age Entered: must be between (0-120), Update Canceled");
                                break;
                            }
                            //Lets user know that an invalid input was entered returns to main menu
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Age Entered: Update Canceled");
                            break;
                        }
                    }else{
                        age = -1; //sets input to -1 cause 'Y' wasn't inputted
                    }

                    //Try Catch blocks allow the system not to crash from invalid user input
                    int severity;
                    //Checks users input for 'Y' and is not case-sensitive all other input is considered no
                    System.out.println("Would you like to update patients severity? (Y/N)");
                    choice = in.nextLine();
                    if (choice.equalsIgnoreCase("Y")){ //If yes list all severity options
                        System.out.println("Enter patients new severity level: ");
                        System.out.println("1: Immediate: life-threatening condition");
                        System.out.println("2: Emergency: high-risk, vitals in danger zone");
                        System.out.println("3: Urgent: stable condition, multiple resources needed to stabilize patient");
                        System.out.println("4: Semi-Urgent: stable condition, one resource needed");
                        System.out.println("5: Non-Urgent: minor condition, can be treated whenever");
                        try {// Converts string to int instead of using int right away to avoid scanner from crashing
                            severity = Integer.parseInt(in.nextLine());
                            if (severity < 1 || severity > 5) {
                                System.out.println("Invalid Severity, Update Canceled");
                                break;
                            }
                            //Lets user know that an invalid input was entered returns to main menu
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Severity Entered: Update Canceled");
                            break;
                        }
                    }else{
                        severity = -1; //sets input to -1 cause 'Y' wasn't inputted
                    }
                    registry.updateExisting(id, name, age, severity); //updates the specified patient with new info
                    System.out.println("Patient Updated!");
                    System.out.println();
                    break;
                }case 3: {
                    //  3) Enqueue for triage (by id)
                    System.out.println("Enter the ID of the patient you want to enqueue:");
                    String id = in.nextLine();
                    if(!registry.contains(id)){ //checks if id is in registry
                        System.out.println("Patient is not in the system! Enqueue Canceled");
                        System.out.println();
                        break;
                    }

                    Patient patient = registry.get(id).get(); //creates a temporary variable for patient being enqueued
                    if(triage.snapshotOrder().contains(patient)){  //checks if patient is already in queue
                        System.out.println("Patient is already in queue!");
                    }else{
                        triage.enqueue(patient); //enqueus patient if not in queue
                        System.out.println("Patient ID:"+id+" has been enqueued!");
                    }
                    break;
                }case 4: {
                    //  4) Peek next
                    if (triage.size() < 1) { //checks if triage queue is empty
                        System.out.println("No more patients in line!");
                        System.out.println();
                    } else {
                        Patient nextPatient = triage.peekNext().get(); //Looks at the next prioritized patient
                        System.out.println("Next patient in line: [ID,Name,Age,Severity]" +// returns patient info
                                " -> [" + nextPatient + "]");
                        System.out.println();
                    }
                    break;
                }case 5: {
                    //  5) Admit/treat next (capture outcome + notes; append to log)

                    if (triage.size() < 1){ //Checks if triage queue is empty
                        System.out.println("No patients to be admitted, line is empty! | Exiting to Menu");
                        System.out.println();
                    }else{
                        Patient currentPatient = triage.dequeueNext().get(); //creates a temp variable of patient dequeued
                        Instant start = Instant.now(); //Gets treatment start time
                        System.out.println("Patient being treated: [ID,Name,Age,Severity] -> ["+ currentPatient+"]");
                        TreatedCase.Outcome outcome = null; // Initializes enum variable outcome to null
                        // Ask user to select option for the outcome of treatment keeps looping for valid input
                        // so that dequeued patient isn't lost.
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
                        while(notes.isBlank()){ //Checks if notes is just blank string or only contains spaces
                            System.out.println("Notes must be given, please enter notes");
                            notes = in.nextLine();
                        }
                        Instant end = Instant.now(); //gets time at end of treatment
                        //Creates a new treated case with the given inputs
                        TreatedCase treated = new TreatedCase(currentPatient, start, end, outcome, notes);
                        log.append(treated); //appends new treated case to the treatment log
                    }
                    break;
                }case 6:{
                    //  6) Show triage order (non-destructive)
                    if (triage.size() < 1){ //checks if queue is empty
                        System.out.println("No patients to be admitted, line is empty! | Exiting to Menu");
                        System.out.println();
                    }else{
                        //stores triage list order in a temporary variable
                        List<Patient> triageOrder = triage.snapshotOrder();
                        System.out.println("Triage Order:");
                        int position = 1; //variable to show what position the patients are in
                        for (Patient p : triageOrder) { //cycles through patient queue
                            System.out.println(position + ". [ID,Name,Age,Severity] -> [" + p + "]");
                            position++; //increments position counter for patient order
                        }
                    }
                    break;
                }case 7: {
                    //  7) Find patient by id
                    if (registry.size()<1){ //checks if registry is empty
                        System.out.println("No Patients in registry | Exiting to Menu");
                        break;
                    }else{
                        System.out.println("Search for patient:");
                        System.out.println("Enter patient ID:");
                        String id = in.nextLine();
                        if(!registry.contains(id)){ // checks if patient is in registry
                            System.out.println("Patient is not in the system!");
                            System.out.println();
                            break;
                        }else {
                            // Returns patient if Id is found in registry gets patient info and returns info as a message
                            System.out.println("Patient Found!");
                            System.out.println("[ID,Name,Age,Severity] -> ["+ registry.get(id).get()+"]");
                            System.out.println();
                        }

                    }
                    break;
                }case 8: {
                    //  8) Show treatment log
                    if (log.size()<1){ // checks if treatment log is empty
                        System.out.println("Treatment Log is Empty");
                        break;
                    }else{ //Asks how user wants the log to be ordered
                        System.out.println("""
                        How would you like the log to be ordered?
                        1. Oldest to Newest
                        2. Newest to Oldest""");
                        int choice;
                        try { // Converts string to int instead of using int right away to avoid scanner from crashing
                            choice = Integer.parseInt(in.nextLine());

                        } catch (NumberFormatException e) { //returns to menu if invalid input was recieved
                            System.out.println("Invalid Choice Entered: Action Canceled");
                            break;
                        }
                        //Sorts treatment log based on user choice
                        if (choice == 1){ // sorts from oldest to newest
                            for(TreatedCase tc : log.asListOldestFirst()){
                                System.out.println("__" + tc + "__");
                            }

                        } else if (choice == 2) { // sorts from newest to oldes
                            for(TreatedCase tc : log.asListNewestFirst()){
                                System.out.println("__" + tc + "__");
                            }
                        }else { //invalid input recieved goes back to main menu
                            System.out.println("Invalid Choice Entered: Action Canceled");
                            break;
                        }


                    }
                    break;
                }case 9: {
                    // 9) Performance demo
                    runPerformanceDemo();
                    break;
                }case 10: {
                    // 10) Export log to CSV
                    if (log.size()<1){ //Checks if there is information in the treatment log
                        System.out.println("Treatment Log is Empty");
                        System.out.println();
                        break;
                    }else{ //Ask user for file name they want to save it as
                        System.out.println("Enter the file path to save the log (example: log.csv):");
                        String filePath = in.nextLine().trim(); //sanitizes user input to be able to use as file path
                        //checks if input is empty or just contain spaces, returns to main menu if it does
                        if (filePath.isBlank()) {
                            System.out.println("Invalid file path. Export canceled.");
                            break;
                        }
                        try {//turns user path into a file path usable with our csvIO exportLog method
                            Path path = Path.of(filePath);

                            //calls exportLog method passing through the file path and the log order
                            CsvIO.exportLog(path, log.asListOldestFirst());

                            System.out.println("Treatment log successfully exported to:");
                            System.out.println(path.toAbsolutePath()); //tells the user the file path of the log

                        } catch (Exception e) { // Tells user that log failed to export to a csv file
                            System.out.println("Failed to export CSV: " + e.getMessage());
                        }

                        System.out.println();
                        break;

                    }

                }case 0: { //Exits system
                    System.out.println("Exiting Program");
                    return;

                }default: {
                    System.out.println("Invalid Option");
                }
            }
        }
    }
    /** Prompt user for an integer between min and max (inclusive). */
    private int getIntInput(String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            String line = in.nextLine();
            try {
                value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Error: value must be between " + min + " and " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: enter a valid integer.");
            }
        }
    }

    /** Prompt user for a double between min and max (inclusive). */
    private double getDoubleInput(String prompt, double min, double max) {
        double value;
        while (true) {
            System.out.print(prompt);
            String line = in.nextLine();
            try {
                value = Double.parseDouble(line);
                if (value < min || value > max) {
                    System.out.println("Error: value must be between " + min + " and " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: enter a valid number.");
            }
        }
    }

    /** Prompt user for a long value. */
    private long getLongInput(String prompt) {
        long value;
        while (true) {
            System.out.print(prompt);
            String line = in.nextLine();
            try {
                value = Long.parseLong(line);
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Error: enter a valid long.");
            }
        }
    }

    /** Prompt user for a string from a list of allowed values (case-insensitive). */
    private String getStringInput(String prompt, java.util.List<String> allowedValues) {
        while (true) {
            System.out.print(prompt);
            String line = in.nextLine().trim().toLowerCase();
            for (String allowed : allowedValues) {
                if (line.equalsIgnoreCase(allowed)) return allowed;
            }
            System.out.println("Error: must be one of " + allowedValues);
        }
    }
    private void runPerformanceDemo() {
        try {
            System.out.println("Performance Demo:");

            // --- Get inputs ---
            int workloadSize = getIntInput("Enter total number of operations (workload size): ", 1, Integer.MAX_VALUE);
            double enqueueRatio = getDoubleInput("Enter enqueue ratio (0.0-1.0): ", 0.0, 1.0);
            String distribution = getStringInput("Enter severity distribution (uniform/skewed): ", Arrays.asList("uniform", "skewed"));
            long seed = getLongInput("Enter random seed (any long value, e.g. 42): ");

            int enqueueOps = (int)(workloadSize * enqueueRatio);
            int dequeueTarget = workloadSize - enqueueOps;

            System.out.println("\n=== Performance Report ===");

            SampleWorkloads.WorkloadResult result;

            // --- Run workload with timer ---
            try (PerfTimer timer = new PerfTimer("Time for enqueue + dequeue: ")) {
                result = SampleWorkloads.runWorkload(
                        registry,
                        triage,
                        workloadSize,
                        enqueueRatio,
                        distribution,
                        seed
                );
            }

            int finalQueueSize = triage.size();

            // --- Report ---
            System.out.println("\nPatients enqueued: " + enqueueOps);

            System.out.println("\nPatient severity counts (after enqueue, before dequeue):");
            for (int i = 0; i < result.beforeDequeues.length; i++) {
                System.out.println("  Severity " + (i + 1) + ": " + result.beforeDequeues[i]);
            }

            // Print the patients dequeued
            System.out.println("\nPatients dequeued: " + result.dequeuesPerformed);
            
            // Check if dequeues performed matches the expected ratio
            if (result.dequeuesPerformed < dequeueTarget) {
                System.out.println("Dequeue attempts (requested): " + dequeueTarget);
                System.out.println("Dequeue attempts (performed): " + result.dequeuesPerformed);
                System.out.println("Queue emptied early — not enough patients to match ratio.");
            }

            System.out.println("\nPatient severity counts remaining in queue (after dequeues):");
            for (int i = 0; i < result.afterDequeues.length; i++) {
                System.out.println("  Severity " + (i + 1) + ": " + result.afterDequeues[i]);
            }

            System.out.println("\nPatients still in queue: " + finalQueueSize);

            // Clear queue
            System.out.println("\nClearing queue...");
            triage.clear();
            System.out.println("Queue cleared.");
            System.out.println("==========================\n");

        } catch (Exception e) {
            System.out.println("Error during performance demo: " + e.getMessage());
            in.nextLine();
        }
    }
}
