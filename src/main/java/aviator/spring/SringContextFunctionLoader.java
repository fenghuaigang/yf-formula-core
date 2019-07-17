package aviator.spring;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import aviator.FunctionLoader;
import aviator.runtime.type.AviatorFunction;

/**
 * Function loader based on spring context, try to find the function by name from spring context.
 *
 * @since 4.0.0
 * @author dennis
 *
 */
public class SringContextFunctionLoader implements FunctionLoader {

  private ApplicationContext applicationContext;


  public SringContextFunctionLoader() {
    super();
  }


  public SringContextFunctionLoader(ApplicationContext applicationContext) {
    super();
    this.applicationContext = applicationContext;
  }


  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }


  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }


  @Override
  public AviatorFunction onFunctionNotFound(String name) {
    try {
      return (AviatorFunction) this.applicationContext.getBean(name);
    } catch (NoSuchBeanDefinitionException e) {
      return null;
    }
  }

}
