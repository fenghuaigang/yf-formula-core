package aviator.parser;

import aviator.code.CodeGenerator;


public interface Parser {

  CodeGenerator getCodeGenerator();

  void setCodeGenerator(CodeGenerator codeGenerator);

  ScopeInfo enterScope();

  void restoreScope(ScopeInfo info);

}
