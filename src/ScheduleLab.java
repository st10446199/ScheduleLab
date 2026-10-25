import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.IOException;
public final class ScheduleLab {
    public static String escape(String value){return value.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");}
    public static String report(List<Scheduler.Result> results,int quantum) {
        StringBuilder s=new StringBuilder("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><title>ScheduleLab</title><style>body{font:16px/1.6 system-ui;background:#11161d;color:#edf2f8;padding:24px}main{max-width:1100px;margin:auto}h1{font-size:42px}section{background:#1a222d;border:1px solid #34404e;padding:22px;border-radius:14px;margin:24px 0}.scroll{overflow:auto}table{border-collapse:collapse;width:100%}td,th{padding:10px;border-bottom:1px solid #34404e;text-align:left;white-space:nowrap}th,small{color:#c8b6ff}.timeline{display:flex;overflow:auto;gap:4px}.slice{padding:8px;background:#334559;border-left:3px solid #c8b6ff;min-width:65px;flex-shrink:0}p{color:#b8c4d1}</style></head><body><main><small>ALGORITHM COMPARISON / JAVA</small><h1>ScheduleLab</h1><p>One CPU. Integer time units. No context switching cost. Round robin quantum: "+quantum+". Timeline blocks are labelled with exact times; widths are not to scale.</p>");
        for(var r:results){s.append("<section><h2>").append(r.algorithm()).append("</h2><p>").append(String.format(Locale.ROOT,"Average waiting %.2f / turnaround %.2f / response %.2f",r.averageWaiting(),r.averageTurnaround(),r.averageResponse())).append("</p><div class=\"timeline\">");
            int shown=0;for(var slice:r.slices()){if(shown++==1000)break;s.append("<div class=\"slice\">").append(escape(slice.id())).append("<br>").append(slice.start()).append(" to ").append(slice.end()).append("</div>");}
            s.append("</div><p>Showing up to 1000 execution slices. CSV contains the full timeline.</p><div class=\"scroll\"><table><thead><tr><th>ID</th><th>Finish</th><th>Turnaround</th><th>Waiting</th><th>Response</th></tr></thead><tbody>");
            for(var m:r.metrics())s.append("<tr><td>").append(escape(m.id())).append("</td><td>").append(m.finish()).append("</td><td>").append(m.turnaround()).append("</td><td>").append(m.waiting()).append("</td><td>").append(m.response()).append("</td></tr>");
            s.append("</tbody></table></div></section>");
        }return s.append("</main></body></html>").toString();
    }
    public static void main(String[] args) {
        try {
            if(args.length>3)throw new IllegalArgumentException("Usage: ScheduleLab [input.csv] [output directory] [quantum]");
            Path input=Path.of(args.length>0?args[0]:"samples/processes.csv").toAbsolutePath().normalize();
            Path output=Path.of(args.length>1?args[1]:"reports").toAbsolutePath().normalize();
            int quantum=args.length>2?Integer.parseInt(args[2]):2;
            if(Files.size(input)>1048576)throw new IllegalArgumentException("Input limit is 1 MiB.");
            for(String file:List.of("report.html","timeline.csv"))if(input.toString().equalsIgnoreCase(output.resolve(file).toString()))throw new IllegalArgumentException("Output would overwrite input.");
            var processes=Scheduler.parse(Files.readString(input,StandardCharsets.UTF_8));
            List<Scheduler.Result> results=new ArrayList<>();for(String algorithm:List.of("FCFS","SJF","RR"))results.add(Scheduler.run(processes,algorithm,quantum));
            Files.createDirectories(output);Files.writeString(output.resolve("report.html"),report(results,quantum),StandardCharsets.UTF_8);
            StringBuilder csv=new StringBuilder("algorithm,id,start,end\n");for(var r:results)for(var slice:r.slices())csv.append(r.algorithm()).append(',').append(slice.id()).append(',').append(slice.start()).append(',').append(slice.end()).append('\n');
            Files.writeString(output.resolve("timeline.csv"),csv.toString(),StandardCharsets.UTF_8);
            System.out.println("Compared FCFS, SJF and RR for "+processes.size()+" processes. Report: "+output.resolve("report.html"));
        } catch(IllegalArgumentException | IOException e){System.err.println(e.getMessage());System.exit(2);}
    }
}
