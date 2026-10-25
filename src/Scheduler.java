import java.util.*;
public final class Scheduler {
    public record Process(String id,int arrival,int burst) {}
    public record Slice(String id,int start,int end) {}
    public record Metric(String id,int finish,int turnaround,int waiting,int response) {}
    public record Result(String algorithm,List<Slice> slices,List<Metric> metrics,double averageWaiting,double averageTurnaround,double averageResponse) {}
    private static final class State {
        final Process process; final int order; int remaining; int first=-1; int finish;
        State(Process p,int i){process=p;order=i;remaining=p.burst();}
    }
    public static List<Process> parse(String text) {
        String[] lines=text.replace("\uFEFF", "").strip().split("\\R");
        if(lines.length<2 || !lines[0].strip().equals("id,arrival,burst"))throw new IllegalArgumentException("Expected id,arrival,burst header and at least one process.");
        List<Process> processes=new ArrayList<>();
        for(int i=1;i<lines.length;i++){
            String[] cells=lines[i].split(",",-1);
            if(cells.length!=3)throw new IllegalArgumentException("Record "+(i+1)+" needs three fields. Quoted CSV is not supported.");
            try {processes.add(new Process(cells[0].strip(),Integer.parseInt(cells[1].strip()),Integer.parseInt(cells[2].strip())));}
            catch(NumberFormatException e){throw new IllegalArgumentException("Record "+(i+1)+" needs whole numbers.");}
        }
        validate(processes);return processes;
    }
    private static void validate(List<Process> processes) {
        if(processes==null || processes.isEmpty() || processes.size()>100)throw new IllegalArgumentException("Use 1 to 100 processes.");
        Set<String> ids=new HashSet<>();
        for(Process p:processes)if(p==null || p.id()==null || p.id().equals("IDLE") || !p.id().matches("[A-Za-z0-9_]{1,32}") || !ids.add(p.id()) || p.arrival()<0 || p.arrival()>10000 || p.burst()<1 || p.burst()>1000)
            throw new IllegalArgumentException("Unique alphanumeric IDs, arrival 0 to 10000 and burst 1 to 1000 are required.");
    }
    public static Result run(List<Process> processes,String algorithm,int quantum) {
        validate(processes);
        if(!List.of("FCFS","SJF","RR").contains(algorithm) || quantum<1 || quantum>1000)throw new IllegalArgumentException("Use FCFS, SJF or RR and quantum 1 to 1000.");
        List<State> states=new ArrayList<>();for(int i=0;i<processes.size();i++)states.add(new State(processes.get(i),i));
        List<State> arrivals=new ArrayList<>(states);arrivals.sort(Comparator.comparingInt((State s)->s.process.arrival()).thenComparingInt(s->s.order));
        Deque<State> queue=new ArrayDeque<>();List<Slice> slices=new ArrayList<>();int time=0,done=0,index=0;
        while(done<states.size()) {
            State current;
            if(algorithm.equals("RR")) {
                while(index<arrivals.size() && arrivals.get(index).process.arrival()<=time)queue.addLast(arrivals.get(index++));
                if(queue.isEmpty()) {int next=arrivals.get(index).process.arrival();slices.add(new Slice("IDLE",time,next));time=next;continue;}
                current=queue.removeFirst();
            } else {
                final int now=time;
                Comparator<State> comparator=Comparator.comparingInt((State s)->s.process.arrival()).thenComparingInt(s->s.order);
                if(algorithm.equals("SJF"))comparator=Comparator.comparingInt((State s)->s.process.burst()).thenComparing(comparator);
                current=states.stream().filter(s->s.remaining>0 && s.process.arrival()<=now).min(comparator).orElse(null);
                if(current==null){int next=states.stream().filter(s->s.remaining>0).mapToInt(s->s.process.arrival()).min().orElseThrow();slices.add(new Slice("IDLE",time,next));time=next;continue;}
            }
            if(current.first<0)current.first=time;
            int duration=algorithm.equals("RR")?Math.min(quantum,current.remaining):current.remaining;
            slices.add(new Slice(current.process.id(),time,time+duration));time+=duration;current.remaining-=duration;
            if(algorithm.equals("RR")) {
                // Newly arrived processes enter before the interrupted process at a quantum boundary.
                while(index<arrivals.size() && arrivals.get(index).process.arrival()<=time)queue.addLast(arrivals.get(index++));
                if(current.remaining>0)queue.addLast(current);
            }
            if(current.remaining==0){current.finish=time;done++;}
        }
        List<Metric> metrics=new ArrayList<>();
        for(State s:states){int turnaround=s.finish-s.process.arrival();metrics.add(new Metric(s.process.id(),s.finish,turnaround,turnaround-s.process.burst(),s.first-s.process.arrival()));}
        return new Result(algorithm,List.copyOf(slices),List.copyOf(metrics),metrics.stream().mapToInt(Metric::waiting).average().orElse(0),metrics.stream().mapToInt(Metric::turnaround).average().orElse(0),metrics.stream().mapToInt(Metric::response).average().orElse(0));
    }
}
