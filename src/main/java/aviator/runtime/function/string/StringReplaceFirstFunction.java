package aviator.runtime.function.string;

import java.util.Map;

import aviator.runtime.function.AbstractFunction;
import aviator.runtime.function.FunctionUtils;
import aviator.runtime.type.AviatorObject;
import aviator.runtime.type.AviatorString;


/**
 * string.replace_first function
 * 
 * @author boyan
 * 
 */
public class StringReplaceFirstFunction extends AbstractFunction {
  @Override
  public String getName() {
    return "string.replace_first";
  }


  @Override
  public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2,
      AviatorObject arg3) {
    String target = FunctionUtils.getStringValue(arg1, env);
    String regex = FunctionUtils.getStringValue(arg2, env);
    String replacement = FunctionUtils.getStringValue(arg3, env);
    return new AviatorString(target.replaceFirst(regex, replacement));

  }

}
