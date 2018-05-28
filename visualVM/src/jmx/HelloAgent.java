package jmx;

/**
 * Created by miguel on 09/04/2018.
 */
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import com.sun.jdmk.comm.HtmlAdaptorServer;

/**
 *
 * @author Administrator 该类是一个Agent类，说明： 先创建了一个MBeanServer，用来做MBean的容器
 *         将Hello这个类注入到MBeanServer中，注入需要创建一个ObjectName类
 *         创建一个AdaptorServer，这个类将决定MBean的管理界面，这里用最普通的Html型界面。
 *         AdaptorServer其实也是一个MBean。
 *         zhenghongqiang:name=HelloWorld的名字是有一定规则的，格式为：“域名:name=MBean名称”，
 *         域名和MBean名称都可以任意取。
 *
 *
 */
public class HelloAgent {
    public static void main(String[] args) throws Exception {
        //创建一个MBeanServer，用来做MBean的容器
        MBeanServer server = MBeanServerFactory.createMBeanServer();
        //将Hello这个类注入到MBeanServer中，注入需要创建一个ObjectName类
        ObjectName helloName = new ObjectName("zhenghongqiang:name=HelloWorld");

        server.registerMBean(new Hello(), helloName);
        ObjectName adapterName = new ObjectName(
                "HelloAgent:name=htmladapter,port=8080");
        //创建一个AdaptorServer，这个类将决定MBean的管理界面，这里用最普通的Html型界面
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        adapter.setPort(8080);
        server.registerMBean(adapter, adapterName);

        adapter.start();
        System.out.println("start.....");
    }
}
