import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.quartz.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

public class Scheduler {
    public static void main(String[] args) throws SchedulerException {
        String csvFile = "/Users/fabrizioferla/Downloads/prova.csv";
        SparkConf conf = new SparkConf().setAppName("Spark Test").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        Map<String,String> kafkaParams = new HashMap<>();
        kafkaParams.put("meta")





        JobDetail job = newJob(com.example.LeggiFileCSVJob.class).withIdentity("job1", "group1").build();

        Trigger trigger = newTrigger().withIdentity("trigger1", "group1")
                .withSchedule(cronSchedule("0 0/1 * * * ?")).build();
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();


        org.quartz.Scheduler sched = schedFact.getScheduler();
        sched.start();
        sched.scheduleJob(job, trigger);
    }
}