/**
 * Copyright (C) 2010 dennis zhuang (killme2008@gmail.com)
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 **/
package aviator.runtime.function.seq;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import aviator.exception.ExpressionRuntimeException;
import aviator.runtime.function.AbstractFunction;
import aviator.runtime.function.FunctionUtils;
import aviator.runtime.type.AviatorFunction;
import aviator.runtime.type.AviatorJavaType;
import aviator.runtime.type.AviatorObject;
import aviator.runtime.type.AviatorRuntimeJavaType;


/**
 * map(col,fun) function to iterate seq with function
 * 
 * @author dennis
 * 
 */
public class SeqMapFunction extends AbstractFunction {

  @Override
  @SuppressWarnings("unchecked")
  public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {

    Object first = arg1.getValue(env);
    AviatorFunction fun = FunctionUtils.getFunction(arg2, env, 1);
    if (fun == null) {
      throw new ExpressionRuntimeException(
          "There is no function named " + ((AviatorJavaType) arg2).getName());
    }
    if (first == null) {
      throw new NullPointerException("null seq");
    }
    Class<?> clazz = first.getClass();

    if (Collection.class.isAssignableFrom(clazz)) {
      Collection<Object> result = null;
      try {
        result = (Collection<Object>) clazz.newInstance();
      } catch (Throwable t) {
        // ignore
        result = new ArrayList<Object>();
      }
      for (Object obj : (Collection<?>) first) {
        result.add(fun.call(env, new AviatorRuntimeJavaType(obj)).getValue(env));
      }
      return new AviatorRuntimeJavaType(result);
    } else if (clazz.isArray()) {
      // Object[] seq = (Object[]) first;
      int length = Array.getLength(first);
      Object result = Array.newInstance(Object.class, length);
      int index = 0;
      for (int i = 0; i < length; i++) {
        Object obj = Array.get(first, i);
        Array.set(result, index++, fun.call(env, new AviatorRuntimeJavaType(obj)).getValue(env));
      }
      return new AviatorRuntimeJavaType(result);
    } else {
      throw new IllegalArgumentException(arg1.desc(env) + " is not a seq");
    }

  }


  @Override
  public String getName() {
    return "map";
  }

}
