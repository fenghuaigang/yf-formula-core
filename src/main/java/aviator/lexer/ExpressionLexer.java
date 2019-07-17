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
package aviator.lexer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Stack;
import aviator.AviatorEvaluatorInstance;
import aviator.Options;
import aviator.exception.CompileExpressionErrorException;
import aviator.exception.ExpressionSyntaxErrorException;
import aviator.lexer.token.CharToken;
import aviator.lexer.token.NumberToken;
import aviator.lexer.token.StringToken;
import aviator.lexer.token.Token;
import aviator.lexer.token.Variable;


/**
 * Expression Lexer,scan tokens from string
 *
 * @author dennis
 *
 */
public class ExpressionLexer {
  // current char
  private char peek;
  // Char iterator for string
  private final CharacterIterator iterator;
  // symbol table
  private final SymbolTable symbolTable;
  // Tokens buffer
  private final Stack<Token<?>> tokenBuffer = new Stack<Token<?>>();
  private AviatorEvaluatorInstance instance;
  private String expression;
  private MathContext mathContext;
  private boolean parseFloatIntoDecimal;

  public ExpressionLexer(AviatorEvaluatorInstance instance, String expression) {
    this.iterator = new StringCharacterIterator(expression);
    this.expression = expression;
    this.symbolTable = new SymbolTable();
    this.peek = this.iterator.current();
    this.instance = instance;
    this.mathContext = this.instance.getOption(Options.MATH_CONTEXT);
    this.parseFloatIntoDecimal =
        this.instance.getOption(Options.ALWAYS_PARSE_FLOATING_POINT_NUMBER_INTO_DECIMAL);
  }

  /**
   * Push back token
   *
   * @param token
   */
  public void pushback(Token<?> token) {
    this.tokenBuffer.push(token);
  }


  public Token<?> scan() {
    return this.scan(true);
  }


  public void nextChar() {
    this.peek = this.iterator.next();
  }


  public void prevChar() {
    this.peek = this.iterator.previous();
  }

  static final char[] VALID_HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'a',
      'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f'};


  public boolean isValidHexChar(char ch) {
    for (char c : VALID_HEX_CHAR) {
      if (c == ch) {
        return true;
      }
    }
    return false;
  }


  public int getCurrentIndex() {
    return this.iterator.getIndex();
  }



  public Token<?> scan(boolean analyse) {
    // If buffer is not empty,return
    if (!this.tokenBuffer.isEmpty()) {
      return this.tokenBuffer.pop();
    }
    // Skip white space or line
    for (;; this.nextChar()) {
      if (this.peek == CharacterIterator.DONE) {
        return null;
      }

      if (analyse) {
        if (this.peek == ' ' || this.peek == '\t' || this.peek == '\r') {
          continue;
        }
        if (this.peek == '\n') {
          throw new CompileExpressionErrorException(
              "Aviator doesn't support multi-lines expression at " + this.iterator.getIndex());
        } else {
          break;
        }
      } else {
        char ch = this.peek;
        int index = this.iterator.getIndex();
        this.nextChar();
        return new CharToken(ch, index);
      }
    }

    // if it is a hex digit
    if (Character.isDigit(this.peek) && this.peek == '0') {
      this.nextChar();
      if (this.peek == 'x' || this.peek == 'X') {
        this.nextChar();
        StringBuffer sb = new StringBuffer();
        int startIndex = this.iterator.getIndex() - 2;
        long value = 0L;
        do {
          sb.append(this.peek);
          value = 16 * value + Character.digit(this.peek, 16);
          this.nextChar();
        } while (this.isValidHexChar(this.peek));
        return new NumberToken(value, sb.toString(), startIndex);
      } else {
        this.prevChar();
      }
    }

    // If it is a digit
    if (Character.isDigit(this.peek) || this.peek == '.') {
      StringBuffer sb = new StringBuffer();
      int startIndex = this.iterator.getIndex();
      long lval = 0L;
      double dval = 0d;
      boolean hasDot = false;
      double d = 10.0;
      boolean isBigInt = false;
      boolean isBigDecimal = false;
      boolean scientificNotation = false;
      boolean negExp = false;
      do {
        sb.append(this.peek);
        if (this.peek == '.') {
          if (scientificNotation) {
            throw new CompileExpressionErrorException(
                "Illegal number " + sb + " at " + this.iterator.getIndex());
          }
          if (hasDot) {
            throw new CompileExpressionErrorException(
                "Illegal Number " + sb + " at " + this.iterator.getIndex());
          } else {
            hasDot = true;
            this.nextChar();
          }

        } else if (this.peek == 'N') {
          // big integer
          if (hasDot) {
            throw new CompileExpressionErrorException(
                "Illegal number " + sb + " at " + this.iterator.getIndex());
          }
          isBigInt = true;
          this.nextChar();
          break;
        } else if (this.peek == 'M') {
          isBigDecimal = true;
          this.nextChar();
          break;
        } else if (this.peek == 'e' || this.peek == 'E') {
          if (scientificNotation) {
            throw new CompileExpressionErrorException(
                "Illegal number " + sb + " at " + this.iterator.getIndex());
          }
          scientificNotation = true;
          this.nextChar();
          if (this.peek == '-') {
            negExp = true;
            sb.append(this.peek);
            this.nextChar();
          }
        } else {
          int digit = Character.digit(this.peek, 10);
          if (scientificNotation) {
            int n = digit;
            this.nextChar();
            while (Character.isDigit(this.peek)) {
              n = 10 * n + Character.digit(this.peek, 10);
              this.nextChar();
            }
            while (n-- > 0) {
              if (negExp) {
                dval = dval / 10;
              } else {
                dval = 10 * dval;
              }
            }
            hasDot = true;
          } else if (hasDot) {
            dval = dval + digit / d;
            d = d * 10;
            this.nextChar();
          } else {
            lval = 10 * lval + digit;
            dval = 10 * dval + digit;
            this.nextChar();
          }

        }
      } while (Character.isDigit(this.peek) || this.peek == '.' || this.peek == 'E'
          || this.peek == 'e' || this.peek == 'M' || this.peek == 'N');
      Number value;
      if (isBigDecimal) {
        value = new BigDecimal(this.getBigNumberLexeme(sb), this.mathContext);
      } else if (isBigInt) {
        value = new BigInteger(this.getBigNumberLexeme(sb));
      } else if (hasDot) {
        if (this.parseFloatIntoDecimal && sb.length() > 1) {
          value = new BigDecimal(sb.toString(), this.mathContext);
        } else if (sb.length() == 1) {
          // only have a dot character.
          return new CharToken('.', startIndex);
        } else {
          value = dval;
        }
      } else {
        // if the long value is out of range,then it must be negative,so
        // we make it as a big integer.
        if (lval < 0) {
          value = new BigInteger(sb.toString());
        } else {
          value = lval;
        }
      }
      String lexeme = sb.toString();
      if (isBigDecimal || isBigInt) {
        lexeme = lexeme.substring(0, lexeme.length() - 1);
      }
      return new NumberToken(value, lexeme, startIndex);
    }

    // It is a variable
    if (this.peek == '#') {
      int startIndex = this.iterator.getIndex();
      this.nextChar(); // skip $
      StringBuilder sb = new StringBuilder();
      while (Character.isJavaIdentifierPart(this.peek) || this.peek == '.' || this.peek == '['
          || this.peek == ']') {
        sb.append(this.peek);
        this.nextChar();
      }
      String lexeme = sb.toString();
      if (lexeme.isEmpty()) {
        throw new ExpressionSyntaxErrorException("Blank variable name after '#'");
      }
      Variable variable = new Variable(lexeme, startIndex);
      variable.setQuote(true);
      return this.reserverVar(lexeme, variable);
    }
    if (Character.isJavaIdentifierStart(this.peek)) {
      int startIndex = this.iterator.getIndex();
      StringBuilder sb = new StringBuilder();
      do {
        sb.append(this.peek);
        this.nextChar();
      } while (Character.isJavaIdentifierPart(this.peek) || this.peek == '.');
      String lexeme = sb.toString();
      Variable variable = new Variable(lexeme, startIndex);
      return this.reserverVar(lexeme, variable);
    }

    if (isBinaryOP(this.peek)) {
      CharToken opToken = new CharToken(this.peek, this.iterator.getIndex());
      this.nextChar();
      return opToken;
    }
    // String
    if (this.peek == '"' || this.peek == '\'') {
      char left = this.peek;
      int startIndex = this.iterator.getIndex();
      StringBuilder sb = new StringBuilder();
      // char prev = this.peek;
      while ((this.peek = this.iterator.next()) != left) {
        if (this.peek == '\\') // escape
        {
          this.nextChar();
          if (this.peek == CharacterIterator.DONE) {
            throw new CompileExpressionErrorException(
                "EOF while reading string at index: " + this.iterator.getIndex());
          }
          if (this.peek == left) {
            sb.append(this.peek);
            continue;
          }
          switch (this.peek) {
            case 't':
              this.peek = '\t';
              break;
            case 'r':
              this.peek = '\r';
              break;
            case 'n':
              this.peek = '\n';
              break;
            case '\\':
              break;
            case 'b':
              this.peek = '\b';
              break;
            case 'f':
              this.peek = '\f';
              break;
            default: {
              throw new CompileExpressionErrorException(
                  "Unsupported escape character: \\" + this.peek);
            }

          }
        }

        if (this.peek == CharacterIterator.DONE) {
          throw new CompileExpressionErrorException(
              "EOF while reading string at index: " + this.iterator.getIndex());
        }

        sb.append(this.peek);
      }
      this.nextChar();
      return new StringToken(sb.toString(), startIndex);
    }

    Token<Character> token = new CharToken(this.peek, this.iterator.getIndex());
    this.nextChar();
    return token;
  }

  public String getScanString() {
    return this.expression.substring(0, this.iterator.getIndex());
  }

  private Token<?> reserverVar(String lexeme, Variable variable) {
    // If it is a reserved word(true/false/nil/lambda)
    if (this.symbolTable.contains(lexeme)) {
      return this.symbolTable.getVariable(lexeme);
    } else {
      this.symbolTable.reserve(lexeme, variable);
      return variable;
    }
  }


  private String getBigNumberLexeme(StringBuffer sb) {
    String lexeme = sb.toString();
    lexeme = lexeme.substring(0, lexeme.length() - 1);
    return lexeme;
  }

  static final char[] OPS = {'=', '>', '<', '+', '-', '*', '/', '%', '!', '&', '|'};


  public static boolean isBinaryOP(char ch) {
    for (char tmp : OPS) {
      if (tmp == ch) {
        return true;
      }
    }
    return false;
  }

}
