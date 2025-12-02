package edu.hcu.triage;

import java.nio.file.Path;
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
        // TODO: main loop with menu and input validation
        // Required actions:
        Scanner myObj = new Scanner(System.in);
        int option;
        // TODO: helper methods for each menu action
        while (true){
            System.out.println("Enter Your Option: \n1) Register patient\n2) Update patient\n3) Enqueue for triage" +
                    "\n4) Show next patient\n5) Admit/treat patient\n6) Show triage order\n7) Find patient by id" +
                    "\n8) Show treatment log\n9) Performance demo\n10) Export log to CSV\n0) Exit");
            option= myObj.nextInt();
            switch (option){
                case 1:
                    //  1) Register patient
                    break;
                case 2:
                    //  2) Update patient
                    break;
                case 3:
                    //  3) Enqueue for triage (by id)
                    break;
                case 4:
                    //  4) Peek next
                    break;
                case 5:
                    //  5) Admit/treat next (capture outcome + notes; append to log)
                    break;
                case 6:
                    //  6) Show triage order (non-destructive)
                    break;
                case 7:
                    //  7) Find patient by id
                    break;
                case 8:
                    //  8) Show treatment log
                    break;
                case 9:
                    //  9) Performance demo (use SampleWorkloads)
                    break;
                case 10:
                    // 10) Export log to CSV
                    break;
                case 0:
                    System.out.println("Exiting Program");
                    return;

        }

        }
    }
}
