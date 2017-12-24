package spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * describe : aop java注入方式 ,使用另外的jar包（aspectjrt）
 * Created by jiadu on 2017/10/22 0022.
 */
@Aspect
@Component
public class Audience {

    @Pointcut("execution(* com.springmvc..*(..))")
    public void performance(){}

    @Before("performance()")
    public void silenceCellPhones(){
        System.out.println("befor");
    }

    @Before("performance()")
    public void takeSeats(){
        System.out.println("befor_2");
    }

    @AfterThrowing("performance()")
    public void demandRefund(){
        System.out.println("have exception");
    }

    @AfterReturning("performance()")
    public void after(){
        System.out.println("after");
    }

//    @Around("performance()")
//    public void watch(ProceedingJoinPoint jp){
//        System.out.println("wath-before");
//        try {
//            jp.proceed();
//            System.out.println("wath-after");
//        } catch (Throwable throwable) {
//            System.out.println("wath-exception");
//        }
//    }
}
