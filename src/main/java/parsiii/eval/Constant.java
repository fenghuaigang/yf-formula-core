/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package parsiii.eval;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

/**
 * Represents a constant numeric expression.
 */
public class Constant implements Expression {

//    private double value;
 
	private BigDecimal value;

	private String valStr;
	
	private double nan;
    /**
     * Used as dummy expression by the parser if an error occurs while parsing.
     */
    public static final Constant EMPTY = new Constant(Double.NaN);

    public Constant(double value) {
        this.nan = value;
    }
    
    public Constant(BigDecimal value) {
        this.value = value;
    }
   
    public Constant(String value){
    	this.valStr = value;
    }
//    public Constant(double value) {
//    	this.value = new BigDecimal(value);
//    }

	@Override
    public double evaluate() {
        return value.doubleValue();
    }
	@Override
	public BigDecimal evaluate2() {
		return value;
	}
	
    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
    	if(isCustomFormula()){
    		return valStr;
    	}
        return String.valueOf(value);
    }

    @Override
	public boolean isCustomFormula() {
		return StringUtils.isNotBlank(valStr);
	}

	@Override
	public String evaluate3() {
		return toString();
	}

}
