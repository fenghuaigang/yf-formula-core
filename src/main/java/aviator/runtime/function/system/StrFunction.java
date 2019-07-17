package aviator.runtime.function.system;

import java.util.Map;
import aviator.runtime.function.AbstractFunction;
import aviator.runtime.type.AviatorObject;
import aviator.runtime.type.AviatorString;


/**
 * Cast value to string
 *
 * @author dennis
 * @Date 2011-5-18
 * @since 1.1.1
 *
 */
public class StrFunction extends AbstractFunction {

  @Override
  public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
    final Object value = arg1.getValue(env);
    return new AviatorString(value == null ? "null" : value.toString());
  }

  @Override
  public String getName() {
    return "str";
  }

}
