package aviator.runtime.function.system;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import aviator.runtime.function.AbstractFunction;
import aviator.runtime.function.FunctionUtils;
import aviator.runtime.type.AviatorObject;
import aviator.runtime.type.AviatorString;


/**
 * date_to_string function
 * 
 * @author boyan
 * 
 */
public class Date2StringFunction extends AbstractFunction {

  @Override
  public String getName() {
    return "date_to_string";
  }


  @Override
  public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
    Date date = (Date) arg1.getValue(env);
    String format = FunctionUtils.getStringValue(arg2, env);
    SimpleDateFormat dateFormat = DateFormatCache.getOrCreateDateFormat(format);
    return new AviatorString(dateFormat.format(date));
  }

}
