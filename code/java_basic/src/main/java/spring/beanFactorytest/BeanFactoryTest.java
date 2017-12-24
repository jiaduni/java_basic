package spring.beanFactorytest;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * describe :
 * Created by jiadu on 2017/10/30 0030.
 */
public class BeanFactoryTest {
    public static void main(String[] args) {
//        ApplicationContext ac = new ClassPathXmlApplicationContext("BeanFactoryTest.xml");
//        MytestBean mytestBean1 = (MytestBean) ac.getBean("myTestBean");
//        mytestBean1.testStr();
        BeanFactory bf= new XmlBeanFactory(new ClassPathResource("BeanFactoryTest.xml"));
        MytestBean mytestBean = (MytestBean) bf.getBean("myTestBean");
        mytestBean.testStr();
    }
}
