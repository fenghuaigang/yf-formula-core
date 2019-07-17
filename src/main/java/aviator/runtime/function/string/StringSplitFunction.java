package aviator.runtime.function.string;

import java.util.Map;
import aviator.exception.ExpressionRuntimeException;
import aviator.runtime.function.AbstractFunction;
import aviator.runtime.function.FunctionUtils;
import aviator.runtime.type.AviatorObject;
import aviator.runtime.type.AviatorRuntimeJavaType;


/**
 * string.split function
 * 
 * @author boyan
 * 
 */
public class StringSplitFunction extends AbstractFunction {
  @Override
  public String getName() {
    return "string.split";
  }


  @Override
  public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2,
      AviatorObject arg3) {
    String target = FunctionUtils.getStringValue(arg1, env);
    if (target == null)
      throw new ExpressionRuntimeException("Could not split with null string");
    String regex = FunctionUtils.getStringValue(arg2, env);
    int limit = FunctionUtils.getNumberValue(arg3, env).intValue();
    return new AviatorRuntimeJavaType(target.split(regex, limit));
  }


  @Override
  public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
    String target = FunctionUtils.getStringValue(arg1, env);
    if (target == null)
      throw new ExpressionRuntimeException("Could not replace with null string");
    String regex = FunctionUtils.getStringValue(arg2, env);
    return new AviatorRuntimeJavaType(target.split(regex));
  }
}
