package jmx;

/**
 * Created by miguel on 09/04/2018.
 */
public class Hello implements HelloMBean {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void printHello() {
        System.out.println("Hello World, " + name);
    }

    public String printHello(String whoName) {
        System.out.println("Hello , " + whoName);
        return "Hello , " + whoName;
    }
}
