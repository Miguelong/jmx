package jmx;

/**
 * Created by miguel on 09/04/2018.
 * 要管理Hello则必须创建一个相应MBean，包含在MBean中方法都将是可以被管理的。MBean起名是有规范的，就是原类名后加上MBean字样
 */
public interface HelloMBean {
    /**
     * 这里面的方法都是可以管理的
     * @return
     */
    public String getName();

    public void setName(String name);

    public void printHello();

    public String printHello(String whoName);

}