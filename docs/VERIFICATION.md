# Verification

Checked on 21 October 2026 in Linux.

All 15 regression checks passed with OpenJDK 17.0.20. Compilation used the JDK compiler entry point via java com.sun.tools.javac.Main because the javac launcher was not on PATH. The sample report and timeline CSV were generated from the compiled Java program.

Browser startup was blocked by the execution environment. Visual layout, keyboard interactions and downloads need manual checks locally.

## Local acceptance

1. Extract the entire ZIP.
2. Follow README.md to run the sample.
3. Confirm: The sample has 4 processes. With quantum 2, average waiting times are FCFS 4.75, SJF 4.00 and RR 5.00. All three finish at time 16.
4. Run the automated checks using `java -cp out SchedulerTests`.
5. Inspect the report or page at a narrow browser width.
6. Enter malformed input and verify a readable error.
7. Restore the sample before taking screenshots.
