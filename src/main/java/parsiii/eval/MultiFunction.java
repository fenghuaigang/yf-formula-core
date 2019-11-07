/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package parsiii.eval;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a binary function.
 * <p>
 * A binary function has two arguments which are always evaluated in order to
 * compute the final result.
 */
public abstract class MultiFunction implements Function {
	@Override
	public int getNumberOfArguments() {
		return -1;
	}

	@Override
	public double eval(List<Expression> args) {
		return eval2(args).doubleValue();
	}

	private BigDecimal[] convertBigDecimals(List<Expression> args) {
		BigDecimal[] doubles = new BigDecimal[args.size()];
		for (int i = 0; i < args.size(); i++) {
			doubles[i] = args.get(i).evaluate2();
		}
		return doubles;
	}

	@Override
	public BigDecimal eval2(List<Expression> args) {
		return eval(convertBigDecimals(args));
	}

	protected abstract BigDecimal eval(BigDecimal[] params);

	@Override
	public boolean isNaturalFunction() {
		return true;
	}
}
