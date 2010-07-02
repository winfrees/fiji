/* @author rich
 * Created on 09-Mar-2004
 */
package org.lsmp.djep.groupJep.groups;
import org.lsmp.djep.groupJep.interfaces.*;
import org.lsmp.djep.groupJep.values.*;
import org.nfunk.jep.type.*;

/**
 * An Algebraic Extension of a Ring.
 * The ring generated by {1,t,...,t^n-1} where t is an algebraic number
 * i.e t is be a root of a monic polynomial equation.
 * 
 * @see AlgebraicExtensionElement
 * @author Rich Morris
 * Created on 09-Mar-2004
 */
public class AlgebraicExtension extends ExtendedFreeGroup implements RingI {

	private Polynomial poly;
	private Polynomial poly2;

	/**
	 * Create the ring K(t) where t is a solution of the monic polynomial p.
	 * 
	 * @param K the Ring this is an extension of.
	 * @param poly A monic polynomial whose solution gives an algebraic number which is used to generate this group.
	 * @throws IllegalArgumentException if the base ring of the poly is not the same.
	 * @throws IllegalArgumentException if the polynomial is not monic.
	 */
	public AlgebraicExtension(RingI K, Polynomial poly) {
		super(K,poly.getSymbol());
		this.poly = poly;
		if(baseRing != poly.getBaseRing())
			throw new IllegalArgumentException("The polynomial should be specified over the same base ring");
		// test for monic
		if(!baseRing.equals(
			poly.getCoeffs()[poly.getDegree()],
			baseRing.getONE()))
			throw new IllegalArgumentException("poly "+poly.toString()+" should be monic");
		
		// construct q = t^n - poly (deg n-1)	
		Number coeffs[] = new Number[poly.getDegree()];
		for(int i=0;i<poly.getDegree();++i)
			coeffs[i]= baseRing.getInverse(poly.getCoeffs()[i]);
		poly2 = new Polynomial(baseRing,poly.getSymbol(),coeffs);
		
		if(poly.getDegree()==2)
		{
			double b = poly.getCoeffs()[1].doubleValue();
			double c = poly.getCoeffs()[0].doubleValue();
			double det = b*b-4*c;
			if(det<0)
				rootVal = new Complex(-b/2,Math.sqrt(-det)/2);
			else
				rootVal = new Complex(-b/2+Math.sqrt(det)/2);
		}
		else
		{
			boolean flag = true;
			for(int i=1;i<poly.getDegree();++i)
				if(!baseRing.equals(poly.getCoeffs()[i],baseRing.getZERO()))
				{	flag = false; break;	}
			if(flag)
			{
				double a0 = poly.getCoeffs()[0].doubleValue();
				Complex z = new Complex(-a0);
				rootVal = z.power(1.0/poly.getDegree());
			}
		}

		// construct the zero poly
		zeroPoly = new AlgebraicExtensionElement(this,new Number[]{
					baseRing.getZERO()});
		// construct the unit poly
		unitPoly = new AlgebraicExtensionElement(this,new Number[]{
					baseRing.getONE()});
		// construct the polynomial t
		tPoly = new AlgebraicExtensionElement(this,new Number[]{
					baseRing.getZERO(),
					baseRing.getONE()});
	}
	
	public Number valueOf(Number coeffs[])	{
		return new AlgebraicExtensionElement(this, coeffs);
	}
	
	public String toString()
	{
		return baseRing.toString() + '[' + poly.toString() + ']';
	}
	
	/** Returns the polynomial defining the algebraic number. */
	public Polynomial getPoly() {
		return poly;
	}

	/** Returns the polynomial -a_(n-1) t^(n-1) + ... + a_0.
	 * This polynomial is used in reducing the equation t^n
	 */
	public Polynomial getSubsPoly() {
		return poly2;
	}
}
