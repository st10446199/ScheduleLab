# ScheduleLab

A Java CPU scheduling simulator comparing FCFS, SJF and round robin with timelines, waiting times and HTML reports.

**Stack:** Java 17+

## Why this project

A focused portfolio project demonstrating Java records, comparators, ArrayDeque, scheduling algorithms, OOP, file processing and formula based testing.

## Features

* First come first served (FCFS).
* Nonpreemptive shortest job first (SJF).
* Round robin (RR) with configurable quantum.
* Idle periods, execution timelines and per-process metrics.
* Comparison report and a complete timeline CSV.

## Quick start

Requires JDK 17 or newer, with both java and javac available. No Maven, Gradle, Python or third party packages are needed. Open a terminal in this folder:

```text
javac -d out src/Scheduler.java src/ScheduleLab.java tests/SchedulerTests.java
java -cp out ScheduleLab samples/processes.csv reports 2
```

Open `reports/report.html`. `reports/timeline.csv` contains every execution slice. A generated demo is included at `samples/output/report.html`. On Windows, `run-demo.cmd` compiles and runs the demo.

The sample has 4 processes. With quantum 2, average waiting times are FCFS 4.75, SJF 4.00 and RR 5.00. All three finish at time 16.

## Automated tests

```text
java -cp out SchedulerTests
```

Expected: 15 passing checks. Compile with the command above first. Assertions use explicit checks and do not need the Java -ea option.

## Design and scope

The model has one CPU, integer arrival and burst times, and no context switching overhead or I/O. FCFS uses arrival time then input order. SJF chooses the shortest available burst, then arrival time, then input order. Both are nonpreemptive. Round robin uses ArrayDeque; new arrivals join before the just-interrupted process at a quantum boundary.

Turnaround equals finish minus arrival. Waiting equals turnaround minus burst. Response equals first execution minus arrival (Arpaci-Dusseau and Arpaci-Dusseau, 2023). These are distinct measures: a process may respond early but finish late. Timeline block widths are not to scale; labels show exact time intervals. The HTML report shows at most 1000 slices, while CSV includes all slices.

Input is a deliberately limited CSV format: exact header id,arrival,burst, no quoted fields, alphanumeric or underscore IDs excluding IDLE, and integer times. Limits are 100 processes, arrival up to 10000 and burst up to 1000. This is an educational scheduler, not an operating system scheduler or performance benchmark.

## Source guide

`src/Scheduler.java` contains the algorithm engine. `src/ScheduleLab.java` contains CLI input and reports. `tests/SchedulerTests.java` contains regression checks.

See `docs/DESIGN.md` for discussion questions and `docs/VERIFICATION.md` for the exact checks performed.

## Data and outputs

All example data is synthetic. No account, paid service or network access is required by the app. Outputs can contain user supplied values, so inspect them before sharing. Generated files in the selected output directory are overwritten on each run. Keep the provided sample outputs in Git; generated personal reports are ignored.

## References

Arpaci-Dusseau, R.H. and Arpaci-Dusseau, A.C. (2023) Scheduling: Introduction. Operating Systems: Three Easy Pieces, version 1.10. Available at: https://pages.cs.wisc.edu/~remzi/OSTEP/cpu-sched.pdf (Accessed: 21 October 2026).

## Licence

MIT. See LICENSE.
