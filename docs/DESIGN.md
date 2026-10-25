# ScheduleLab: design notes

## Concepts demonstrated

Java records, comparators, ArrayDeque, scheduling algorithms, OOP, file processing and formula based testing.

## Decisions

The model has one CPU, integer arrival and burst times, and no context switching overhead or I/O. FCFS uses arrival time then input order. SJF chooses the shortest available burst, then arrival time, then input order. Both are nonpreemptive. Round robin uses ArrayDeque; new arrivals join before the just-interrupted process at a quantum boundary.

Turnaround equals finish minus arrival. Waiting equals turnaround minus burst. Response equals first execution minus arrival (Arpaci-Dusseau and Arpaci-Dusseau, 2023). These are distinct measures: a process may respond early but finish late. Timeline block widths are not to scale; labels show exact time intervals. The HTML report shows at most 1000 slices, while CSV includes all slices.

Input is a deliberately limited CSV format: exact header id,arrival,burst, no quoted fields, alphanumeric or underscore IDs excluding IDLE, and integer times. Limits are 100 processes, arrival up to 10000 and burst up to 1000. This is an educational scheduler, not an operating system scheduler or performance benchmark.

## Understand before presenting

Explain how round robin improves response time and can increase turnaround time. Predict the effect of quantum 1 versus quantum 100. Discuss why SJF assumes burst lengths are already known.

Trace the sample from input through the engine to the report. Identify one test for an invalid input and one boundary test. Explain the stated limitations and one extension you would build next.

## References

Arpaci-Dusseau, R.H. and Arpaci-Dusseau, A.C. (2023) Scheduling: Introduction. Operating Systems: Three Easy Pieces, version 1.10. Available at: https://pages.cs.wisc.edu/~remzi/OSTEP/cpu-sched.pdf (Accessed: 21 October 2026).
