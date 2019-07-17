package parsiii.eval;

import java.math.BigDecimal;

/**
 * 精度保留 round(12.4) -> 12 round(12.5) -> 13 round(123.11111,3) -> 123.111
 * round(123.1245,3) -> 123.125
 *
 * @Auther: feng
 * @Date: 2018/10/16
 * @Description:
 */
public class RoundFormulaFunction extends MultiFunction {

	@Override
	protected BigDecimal eval(BigDecimal[] params) {
		BigDecimal decimal = new BigDecimal(params[0] + "");
		return decimal.setScale(params.length == 1 ? 0 : (params[1].intValue()), BigDecimal.ROUND_HALF_UP);
	}
	
}
