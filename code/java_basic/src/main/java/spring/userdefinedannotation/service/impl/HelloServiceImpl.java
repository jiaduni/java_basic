package spring.userdefinedannotation.service.impl;


import spring.userdefinedannotation.annotation.RpcService;
import spring.userdefinedannotation.service.HelloService;

@RpcService("HelloServicebb")
public class HelloServiceImpl implements HelloService {
    public String hello(String name) {
        return "Hello! " + name;
    }
    
    public void test(){
    	System.out.println("test");
    }
}
