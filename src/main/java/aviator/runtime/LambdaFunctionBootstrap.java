package aviator.runtime;

import java.lang.invoke.MethodHandle;
import java.util.List;
import aviator.Expression;
import aviator.exception.ExpressionRuntimeException;
import aviator.runtime.function.LambdaFunction;
import aviator.utils.Env;

/**
 * A lambda function creator.
 *
 * @author dennis
 *
 */
public class LambdaFunctionBootstrap {
  // the generated lambda class name
  private String name;
  // The compiled lambda body expression
  private Expression expression;
  // The method handle to create lambda instance.
  private MethodHandle constructor;
  // The arguments list.
  private List<String> arguments;


  public String getName() {
    return this.name;
  }

  public LambdaFunctionBootstrap(String name, Expression expression, MethodHandle constructor,
      List<String> arguments) {
    super();
    this.name = name;
    this.expression = expression;
    this.constructor = constructor;
    this.arguments = arguments;
  }


  /**
   * Create a lambda function.
   *
   * @param env
   * @return
   */
  public LambdaFunction newInstance(Env env) {
    try {
      return (LambdaFunction) constructor.invoke(arguments, expression, env);
    } catch (Throwable t) {
      throw new ExpressionRuntimeException(t);
    }
  }
}
