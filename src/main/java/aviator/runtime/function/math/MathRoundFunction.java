package aviator.runtime.function.math;

import java.util.Map;
import aviator.runtime.function.AbstractFunction;
import aviator.runtime.function.FunctionUtils;
import aviator.runtime.type.AviatorDouble;
import aviator.runtime.type.AviatorObject;


/**
 * math.round(d) function
 * 
 * @author dennis
 * 
 */
public class MathRoundFunction extends AbstractFunction {

  @Override
  public AviatorObject call(Map<String, Object> env, AviatorObject arg) {
    return AviatorDouble.valueOf(Math.round(FunctionUtils.getNumberValue(arg, env).doubleValue()));
  }


  @Override
  public String getName() {
    return "math.round";
  }

}
