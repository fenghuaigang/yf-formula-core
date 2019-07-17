package aviator.runtime.function.seq;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import aviator.exception.ExpressionRuntimeException;
import aviator.runtime.function.AbstractFunction;
import aviator.runtime.function.FunctionUtils;
import aviator.runtime.type.AviatorFunction;
import aviator.runtime.type.AviatorJavaType;
import aviator.runtime.type.AviatorNil;
import aviator.runtime.type.AviatorObject;
import aviator.runtime.type.AviatorRuntimeJavaType;


/**
 * Returns the first logical true value of fun.call(x) for any x in sequence, else returns nil.
 * 
 * @author dennis
 *
 */
public class SeqSomeFunction extends AbstractFunction {

  @Override
  public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
    Object first = arg1.getValue(env);
    AviatorFunction fun = FunctionUtils.getFunction(arg2, env, 1);
    if (fun == null) {
      throw new ExpressionRuntimeException(
          "There is no function named " + ((AviatorJavaType) arg2).getName());
    }
    if (first == null) {
      return AviatorNil.NIL;
    }
    Class<?> clazz = first.getClass();

    if (Collection.class.isAssignableFrom(clazz)) {
      for (Object obj : (Collection<?>) first) {
        // return first fun returns true element.
        if (fun.call(env, new AviatorRuntimeJavaType(obj)).booleanValue(env)) {
          return new AviatorRuntimeJavaType(obj);
        }
      }
    } else if (clazz.isArray()) {
      int length = Array.getLength(first);
      for (int i = 0; i < length; i++) {
        Object obj = Array.get(first, i);
        // return first fun returns true element.
        if (fun.call(env, new AviatorRuntimeJavaType(obj)).booleanValue(env)) {
          return new AviatorRuntimeJavaType(obj);
        }
      }
    } else {
      throw new IllegalArgumentException(arg1.desc(env) + " is not a seq collection");
    }
    // else return nil
    return AviatorNil.NIL;
  }


  @Override
  public String getName() {
    return "seq.some";
  }
}
