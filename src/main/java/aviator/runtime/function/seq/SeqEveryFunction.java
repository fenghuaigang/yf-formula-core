package aviator.runtime.function.seq;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import aviator.exception.ExpressionRuntimeException;
import aviator.runtime.function.AbstractFunction;
import aviator.runtime.function.FunctionUtils;
import aviator.runtime.type.AviatorBoolean;
import aviator.runtime.type.AviatorFunction;
import aviator.runtime.type.AviatorJavaType;
import aviator.runtime.type.AviatorNil;
import aviator.runtime.type.AviatorObject;
import aviator.runtime.type.AviatorRuntimeJavaType;


/**
 * Returns true if fun.call(x) is logical true for every x in sequence, else false.
 */
public class SeqEveryFunction extends AbstractFunction {

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
        if (!fun.call(env, new AviatorRuntimeJavaType(obj)).booleanValue(env)) {
          return AviatorBoolean.FALSE;
        }
      }
    } else if (clazz.isArray()) {
      int length = Array.getLength(first);
      for (int i = 0; i < length; i++) {
        Object obj = Array.get(first, i);
        if (!fun.call(env, new AviatorRuntimeJavaType(obj)).booleanValue(env)) {
          return AviatorBoolean.FALSE;
        }
      }
    } else {
      throw new IllegalArgumentException(arg1.desc(env) + " is not a seq collection");
    }
    return AviatorBoolean.TRUE;
  }


  @Override
  public String getName() {
    return "seq.every";
  }
}
