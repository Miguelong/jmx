package jmx.client;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
/**
 * Created by miguel on 09/04/2018.
 */
public class JMXClient {
    public static void main(String[] args) {
        try {
            //获取MBeanServerConnection,根据它可以取得远程JVM的MBeans
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://127.0.0.1:8089/jmxrmi");
            JMXConnector connector = JMXConnectorFactory.connect(url);
            MBeanServerConnection mbsc = connector.getMBeanServerConnection();

            //获取远程JVM的所有MBeans的ObjectName
            /*Set<ObjectName> names = mbsc.queryNames(null,null);
            for(ObjectName name:names){
               System.out.println(name.getCanonicalName());
            }*/


            //获取远程Java进程开启时间
            ObjectName runtime = new ObjectName("java.lang:type=Runtime");
            long JVM_StartTime = (long)mbsc.getAttribute(runtime,"StartTime");
            System.out.println("Remote JVM StartTime:"+new Date(JVM_StartTime));

            //获取PS Scavenge和PS MarkSweep
            ObjectName scavengeName = new ObjectName("java.lang:name=PS Scavenge,type=GarbageCollector");
            ObjectName markSweepName = new ObjectName("java.lang:name=PS MarkSweep,type=GarbageCollector");
//            ObjectInstance scavenge = mbsc.getObjectInstance(scavengeName);
//            System.out.println(scavenge.getClassName());

            //获取PS Scavenge的属性值
            System.out.println("PS Scavenge->CollectionCount:"+mbsc.getAttribute(scavengeName,"CollectionCount"));
            System.out.println("PS Scavenge->CollectionTime:"+mbsc.getAttribute(scavengeName,"CollectionTime"));
            CompositeDataSupport lastGcInfo = (CompositeDataSupport)mbsc.getAttribute(scavengeName,"LastGcInfo");
            System.out.println("PS Scavenge->lastGcInfo->startTime:"+lastGcInfo.get("startTime"));
            System.out.println("PS Scavenge->lastGcInfo->endTime:"+lastGcInfo.get("endTime"));
            System.out.println("PS Scavenge->lastGcInfo->duration:"+lastGcInfo.get("duration"));

            //获取PS Scavenge的属性集合
            /*MBeanInfo info = mbsc.getMBeanInfo(scavengeName);
            MBeanAttributeInfo[] attributeInfos=info.getAttributes();
            for(MBeanAttributeInfo attributeInfo:attributeInfos){
                System.out.println(attributeInfo.getName());
            }*/


            //监听GARBAGE_COLLECTION_NOTIFICATION
            NotificationListener listener = (notification,handback)->{
                if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                    GarbageCollectionNotificationInfo notificationInfo = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());

                    GcInfo gcInfo = notificationInfo.getGcInfo();
                    Map<String,MemoryUsage> memoryUsageBeforeGc = gcInfo.getMemoryUsageBeforeGc();
                    Map<String,MemoryUsage> memoryUsageAfterGc = gcInfo.getMemoryUsageAfterGc();

                    System.out.println("------------------------------MemoryUsageBeforeGc------------------------------");
                    Set<String> keys = memoryUsageBeforeGc.keySet();
                    for(String key:keys){
                        MemoryUsage memoryUsage =memoryUsageBeforeGc.get(key);
                        System.out.println(key+":"+memoryUsage.toString());
                        /*System.out.println(key+"->committed:"+memoryUsage.getCommitted());
                        System.out.println(key+"->init:"+memoryUsage.getInit());
                        System.out.println(key+"->max:"+memoryUsage.getMax());
                        System.out.println(key+"->used:"+memoryUsage.getUsed());*/
                    }

                    System.out.println("------------------------------MemoryUsageAfterGc-------------------------------");
                    keys = memoryUsageAfterGc.keySet();
                    for(String key:keys){
                        MemoryUsage memoryUsage =memoryUsageAfterGc.get(key);
                        System.out.println(key+":"+memoryUsage.toString());
                    }

                    //getStartTime():Returns the start time of this GC in milliseconds since the Java virtual machine was started.
                    long gc_StartTime=JVM_StartTime+notificationInfo.getGcInfo().getStartTime();
                    System.out.println("------------------------------GcInformation------------------------------------");
                    System.out.println(notificationInfo.getGcAction() + ": - "
                            + notificationInfo.getGcInfo().getId() + ", "
                            + notificationInfo.getGcName()
                            + "start from "+new Date(gc_StartTime)
                            + " (cause of " + notificationInfo.getGcCause() + ") "
                            + gcInfo.getDuration() + " milliseconds");
                    System.out.println();
                    System.out.println();

                }
            };
            mbsc.addNotificationListener(scavengeName,listener,null,null);
            mbsc.addNotificationListener(markSweepName,listener,null,null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        while(true){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
