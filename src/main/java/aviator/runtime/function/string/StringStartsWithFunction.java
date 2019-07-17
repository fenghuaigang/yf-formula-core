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
package aviator.runtime.function.string;

import java.util.Map;
import aviator.runtime.function.AbstractFunction;
import aviator.runtime.function.FunctionUtils;
import aviator.runtime.type.AviatorBoolean;
import aviator.runtime.type.AviatorObject;


/**
 * string.startsWith(s1,s2) function
 * 
 * @author dennis
 * 
 */
public class StringStartsWithFunction extends AbstractFunction {
  @Override
  public String getName() {
    return "string.startsWith";
  }


  @Override
  public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {

    String target = FunctionUtils.getStringValue(arg1, env);
    String param = FunctionUtils.getStringValue(arg2, env);
    return target.startsWith(param) ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
  }

}
