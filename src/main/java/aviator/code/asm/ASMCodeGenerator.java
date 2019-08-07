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
package aviator.code.asm;

import static aviator.asm.Opcodes.*;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;
import aviator.AviatorEvaluatorInstance;
import aviator.ClassExpression;
import aviator.Expression;
import aviator.Options;
import aviator.asm.ClassWriter;
import aviator.asm.Label;
import aviator.asm.MethodVisitor;
import aviator.asm.Opcodes;
import aviator.code.CodeGenerator;
import aviator.code.LambdaGenerator;
import aviator.exception.CompileExpressionErrorException;
import aviator.exception.ExpressionRuntimeException;
import aviator.lexer.token.NumberToken;
import aviator.lexer.token.OperatorType;
import aviator.lexer.token.Token;
import aviator.lexer.token.Token.TokenType;
import aviator.lexer.token.Variable;
import aviator.parser.AviatorClassLoader;
import aviator.parser.Parser;
import aviator.runtime.LambdaFunctionBootstrap;
import aviator.runtime.op.OperationRuntime;
import aviator.utils.Env;
import aviator.utils.TypeUtils;


/**
 * Code generator using asm
 *
 * @author dennis
 *
 */
public class ASMCodeGenerator implements CodeGenerator {

  private static final String FIELD_PREFIX = "f";
  // evaluator instance
  private AviatorEvaluatorInstance instance;
  /**
   * Compile environment only has the *instance*.
   */
  private Env compileEnv;
  // Class Writer to generate class
  // private final ClassWriter clazzWriter;
  // Trace visitor
  // private ClassVisitor traceClassVisitor;
  // Check visitor
  private ClassWriter classWriter;
  // Method visitor
  private MethodVisitor mv;
  // Class name
  private final String className;
  // Class loader to define generated class
  private final AviatorClassLoader classLoader;
  // lambda function generator
  private LambdaGenerator lambdaGenerator;
  // parser
  private Parser parser;

  private static final AtomicLong CLASS_COUNTER = new AtomicLong();

  /**
   * Operands count to check stack frames
   */
  private int operandsCount = 0;

  private int maxStacks = 0;
  private int maxLocals = 2;

  private int fieldCounter = 0;

  private Map<String/* variable name */, String/* inner var name */> innerVars =
      Collections.emptyMap();
  private Map<String/* method name */, String/* inner method name */> innerMethodMap =
      Collections.emptyMap();

  private Map<String, Integer/* counter */> varTokens = Collections.emptyMap();
  private Map<String, Integer/* counter */> methodTokens = Collections.emptyMap();

  private final Map<Label, Map<String/* inner name */, Integer/* local index */>> labelNameIndexMap =
      new IdentityHashMap<Label, Map<String, Integer>>();

  /**
   * Compiled lambda functions.
   */
  private Map<String, LambdaFunctionBootstrap> lambdaBootstraps;

  private static final Label START_LABEL = new Label();

  private Label currentLabel = START_LABEL;

  /**
   * parent code generator when compiling lambda.
   */
  private CodeGenerator parentCodeGenerator;

  @Override
  public void setParser(Parser parser) {
    this.parser = parser;
  }


  private void setMaxStacks(int newMaxStacks) {
    if (newMaxStacks > this.maxStacks) {
      this.maxStacks = newMaxStacks;
    }
  }


  public ASMCodeGenerator(AviatorEvaluatorInstance instance, AviatorClassLoader classLoader,
      OutputStream traceOut, boolean trace) {
    this.classLoader = classLoader;
    this.instance = instance;
    this.compileEnv = new Env();
    this.compileEnv.setInstance(this.instance);
    // Generate inner class name
    this.className = "Script_" + System.currentTimeMillis() + "_" + CLASS_COUNTER.getAndIncrement();
    // Auto compute frames
    this.classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    // if (trace) {
    // this.traceClassVisitor = new TraceClassVisitor(this.clazzWriter, new PrintWriter(traceOut));
    // this.classWriter = new CheckClassAdapter(this.traceClassVisitor);
    // } else {
    // this.classWriter = new CheckClassAdapter(this.clazzWriter);
    // }
    this.visitClass();
  }



  public AviatorClassLoader getClassLoader() {
    return classLoader;
  }


  LambdaGenerator getLambdaGenerator() {
    return lambdaGenerator;
  }


  public void start() {
    this.makeConstructor();
    this.startVisitMethodCode();
  }


  private void startVisitMethodCode() {
    this.mv = this.classWriter.visitMethod(ACC_PUBLIC + +ACC_FINAL, "execute0",
        "(Laviator/utils/Env;)Ljava/lang/Object;",
        "(Laviator/utils/Env;)Ljava/lang/Object;", null);
    this.mv.visitCode();
  }


  private void endVisitMethodCode() {
    if (this.operandsCount > 0) {
      this.loadEnv();
      this.mv.visitMethodInsn(INVOKEVIRTUAL, "aviator/runtime/type/AviatorObject",
          "getValue", "(Ljava/utils/Map;)Ljava/lang/Object;");
      this.mv.visitInsn(ARETURN);
      this.popOperand();
      this.popOperand();
    } else {
      this.mv.visitInsn(ACONST_NULL);
      this.mv.visitInsn(ARETURN);
      this.pushOperand();
      this.popOperand();
    }
    if (this.operandsCount > 0) {
      throw new CompileExpressionErrorException(
          "operand stack is not empty,count=" + this.operandsCount);
    }
    this.mv.visitMaxs(this.maxStacks, this.maxLocals);
    this.mv.visitEnd();

  }


  private void endVisitClass() {
    this.classWriter.visitEnd();
  }


  /**
   * Make a default constructor
   */
  private void makeConstructor() {
    {
      this.mv = this.classWriter.visitMethod(ACC_PUBLIC, "<init>",
          "(Laviator/AviatorEvaluatorInstance;Ljava/utils/List;)V", null, null);
      this.mv.visitCode();
      this.mv.visitVarInsn(ALOAD, 0);
      this.mv.visitVarInsn(ALOAD, 1);
      this.mv.visitVarInsn(ALOAD, 2);
      this.mv.visitMethodInsn(INVOKESPECIAL, "aviator/ClassExpression", "<init>",
          "(Laviator/AviatorEvaluatorInstance;Ljava/utils/List;)V");
      if (!this.innerVars.isEmpty()) {
        for (Map.Entry<String, String> entry : this.innerVars.entrySet()) {
          String outterName = entry.getKey();
          String innerName = entry.getValue();
          this.mv.visitVarInsn(ALOAD, 0);
          this.mv.visitTypeInsn(NEW, "aviator/runtime/type/AviatorJavaType");
          this.mv.visitInsn(DUP);
          this.mv.visitLdcInsn(outterName);
          this.mv.visitMethodInsn(INVOKESPECIAL,
              "aviator/runtime/type/AviatorJavaType", "<init>",
              "(Ljava/lang/String;)V");
          this.mv.visitFieldInsn(PUTFIELD, this.className, innerName,
              "Laviator/runtime/type/AviatorJavaType;");
        }
      }
      if (!this.innerMethodMap.isEmpty()) {
        for (Map.Entry<String, String> entry : this.innerMethodMap.entrySet()) {
          String outterName = entry.getKey();
          String innerName = entry.getValue();
          this.mv.visitVarInsn(ALOAD, 0);
          this.mv.visitVarInsn(ALOAD, 1);
          this.mv.visitLdcInsn(outterName);
          this.mv.visitMethodInsn(INVOKEVIRTUAL, "aviator/AviatorEvaluatorInstance",
              "getFunction",
              "(Ljava/lang/String;)Laviator/runtime/type/AviatorFunction;");
          this.mv.visitFieldInsn(PUTFIELD, this.className, innerName,
              "Laviator/runtime/type/AviatorFunction;");
        }
      }

      this.mv.visitInsn(RETURN);
      this.mv.visitMaxs(4, 1);
      this.mv.visitEnd();
    }
  }


  private void visitClass() {
    this.classWriter.visit(instance.getBytecodeVersion(), ACC_PUBLIC + ACC_SUPER, this.className,
        null, "aviator/ClassExpression", null);
  }


  /**
   * Make a label
   *
   * @return
   */
  private Label makeLabel() {
    return new Label();
  }


  /*
   * (non-Javadoc)
   *
   * @see com.googlecode.aviator.code.CodeGenerator#onAdd(com.googlecode.aviator .lexer.token.Token)
   */
  @Override
  public void onAdd(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.ADD, "add");
  }

  private void loadOpType(OperatorType opType) {
    this.pushOperand();
    this.mv.visitFieldInsn(GETSTATIC, "aviator/lexer/token/OperatorType",
        opType.name(), "Laviator/lexer/token/OperatorType;");
  }


  /**
   * Pop a operand from stack
   */
  private void popOperand() {
    this.operandsCount--;
  }


  /**
   * Pop a operand from stack
   */
  private void popOperand(int n) {
    this.operandsCount -= n;
  }


  /*
   * (non-Javadoc)
   *
   * @see com.googlecode.aviator.code.CodeGenerator#onSub(com.googlecode.aviator .lexer.token.Token)
   */
  @Override
  public void onSub(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.SUB, "sub");
  }


  /*
   * (non-Javadoc)
   *
   * @see com.googlecode.aviator.code.CodeGenerator#onMult(com.googlecode.aviator
   * .lexer.token.Token)
   */
  @Override
  public void onMult(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.MULT, "mult");
  }


  @Override
  public void onAssignment(Token<?> lookhead) {
    this.loadEnv();

    this.mv.visitMethodInsn(INVOKEVIRTUAL, "aviator/runtime/type/AviatorJavaType",
        "setValue",
        "(Laviator/runtime/type/AviatorObject;Ljava/utils/Map;)Laviator/runtime/type/AviatorObject;");
    this.popOperand();
    this.popOperand();
    this.popOperand();
    this.pushOperand();
  }


  /*
   * (non-Javadoc)
   *
   * @see com.googlecode.aviator.code.CodeGenerator#onDiv(com.googlecode.aviator .lexer.token.Token)
   */
  @Override
  public void onDiv(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.DIV, "div");
  }


  /*
   * (non-Javadoc)
   *
   * @see com.googlecode.aviator.code.CodeGenerator#onMod(com.googlecode.aviator .lexer.token.Token)
   */
  @Override
  public void onMod(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.MOD, "mod");
  }


  /**
   * Do logic operation "&&" left operand
   */
  @Override
  public void onAndLeft(Token<?> lookhead) {
    this.loadEnv();
    this.visitLeftBranch(IFEQ, OperatorType.AND);
  }


  private void visitBoolean() {
    this.mv.visitMethodInsn(INVOKEVIRTUAL, "aviator/runtime/type/AviatorObject",
        "booleanValue", "(Ljava/utils/Map;)Z");
  }


  private void pushLabel0(Label l0) {
    this.l0stack.push(l0);
  }


  /**
   * Do logic operation "&&" right operand
   */
  @Override
  public void onAndRight(Token<?> lookhead) {
    this.visitRightBranch(IFEQ, OperatorType.AND);
    this.popOperand(); // boolean object
    this.popOperand(); // environment
    this.pushOperand();
  }


  private void visitRightBranch(int ints, OperatorType opType) {
    if (!OperationRuntime.hasRuntimeContext(this.compileEnv, opType)) {
      this.loadEnv();
      String first = "TRUE";
      String second = "FALSE";
      if (opType == OperatorType.OR) {
        first = "FALSE";
        second = "TRUE";
      }

      this.visitBoolean();
      this.mv.visitJumpInsn(ints, this.peekLabel0());
      // Result is true
      this.mv.visitFieldInsn(GETSTATIC, "aviator/runtime/type/AviatorBoolean", first,
          "Laviator/runtime/type/AviatorBoolean;");
      Label l1 = this.makeLabel();
      this.mv.visitJumpInsn(GOTO, l1);
      this.visitLabel(this.popLabel0());
      // Result is false
      this.mv.visitFieldInsn(GETSTATIC, "aviator/runtime/type/AviatorBoolean",
          second, "Laviator/runtime/type/AviatorBoolean;");
      this.visitLabel(l1);
    } else {
      this.loadOpType(opType);
      this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/op/OperationRuntime",
          "eval",
          "(Laviator/runtime/type/AviatorObject;Ljava/utils/Map;Laviator/runtime/type/AviatorObject;Laviator/lexer/token/OperatorType;)Laviator/runtime/type/AviatorObject;");
      this.popOperand();
    }
  }

  /**
   * Label stack for ternary operator
   */
  private final Stack<Label> l0stack = new Stack<Label>();
  private final Stack<Label> l1stack = new Stack<Label>();


  @Override
  public void onTernaryBoolean(Token<?> lookhead) {
    this.loadEnv();
    this.visitBoolean();
    Label l0 = this.makeLabel();
    Label l1 = this.makeLabel();
    this.pushLabel0(l0);
    this.pushLabel1(l1);
    this.mv.visitJumpInsn(IFEQ, l0);
    this.popOperand();
    this.popOperand();
    this.pushOperand(1); // add two booleans

    this.popOperand(); // pop the last result
  }


  private void pushLabel1(Label l1) {
    this.l1stack.push(l1);
  }


  @Override
  public void onTernaryLeft(Token<?> lookhead) {
    this.mv.visitJumpInsn(GOTO, this.peekLabel1());
    this.visitLabel(this.popLabel0());
    this.popOperand(); // pop one boolean
  }


  private Label peekLabel1() {
    return this.l1stack.peek();
  }


  @Override
  public void onTernaryRight(Token<?> lookhead) {
    this.visitLabel(this.popLabel1());
    this.popOperand(); // pop one boolean
  }


  @Override
  public void onTernaryEnd(Token<?> lookhead) {
    while (--this.operandsCount > 0) {
      this.mv.visitInsn(POP);
    }
  }

  private Label popLabel1() {
    return this.l1stack.pop();
  }


  /**
   * Do logic operation "||" right operand
   */
  @Override
  public void onJoinRight(Token<?> lookhead) {
    this.visitRightBranch(IFNE, OperatorType.OR);
    this.popOperand();
    this.popOperand();
    this.pushOperand();

  }


  private void visitLabel(Label label) {
    this.mv.visitLabel(label);
    this.currentLabel = label;
  }


  private Label peekLabel0() {
    return this.l0stack.peek();
  }


  private Label popLabel0() {
    return this.l0stack.pop();
  }


  /**
   * Do logic operation "||" left operand
   */
  @Override
  public void onJoinLeft(Token<?> lookhead) {
    this.loadEnv();
    this.visitLeftBranch(IFNE, OperatorType.OR);
  }


  private void visitLeftBranch(int ints, OperatorType opType) {
    if (!OperationRuntime.hasRuntimeContext(this.compileEnv, opType)) {
      this.visitBoolean();
      Label l0 = this.makeLabel();
      this.pushLabel0(l0);
      this.mv.visitJumpInsn(ints, l0);
      this.popOperand();
    }
    this.popOperand();
  }


  @Override
  public void onEq(Token<?> lookhead) {
    this.doCompareAndJump(IFNE, OperatorType.EQ);
  }


  @Override
  public void onMatch(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.MATCH, "match");
    this.popOperand();
    this.pushOperand();
  }


  @Override
  public void onNeq(Token<?> lookhead) {
    this.doCompareAndJump(IFEQ, OperatorType.NEQ);
  }


  private void doCompareAndJump(int ints, OperatorType opType) {
    this.loadEnv();
    this.visitCompare(ints, opType);
    this.popOperand();
    this.popOperand();
  }


  private void visitCompare(int ints, OperatorType opType) {
    if (!OperationRuntime.hasRuntimeContext(this.compileEnv, opType)) {
      this.mv.visitMethodInsn(INVOKEVIRTUAL, "aviator/runtime/type/AviatorObject",
          "compare", "(Laviator/runtime/type/AviatorObject;Ljava/utils/Map;)I");
      Label l0 = this.makeLabel();
      Label l1 = this.makeLabel();
      this.mv.visitJumpInsn(ints, l0);
      this.mv.visitFieldInsn(GETSTATIC, "aviator/runtime/type/AviatorBoolean",
          "TRUE", "Laviator/runtime/type/AviatorBoolean;");
      this.mv.visitJumpInsn(GOTO, l1);
      this.visitLabel(l0);
      this.mv.visitFieldInsn(GETSTATIC, "aviator/runtime/type/AviatorBoolean",
          "FALSE", "Laviator/runtime/type/AviatorBoolean;");
      this.visitLabel(l1);
    } else {
      this.loadOpType(opType);
      this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/op/OperationRuntime",
          "eval",
          "(Laviator/runtime/type/AviatorObject;Laviator/runtime/type/AviatorObject;Ljava/utils/Map;Laviator/lexer/token/OperatorType;)Laviator/runtime/type/AviatorObject;");
      this.popOperand();
    }

  }


  @Override
  public void onGe(Token<?> lookhead) {
    this.doCompareAndJump(IFLT, OperatorType.GE);
  }


  @Override
  public void onGt(Token<?> lookhead) {
    this.doCompareAndJump(IFLE, OperatorType.GT);
  }


  @Override
  public void onLe(Token<?> lookhead) {
    this.doCompareAndJump(IFGT, OperatorType.LE);

  }


  @Override
  public void onLt(Token<?> lookhead) {
    this.doCompareAndJump(IFGE, OperatorType.LT);
  }


  /**
   *
   * @param extras 额外的栈空间大小
   */
  public void pushOperand(int extras) {
    this.operandsCount++;
    this.operandsCount += extras;
    this.setMaxStacks(this.operandsCount);
  }


  /**
   * Logic operation '!'
   */
  @Override
  public void onNot(Token<?> lookhead) {
    this.visitUnaryOperator(OperatorType.NOT, "not");
  }

  private void visitBinOperator(OperatorType opType, String methodName) {
    if (!OperationRuntime.hasRuntimeContext(this.compileEnv, opType)) {
      // swap arguments for regular-expression match operator.
      if (opType == OperatorType.MATCH) {
        this.mv.visitInsn(SWAP);
      }
      this.loadEnv();
      this.mv.visitMethodInsn(INVOKEVIRTUAL, "aviator/runtime/type/AviatorObject",
          methodName,
          "(Laviator/runtime/type/AviatorObject;Ljava/utils/Map;)Laviator/runtime/type/AviatorObject;");
    } else {
      this.loadEnv();
      this.loadOpType(opType);
      this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/op/OperationRuntime",
          "eval",
          "(Laviator/runtime/type/AviatorObject;Laviator/runtime/type/AviatorObject;Ljava/utils/Map;Laviator/lexer/token/OperatorType;)Laviator/runtime/type/AviatorObject;");
      this.popOperand();
    }
    this.popOperand();
    this.popOperand();
  }

  private void visitUnaryOperator(OperatorType opType, String methodName) {
    this.mv.visitTypeInsn(CHECKCAST, "aviator/runtime/type/AviatorObject");
    this.loadEnv();

    if (!OperationRuntime.hasRuntimeContext(this.compileEnv, opType)) {
      this.mv.visitMethodInsn(INVOKEVIRTUAL, "aviator/runtime/type/AviatorObject",
          methodName, "(Ljava/utils/Map;)Laviator/runtime/type/AviatorObject;");
    } else {
      this.loadOpType(opType);
      this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/op/OperationRuntime",
          "eval",
          "(Laviator/runtime/type/AviatorObject;Ljava/utils/Map;Laviator/lexer/token/OperatorType;)Laviator/runtime/type/AviatorObject;");
      this.popOperand();
    }


    this.popOperand();
  }


  /**
   * Bit operation '~'
   */
  @Override
  public void onBitNot(Token<?> lookhead) {
    this.visitUnaryOperator(OperatorType.BIT_NOT, "bitNot");
  }


  /*
   * (non-Javadoc)
   *
   * @see com.googlecode.aviator.code.CodeGenerator#onNeg(com.googlecode.aviator .lexer.token.Token,
   * int)
   */
  @Override
  public void onNeg(Token<?> lookhead) {
    this.visitUnaryOperator(OperatorType.NEG, "neg");
  }


  /*
   * (non-Javadoc)
   *
   * @see com.googlecode.aviator.code.CodeGenerator#getResult()
   */
  @Override
  public Expression getResult() {
    this.end();

    byte[] bytes = this.classWriter.toByteArray();
    try {
      Class<?> defineClass =
          ClassDefiner.defineClass(this.className, Expression.class, bytes, this.classLoader);
      Constructor<?> constructor =
          defineClass.getConstructor(AviatorEvaluatorInstance.class, List.class);
      ClassExpression exp = (ClassExpression) constructor.newInstance(this.instance,
          new ArrayList<String>(this.varTokens.keySet()));
      exp.setLambdaBootstraps(lambdaBootstraps);
      return exp;
    } catch (Exception e) {
      if (e.getCause() instanceof ExpressionRuntimeException) {
        throw (ExpressionRuntimeException) e.getCause();
      }
      throw new CompileExpressionErrorException("define class error", e);
    }
  }

  private void end() {
    this.endVisitMethodCode();
    this.endVisitClass();
  }


  /*
   * (non-Javadoc)
   *
   * @see com.googlecode.aviator.code.CodeGenerator#onConstant(com.googlecode.aviator
   * .lexer.token.Token)
   */
  @Override
  public void onConstant(Token<?> lookhead) {
    if (lookhead == null) {
      return;
    }
    // load token to stack
    switch (lookhead.getType()) {
      case Number:
        // load numbers
        NumberToken numberToken = (NumberToken) lookhead;
        Number number = numberToken.getNumber();

        if (TypeUtils.isBigInt(number)) {
          this.mv.visitLdcInsn(numberToken.getLexeme());
          this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/type/AviatorBigInt",
              "valueOf", "(Ljava/lang/String;)Laviator/runtime/type/AviatorBigInt;");
        } else if (TypeUtils.isDecimal(number)) {
          this.loadEnv();
          // this.pushOperand();
          this.mv.visitLdcInsn(numberToken.getLexeme());
          this.mv.visitMethodInsn(INVOKESTATIC,
              "aviator/runtime/type/AviatorDecimal", "valueOf",
              "(Ljava/utils/Map;Ljava/lang/String;)Laviator/runtime/type/AviatorDecimal;");
          this.popOperand();
        } else if (TypeUtils.isDouble(number)) {
          this.mv.visitLdcInsn(number);
          this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/type/AviatorDouble",
              "valueOf", "(D)Laviator/runtime/type/AviatorDouble;");
        } else {
          this.mv.visitLdcInsn(number);
          this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/type/AviatorLong",
              "valueOf", "(J)Laviator/runtime/type/AviatorLong;");
        }
        this.pushOperand();
        // this.popOperand();
        // this.popOperand();
        break;
      case String:
        // load string
        this.mv.visitTypeInsn(NEW, "aviator/runtime/type/AviatorString");
        this.mv.visitInsn(DUP);
        this.mv.visitLdcInsn(lookhead.getValue(null));
        this.mv.visitMethodInsn(INVOKESPECIAL, "aviator/runtime/type/AviatorString",
            "<init>", "(Ljava/lang/String;)V");
        this.pushOperand(2);
        this.popOperand();
        this.popOperand();
        break;
      case Pattern:
        // load pattern
        this.mv.visitTypeInsn(NEW, "aviator/runtime/type/AviatorPattern");
        this.mv.visitInsn(DUP);
        this.mv.visitLdcInsn(lookhead.getValue(null));
        this.mv.visitMethodInsn(INVOKESPECIAL, "aviator/runtime/type/AviatorPattern",
            "<init>", "(Ljava/lang/String;)V");
        this.pushOperand(2);
        this.popOperand();
        this.popOperand();
        break;
      case Variable:
        // load variable
        Variable variable = (Variable) lookhead;

        if (variable.equals(Variable.TRUE)) {
          this.mv.visitFieldInsn(GETSTATIC, "aviator/runtime/type/AviatorBoolean",
              "TRUE", "Laviator/runtime/type/AviatorBoolean;");
          this.pushOperand();
        } else if (variable.equals(Variable.FALSE)) {
          this.mv.visitFieldInsn(GETSTATIC, "aviator/runtime/type/AviatorBoolean",
              "FALSE", "Laviator/runtime/type/AviatorBoolean;");
          this.pushOperand();
        } else if (variable.equals(Variable.NIL)) {
          this.mv.visitFieldInsn(GETSTATIC, "aviator/runtime/type/AviatorNil", "NIL",
              "Laviator/runtime/type/AviatorNil;");
          this.pushOperand();
        } else {
          String outterVarName = variable.getLexeme();
          String innerVarName = this.innerVars.get(outterVarName);
          if (innerVarName != null) {
            // Is it stored in local?
            Map<String, Integer> name2Index = this.labelNameIndexMap.get(this.currentLabel);
            if (name2Index != null && name2Index.get(innerVarName) != null) {
              int localIndex = name2Index.get(innerVarName);
              this.mv.visitVarInsn(ALOAD, localIndex);
              this.pushOperand();
            } else {
              // Get field at first time
              this.mv.visitVarInsn(ALOAD, 0);
              this.mv.visitFieldInsn(GETFIELD, this.className, innerVarName,
                  "Laviator/runtime/type/AviatorJavaType;");
              // Variable is used more than once,store it to local
              if (this.varTokens.get(outterVarName) > 1) {
                this.mv.visitInsn(DUP);
                int localIndex = this.getLocalIndex();
                this.mv.visitVarInsn(ASTORE, localIndex);
                if (name2Index == null) {
                  name2Index = new HashMap<String, Integer>();
                  this.labelNameIndexMap.put(this.currentLabel, name2Index);
                }
                name2Index.put(innerVarName, localIndex);
                this.pushOperand(2);
                this.popOperand();
                this.popOperand();
              } else {
                this.pushOperand(1);
                this.popOperand();
              }
            }

          } else {
            this.mv.visitTypeInsn(NEW, "aviator/runtime/type/AviatorJavaType");
            this.mv.visitInsn(DUP);
            this.mv.visitLdcInsn(outterVarName);
            this.mv.visitMethodInsn(INVOKESPECIAL,
                "aviator/runtime/type/AviatorJavaType", "<init>",
                "(Ljava/lang/String;)V");
            this.pushOperand(2);
            this.popOperand();
            this.popOperand();
          }

        }
        break;
    }

  }


  public void setLambdaBootstraps(Map<String, LambdaFunctionBootstrap> lambdaBootstraps) {
    this.lambdaBootstraps = lambdaBootstraps;
  }


  public void initVariables(Map<String, Integer/* counter */> varTokens) {
    this.varTokens = varTokens;
    this.innerVars = new HashMap<String, String>(varTokens.size());
    for (String outterVarName : varTokens.keySet()) {
      // Use inner variable name instead of outter variable name
      String innerVarName = this.getInnerName(outterVarName);
      this.innerVars.put(outterVarName, innerVarName);
      this.classWriter.visitField(ACC_PRIVATE + ACC_FINAL, innerVarName,
          "Laviator/runtime/type/AviatorJavaType;", null, null).visitEnd();

    }
  }


  public void initMethods(Map<String, Integer/* counter */> methods) {
    this.methodTokens = methods;
    this.innerMethodMap = new HashMap<String, String>(methods.size());
    for (String outterMethodName : methods.keySet()) {
      // Use inner method name instead of outter method name
      String innerMethodName = this.getInnerName(outterMethodName);
      this.innerMethodMap.put(outterMethodName, innerMethodName);
      this.classWriter.visitField(ACC_PRIVATE + ACC_FINAL, innerMethodName,
          "Laviator/runtime/type/AviatorFunction;", null, null).visitEnd();
    }
  }


  private String getInnerName(String varName) {
    return FIELD_PREFIX + this.fieldCounter++;
  }


  private static String getInvokeMethodDesc(int paramCount) {
    StringBuilder sb = new StringBuilder("(Ljava/utils/Map;");
    if (paramCount <= 20) {
      for (int i = 0; i < paramCount; i++) {
        sb.append("Laviator/runtime/type/AviatorObject;");
      }
    } else {
      for (int i = 0; i < 20; i++) {
        sb.append("Laviator/runtime/type/AviatorObject;");
      }
      // variadic params as an array
      sb.append("[Laviator/runtime/type/AviatorObject;");
    }
    sb.append(")Laviator/runtime/type/AviatorObject;");
    return sb.toString();
  }


  @Override
  public void onMethodInvoke(Token<?> lookhead) {
    final MethodMetaData methodMetaData = this.methodMetaDataStack.pop();
    final int parameterCount = methodMetaData.parameterCount;
    if (parameterCount >= 20) {
      if (parameterCount == 20) {
        // pop the list
        this.mv.visitInsn(Opcodes.POP);
        this.popOperand();
      } else {
        // to array
        this.mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I");
        this.mv.visitTypeInsn(Opcodes.ANEWARRAY,
            "aviator/runtime/type/AviatorObject");
        int arrayIndex = this.getLocalIndex();
        this.mv.visitVarInsn(ASTORE, arrayIndex);
        this.mv.visitVarInsn(ALOAD, methodMetaData.variadicListIndex);
        this.mv.visitVarInsn(ALOAD, arrayIndex);
        this.mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "toArray",
            "([Ljava/lang/Object;)[Ljava/lang/Object;");

        this.mv.visitTypeInsn(CHECKCAST, "[Laviator/runtime/type/AviatorObject;");

        this.popOperand(); // pop list to get size
        this.pushOperand(); // new array, store and load it
        this.pushOperand(); // load list
        this.popOperand(); // list.toArray
      }
    }
    this.mv.visitMethodInsn(INVOKEINTERFACE, "aviator/runtime/type/AviatorFunction",
        "call", getInvokeMethodDesc(parameterCount));

    this.popOperand(); // method object
    this.popOperand(); // env map
    // pop operands
    if (parameterCount <= 20) {
      this.popOperand(parameterCount);
    } else {
      // 20 params + one array
      this.popOperand(21);
    }
    // push result
    this.pushOperand();
  }


  @Override
  public void onMethodParameter(Token<?> lookhead) {
    MethodMetaData currentMethodMetaData = this.methodMetaDataStack.peek();
    if (currentMethodMetaData.parameterCount >= 20) {
      // Add last param to variadic param list
      assert currentMethodMetaData.variadicListIndex >= 0;
      this.mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
      this.mv.visitInsn(Opcodes.POP);
      this.mv.visitVarInsn(ALOAD, currentMethodMetaData.variadicListIndex);
      this.popOperand(); // pop list
      this.popOperand(); // pop param
      this.pushOperand(); // list.add result
      this.popOperand(); // pop last result
      this.pushOperand(); // load list
    }

    currentMethodMetaData.parameterCount++;
    if (currentMethodMetaData.parameterCount == 20) {
      // create variadic params list for further params
      this.mv.visitTypeInsn(NEW, "java/util/ArrayList");
      this.mv.visitInsn(DUP);
      this.mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
      int listIndex = this.getLocalIndex();
      this.mv.visitVarInsn(ASTORE, listIndex);
      this.mv.visitVarInsn(ALOAD, listIndex);
      currentMethodMetaData.variadicListIndex = listIndex;
      this.pushOperand(); // new list
    }

    // // add parameter to list
    // this.mv.visitMethodInsn(INVOKEINTERFACE, "java/utils/List", "add",
    // "(Ljava/lang/Object;)Z");
    // // pop boolean
    // this.mv.visitInsn(POP);
    // this.mv.visitVarInsn(ALOAD,
    // this.methodMetaDataStack.peek().parameterListIndex);
  }


  private void pushOperand() {
    this.pushOperand(0);
  }

  private static class MethodMetaData {
    int parameterCount = 0;
    int variadicListIndex = -1;


    public MethodMetaData(String methodName) {
      super();
    }
  }

  private final Stack<MethodMetaData> methodMetaDataStack = new Stack<MethodMetaData>();


  @Override
  public void onArray(Token<?> lookhead) {
    this.onConstant(lookhead);
  }


  @Override
  public void onArrayIndexStart(Token<?> token) {
    this.loadEnv();
  }


  @Override
  public void onArrayIndexEnd(Token<?> lookhead) {
    if (!OperationRuntime.hasRuntimeContext(this.compileEnv, OperatorType.INDEX)) {
      this.mv.visitMethodInsn(INVOKEVIRTUAL, "aviator/runtime/type/AviatorObject",
          "getElement",
          "(Ljava/utils/Map;Laviator/runtime/type/AviatorObject;)Laviator/runtime/type/AviatorObject;");
    } else {
      this.loadOpType(OperatorType.INDEX);
      this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/op/OperationRuntime",
          "eval",
          "(Laviator/runtime/type/AviatorObject;Ljava/utils/Map;Laviator/runtime/type/AviatorObject;Laviator/lexer/token/OperatorType;)Laviator/runtime/type/AviatorObject;");
      this.popOperand();
    }

    this.popOperand();
    this.popOperand();
    this.popOperand();
    this.pushOperand();
  }


  public int getLocalIndex() {
    return this.maxLocals++;
  }



  @Override
  public void onLambdaDefineStart(Token<?> lookhead) {
    if (this.lambdaGenerator == null) {
      // TODO cache?
      this.lambdaGenerator = new LambdaGenerator(instance, this, this.parser, this.classLoader);
      this.lambdaGenerator.setScopeInfo(this.parser.enterScope());
    } else {
      throw new CompileExpressionErrorException("Compile lambda error");
    }
  }

  @Override
  public void onLambdaArgument(Token<?> lookhead) {
    this.lambdaGenerator.addArgument(lookhead.getLexeme());
  }

  @Override
  public void onLambdaBodyStart(Token<?> lookhead) {
    parentCodeGenerator = this.parser.getCodeGenerator();
    this.parser.setCodeGenerator(this.lambdaGenerator);
  }

  @Override
  public void onLambdaBodyEnd(Token<?> lookhead) {
    this.lambdaGenerator.compileCallMethod();
    LambdaFunctionBootstrap bootstrap = this.lambdaGenerator.getLmabdaBootstrap();
    if (this.lambdaBootstraps == null) {
      lambdaBootstraps = new HashMap<String, LambdaFunctionBootstrap>();
    }
    this.lambdaBootstraps.put(bootstrap.getName(), bootstrap);
    genNewLambdaCode(bootstrap);
    this.parser.restoreScope(this.lambdaGenerator.getScopeInfo());
    this.lambdaGenerator = null;
    this.parser.setCodeGenerator(this.parentCodeGenerator);
  }


  public void genNewLambdaCode(LambdaFunctionBootstrap bootstrap) {
    this.mv.visitVarInsn(ALOAD, 0);
    this.loadEnv();
    this.mv.visitLdcInsn(bootstrap.getName());
    this.mv.visitMethodInsn(INVOKEVIRTUAL, this.className, "newLambda",
        "(Laviator/utils/Env;Ljava/lang/String;)Laviator/runtime/function/LambdaFunction;");
    this.pushOperand();
    this.pushOperand();
    this.popOperand();
    this.popOperand();
  }

  @Override
  public void onMethodName(Token<?> lookhead) {
    String outtterMethodName = "lambda";
    if (lookhead.getType() != TokenType.Delegate) {
      outtterMethodName = lookhead.getLexeme();
      String innerMethodName = this.innerMethodMap.get(outtterMethodName);
      if (innerMethodName != null) {
        this.loadAviatorFunction(outtterMethodName, innerMethodName);
      } else {
        this.createAviatorFunctionObject(outtterMethodName);
      }
    } else {
      this.loadEnv();
      this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/RuntimeUtils",
          "getFunction",
          "(Ljava/lang/Object;Ljava/utils/Map;)Laviator/runtime/type/AviatorFunction;");
      this.popOperand();
    }
    if ((boolean) this.instance.getOption(Options.TRACE_EVAL)) {
      this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/function/TraceFunction",
          "wrapTrace",
          "(Laviator/runtime/type/AviatorFunction;)Laviator/runtime/type/AviatorFunction;");
    }
    this.loadEnv();
    this.methodMetaDataStack.push(new MethodMetaData(outtterMethodName));
  }


  private void loadAviatorFunction(String outterMethodName, String innerMethodName) {
    Map<String, Integer> name2Index = this.labelNameIndexMap.get(this.currentLabel);
    // Is it stored in local?
    if (name2Index != null && name2Index.containsKey(innerMethodName)) {
      int localIndex = name2Index.get(innerMethodName);
      this.mv.visitVarInsn(ALOAD, localIndex);
      this.pushOperand();
    } else {
      this.mv.visitVarInsn(ALOAD, 0);
      this.mv.visitFieldInsn(GETFIELD, this.className, innerMethodName,
          "Laviator/runtime/type/AviatorFunction;");
      // Method is used more than once,store it to local for reusing
      if (this.methodTokens.get(outterMethodName) > 1) {
        this.mv.visitInsn(DUP);
        int localIndex = this.getLocalIndex();
        this.mv.visitVarInsn(ASTORE, localIndex);
        if (name2Index == null) {
          name2Index = new HashMap<String, Integer>();
          this.labelNameIndexMap.put(this.currentLabel, name2Index);
        }
        name2Index.put(innerMethodName, localIndex);
        this.pushOperand(1);
        this.popOperand();
      } else {
        this.pushOperand();
      }
    }
  }


  // private int createArugmentList() {
  // // create argument list
  // this.pushOperand(0);
  // this.pushOperand(0);
  // this.mv.visitTypeInsn(NEW, "java/utils/ArrayList");
  // this.mv.visitInsn(DUP);
  // this.popOperand();
  // this.mv.visitMethodInsn(INVOKESPECIAL, "java/utils/ArrayList", "<init>",
  // "()V");
  // // store to local variable
  // final int parameterLocalIndex = this.getLocalIndex();
  // this.mv.visitVarInsn(ASTORE, parameterLocalIndex);
  // this.mv.visitVarInsn(ALOAD, parameterLocalIndex);
  // return parameterLocalIndex;
  // }

  private void loadEnv() {
    // load env
    this.pushOperand();
    this.mv.visitVarInsn(ALOAD, 1);
  }


  private void createAviatorFunctionObject(String methodName) {
    this.loadEnv();
    this.pushOperand();
    this.mv.visitLdcInsn(methodName);
    this.mv.visitMethodInsn(INVOKESTATIC, "aviator/runtime/RuntimeUtils",
        "getFunction",
        "(Ljava/utils/Map;Ljava/lang/String;)Laviator/runtime/type/AviatorFunction;");
    this.popOperand();
    this.popOperand();
    this.pushOperand();
  }


  @Override
  public void onBitAnd(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.BIT_AND, "bitAnd");
  }


  @Override
  public void onBitOr(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.BIT_OR, "bitOr");
  }


  @Override
  public void onBitXor(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.BIT_XOR, "bitXor");
  }


  @Override
  public void onShiftLeft(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.SHIFT_LEFT, "shiftLeft");

  }


  @Override
  public void onShiftRight(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.SHIFT_RIGHT, "shiftRight");

  }


  @Override
  public void onUnsignedShiftRight(Token<?> lookhead) {
    this.visitBinOperator(OperatorType.U_SHIFT_RIGHT, "unsignedShiftRight");

  }

}
