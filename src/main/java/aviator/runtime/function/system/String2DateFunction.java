package aviator.runtime.function.system;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import aviator.exception.ExpressionRuntimeException;
import aviator.runtime.function.AbstractFunction;
import aviator.runtime.function.FunctionUtils;
import aviator.runtime.type.AviatorObject;
import aviator.runtime.type.AviatorRuntimeJavaType;


/**
 * string_to_date function
 * 
 * @author boyan
 * 
 */
public class String2DateFunction extends AbstractFunction {

  @Override
  public String getName() {
    return "string_to_date";
  }


  @Override
  public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
    String source = FunctionUtils.getStringValue(arg1, env);
    String format = FunctionUtils.getStringValue(arg2, env);
    SimpleDateFormat dateFormat = DateFormatCache.getOrCreateDateFormat(format);
    try {
      return new AviatorRuntimeJavaType(dateFormat.parse(source));
    } catch (ParseException e) {
      throw new ExpressionRuntimeException("Cast string to date failed", e);
    }
  }

}
