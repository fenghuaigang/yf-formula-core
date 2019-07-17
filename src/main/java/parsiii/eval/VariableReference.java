/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package parsiii.eval;

import java.math.BigDecimal;

/**
 * Represents a reference to a variable.
 */
public class VariableReference implements parsiii.eval.Expression {

    private parsiii.eval.Variable var;

    /**
     * Creates a new reference to the given variable.
     *
     * @param var the variable to access when this expression is evaluated
     */
    public VariableReference(Variable var) {
        this.var = var;
    }

    @Override
    public double evaluate() {
        return var.getValue().doubleValue();
    }

    @Override
    public String toString() {
        return var.getName();
    }

    @Override
    public boolean isConstant() {
        return var.isConstant();
    }
    
    @Override
    public boolean isCustomFormula(){
    	return var.isCustomFormula();
    }
    
    @Override
    public Expression simplify() {
    	if(isCustomFormula()){
    		return new Constant(evaluate3());
    	}
        if (isConstant()) {
            return new Constant(evaluate2());
        }
        return this;
    }

	@Override
	public BigDecimal evaluate2() {
		return var.getValue();
	}
	
	@Override
	public String evaluate3() {
		return var.getValStr();
	}
}
