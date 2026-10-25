import java.util.*;
public final class SchedulerTests {
    static int passed=0;
    static void check(String name,Runnable test){try{test.run();passed++;System.out.println("PASS "+name);}catch(Exception|AssertionError e){throw new AssertionError(name+": "+e.getMessage(),e);}}
    static void equal(Object a,Object b){if(!Objects.equals(a,b))throw new AssertionError("Expected "+a+", got "+b);}
    static void rejects(Runnable r){try{r.run();}catch(IllegalArgumentException e){return;}throw new AssertionError("Expected rejection");}
    public static void main(String[] args){
        var jobs=List.of(new Scheduler.Process("A",0,5),new Scheduler.Process("B",1,2),new Scheduler.Process("C",2,1));
        check("FCFS waiting",()->equal(4,Scheduler.run(jobs,"FCFS",2).metrics().get(1).waiting()));
        check("SJF selects shortest available",()->equal("C",Scheduler.run(jobs,"SJF",2).slices().get(1).id()));
        check("RR boundary arrivals precede old job",()->equal("B",Scheduler.run(jobs,"RR",2).slices().get(1).id()));
        check("RR finish time",()->equal(8,Scheduler.run(jobs,"RR",2).metrics().get(0).finish()));
        check("idle CPU represented",()->equal("IDLE",Scheduler.run(List.of(new Scheduler.Process("A",3,2)),"FCFS",2).slices().get(0).id()));
        check("RR large quantum becomes FCFS",()->equal(Scheduler.run(jobs,"FCFS",20).metrics(),Scheduler.run(jobs,"RR",20).metrics()));
        check("zero burst rejected",()->rejects(()->Scheduler.run(List.of(new Scheduler.Process("A",0,0)),"FCFS",2)));
        check("zero quantum rejected",()->rejects(()->Scheduler.run(jobs,"RR",0)));
        check("duplicate ID rejected",()->rejects(()->Scheduler.run(List.of(jobs.get(0),jobs.get(0)),"FCFS",2)));
        check("malformed CSV rejected",()->rejects(()->Scheduler.parse("id,arrival,burst\nA,1")));
        check("CSV parsed",()->equal(1,Scheduler.parse("id,arrival,burst\nA,0,2\n").size()));
        check("HTML escaping",()->equal("&lt;img&gt;",ScheduleLab.escape("<img>")));
        check("work conservation",()->{for(String algorithm:List.of("FCFS","SJF","RR")){int work=Scheduler.run(jobs,algorithm,2).slices().stream().filter(s->!s.id().equals("IDLE")).mapToInt(s->s.end()-s.start()).sum();equal(8,work);}});
        check("turnaround formula",()->{for(var m:Scheduler.run(jobs,"RR",2).metrics()){var p=jobs.stream().filter(j->j.id().equals(m.id())).findFirst().orElseThrow();equal(m.finish()-p.arrival(),m.turnaround());equal(m.turnaround()-p.burst(),m.waiting());}});
        check("idle marker reserved",()->rejects(()->Scheduler.run(List.of(new Scheduler.Process("IDLE",0,1)),"FCFS",2)));
        System.out.println(passed+" checks passed.");
    }
}
