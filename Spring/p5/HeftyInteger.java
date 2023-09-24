// import java.util.Random;

// // NOTE: BigInteger is used only for convenience in printing and converting byte[]'s
// import java.math.BigInteger;

public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};

	private byte[] val;

	/**
	 * Construct the HeftyInteger from a given byte array
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b) {
		val = b;
	}

	/**
	 * Return this HeftyInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other HeftyInteger to sum with this
	 */
	public HeftyInteger add(HeftyInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public HeftyInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other HeftyInteger to subtract from this
	 * @return difference of this and other
	 */
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger multiply(HeftyInteger other) {
		//System.out.print("start ");
		// YOUR CODE HERE (replace the return, too...)
		HeftyInteger answer = null;
		byte[] a, b;
		if (this.isNegative()){ // this is negative
			HeftyInteger A = this.negate();

			if (other.isNegative()){ // other is negative
				HeftyInteger B = other.negate();

				// POSITIVE ANSWER
				// If operands are of different sizes, put larger first ...
				// if (val.length < other.length()) {
				// 	a = B.getVal();
				// 	b = A.getVal();
				// }
				// else {
					a = A.getVal();
					b = B.getVal();
				//

				answer = new HeftyInteger(karatsuba(a, b));

			} else {
				// NEGATE ANSWER
				// If operands are of different sizes, put larger first ...
				// if (val.length < other.length()) {
				// 	a = other.getVal();
				// 	b = A.getVal();
				// }
				// else {
					a = A.getVal();
					b = other.getVal();
				//}

				answer = new HeftyInteger(karatsuba(a, b));
				answer = answer.negate(); // negated answer!
			}
		} else { // this is positive
			if (other.isNegative()){ // other is negative
				HeftyInteger B = other.negate();

				// // If operands are of different sizes, put larger first ...
				// if (val.length < other.length()) {
				// 	a = B.getVal();
				// 	b = this.getVal();
				// }
				// else {
					a = this.getVal();
					b = B.getVal();
				//}

				// NEGATE ANSWER
				answer = new HeftyInteger(karatsuba(a, b));
				answer = answer.negate();

			} else { // other is positive

				// POSITIVE ANSWER
				// If operands are of different sizes, put larger first ...
				// if (val.length < other.length()) {
				// 	a = other.getVal();
				// 	b = this.getVal();
				// }
				// else {
					a = this.getVal();
					b = other.getVal();
				// }

				answer = new HeftyInteger(karatsuba(a, b));
			}
		}
		//System.out.print("end\n\n");
		return answer;
	}

	
	// In this method, I want to assume EVERYTHING IS POSITIVE because I can't figure that shit out otherwise
	public byte[] karatsuba(byte[] a, byte[] b){

		if (a[0] < 0 && a.length > 3){
			// need to add leading 0s here
			// System.out.println("Resizing a since it's negative at top");
			byte[] t = new byte[a.length+1];
			for (int i = 0; i < a.length; i++){
				t[i+1] = a[i];
			}
			a = t;
		}

		if (b[0] < 0 && b.length > 3){
			// need to add leading 0s here
			byte[] t = new byte[b.length+1];
			for (int i = 0; i < b.length; i++){
				t[i+1] = b[i];
			}
			b = t;
		}

		if (a.length > b.length){
			// System.out.println("resizing b");
			int in = a.length - b.length; // knows how far we offset
			byte[] t = new byte[a.length];
			for (int i = 0; i < b.length; i++){
				t[in+i] = b[i];
			}
			b = t;
		} else if (a.length<b.length){
			// System.out.println("resizing a");
			int in = b.length - a.length; // knows how far we offset
			byte[] t = new byte[b.length];
			for (int i = 0; i < a.length; i++){
				t[in+i] = a[i];
			}
			a = t;
		}

		// should have the same size matrices at this point!
		// System.out.println("a has length of "+a.length+" and b has length of "+b.length);



		// if (a.length < b.length){
		// 	byte[] t = a;
		// 	a = b;
		// 	b = t;
		// }
		// BASE CASE:
		if (a.length <= 4){ // since a is the larger val, if we're less than or equal to 4 byte, we'll fit result in a long! with positives! yay!
			long along = 0;
			long blong = 0;
			for (int i = 0; i < a.length; i++){
				along = along<<8;
				along = along | ((char)a[i]&0xFF); // only takes the last 8 bits of the casted long
				// System.out.println("aLong is shifted by 8 (*128) and = "+along);
			}
			// System.out.println("ALONG IS "+along);
			for (int i = 0; i < b.length; i++){
				blong = blong<<8;
				blong = blong | ((char)b[i]&0xFF); // only takes the last 8 bits of the casted long
				// System.out.println("bLong is shifted by 8 (*128) and = "+blong);
			}
			// System.out.println("BLONG IS "+blong);
			long result = along*blong; // answer is now in long form in RESULT
			// System.out.println("RESULT IS "+result);
			byte[] answer = new byte[8];
			answer[0] = (byte)(((result>>>56)&0xFF));
			answer[1] = (byte)(((result>>>48)&0xFF));
			answer[2] = (byte)(((result>>>40)&0xFF));
			answer[3] = (byte)(((result>>>32)&0xFF));
			answer[4] = (byte)((result&0xFF000000)>>>24);
			answer[5] = (byte)((result&0x00FF0000)>>>16);
			answer[6] = (byte)((result&0x0000FF00)>>>8);
			answer[7] = (byte)(result&0x000000FF);

			// String s1 = String.format("%8s", Integer.toBinaryString(answer[0] & 0xFF)).replace(' ', '0');
			// String s2 = String.format("%8s", Integer.toBinaryString(answer[1] & 0xFF)).replace(' ', '0');
			// String s3 = String.format("%8s", Integer.toBinaryString(answer[2] & 0xFF)).replace(' ', '0');
			// String s4 = String.format("%8s", Integer.toBinaryString(answer[3] & 0xFF)).replace(' ', '0');
			// String s5 = String.format("%8s", Integer.toBinaryString(answer[4] & 0xFF)).replace(' ', '0');
			// String s6 = String.format("%8s", Integer.toBinaryString(answer[5] & 0xFF)).replace(' ', '0');
			// String s7 = String.format("%8s", Integer.toBinaryString(answer[6] & 0xFF)).replace(' ', '0');
			// String s8 = String.format("%8s", Integer.toBinaryString(answer[7] & 0xFF)).replace(' ', '0');
			// System.out.println( "answer[0] = "+s1+"\n"+
			// 					"answer[1] = "+s2+"\n"+
			// 					"answer[2] = "+s3+"\n"+
			// 					"answer[3] = "+s4+"\n"+
			// 					"answer[4] = "+s5+"\n"+
			// 					"answer[5] = "+s6+"\n"+
			// 					"answer[6] = "+s7+"\n"+
			// 					"answer[7] = "+s8+"\n");

			if (answer[0] < 0){
				//System.out.println("FUCK I NEED TO RESIZE");
				byte[] te = new byte[9];
				for (int i = 0; i < 8; i++){
					te[i+1] = answer[i];
				}
				answer = te;
			}

			return answer; // will be a 64-bit or less value! so we're chillin'! :-)
		} else {

			// otherwise, keep doing Karatsuba's!
			byte[] ah, al, bh, bl; // since everything is assumed positive, I can take them AS IS
			// get a into a high and a low
			if (a.length % 2 == 0){ // even number of bytes
				ah = new byte[a.length/2]; // new byte array of half the size
				al = new byte[a.length/2]; // new byte array of half the size
				for (int i = 0; i < a.length / 2; i++){
					ah[i] = a[i]; // copy over a to ah
				}
				for (int i = a.length/2; i < a.length; i++){
					al[i-a.length/2] = a[i]; // copy over a to al
				}
			} else { // odd num of bytes
				byte[] resize = new byte[a.length+1]; // adds leading 0s to the byte array to make it even
				for (int i = 0; i < a.length; i++){
					resize[i+1] = a[i]; // copies over a array into resize
				}
				a = resize;
				resize = null;
				ah = new byte[a.length/2]; // new byte array of half the size
				al = new byte[a.length/2]; // new byte array of half the size
				for (int i = 0; i < a.length / 2; i++){
					ah[i] = a[i]; // copy over a to ah
				}
				for (int i = a.length/2; i < a.length; i++){
					al[i-a.length/2] = a[i]; // copy over a to al
				}
			}

			// get b into b high and b low
			if (b.length % 2 == 0){ // even number of bytes
				bh = new byte[b.length/2]; // new byte array of half the size
				bl = new byte[b.length/2]; // new byte array of half the size
				for (int i = 0; i < b.length / 2; i++){
					bh[i] = b[i]; // copy over b to bh
				}
				for (int i = b.length/2; i < b.length; i++){
					bl[i-b.length/2] = b[i]; // copy over b to bl
				}
			} else { // odd num of bytes
				byte[] resize = new byte[b.length+1]; // adds leading 0s to the byte array to make it even
				for (int i = 0; i < b.length; i++){
					resize[i+1] = b[i]; // copies over a array into resize
				}
				b = resize;
				resize = null;
				bh = new byte[b.length/2]; // new byte array of half the size
				bl = new byte[b.length/2]; // new byte array of half the size
				for (int i = 0; i < b.length / 2; i++){
					bh[i] = b[i]; // copy over b to bh
				}
				for (int i = b.length/2; i < b.length; i++){
					bl[i-b.length/2] = b[i]; // copy over b to bl
				}
			}

			// System.out.println("--------------- Recursing at M1 --------------");
			byte[] m1 = karatsuba(ah, bh); // get the val, just need to shift now
			// System.out.println("--------------- returned from m1 -------------");
			byte[] m4;
			// System.out.println("~~~~~~~~~~~~~~~ Recursing at M4 ~~~~~~~~~~~~~~");
			if (al.length >= bl.length){
				m4 = karatsuba(al, bl); // length is good as is!
			} else {
				m4 = karatsuba(bl, al); // length is good as is!
			}
			// System.out.println("~~~~~~~~~~~~~~~ returned from m4 ~~~~~~~~~~~~~~");
			HeftyInteger M1 = new HeftyInteger(m1);
			HeftyInteger M4 = new HeftyInteger(m4);

			byte[] temp;
			// here, there's a possibility that the beginning of al and bl are negative... which is no bueno
			if (al[0] < 0){ // means we have a leading 1, which could be assumed negative
				temp = new byte[al.length+1];
				temp[0] = '\0'; // sets bits of tem[0] to all zeros
				for (int i = 0; i < al.length; i++){
					temp[i+1] = al[i]; // copies over off by one
				}
				al = temp;
			}

			if (ah[0] < 0){ // means we have a leading 1, which could be assumed negative
				temp = new byte[ah.length+1];
				temp[0] = '\0'; // sets bits of tem[0] to all zeros
				for (int i = 0; i < ah.length; i++){
					temp[i+1] = ah[i]; // copies over off by one
				}
				ah = temp;
			}

			if (bl[0] < 0){ // means we have a leading 1, which could be assumed negative
				temp = new byte[bl.length+1];
				temp[0] = '\0'; // sets bits of tem[0] to all zeros
				for (int i = 0; i < bl.length; i++){
					temp[i+1] = bl[i]; // copies over off by one
				}
				bl = temp;
			}

			if (bh[0] < 0){ // means we have a leading 1, which could be assumed negative
				temp = new byte[bh.length+1];
				temp[0] = '\0'; // sets bits of tem[0] to all zeros
				for (int i = 0; i < bh.length; i++){
					temp[i+1] = bh[i]; // copies over off by one
				}
				bh = temp;
			}

			HeftyInteger AH = new HeftyInteger(ah);
			HeftyInteger BH = new HeftyInteger(bh);
			HeftyInteger AL = new HeftyInteger(al);
			HeftyInteger BL = new HeftyInteger(bl);

			// System.out.println("============== Recursing at M5 ================");
			HeftyInteger M5 = new HeftyInteger(AH.add(AL).getVal());
			M5 = new HeftyInteger(BH.add(BL).getVal());
			M5 = new HeftyInteger(  karatsuba(     AH.add(AL).getVal(), BH.add(BL).getVal()      )  ).subtract(M1).subtract(M4);
			// System.out.println("============== returned from m5 ================");
			byte[] m5 = M5.getVal(); // now have m5 in byte[] form for re-alignment
			if (m5[0] < 0){
				temp = new byte[m5.length+1];
				for (int i = 0; i < m5.length; i++){
					temp[i+1] = m5[i];
				}
				m5 = temp;
			}
			M5 = new HeftyInteger(m5);
			temp = new byte[a.length+m1.length];
			for (int i = 0; i < m1.length; i++){
				temp[i] = m1[i];
			}
			m1 = temp; // copy back temp to m1

			if (m1[0] < 0){
				temp = new byte[m1.length+1];
				for (int i = 0; i < m1.length; i++){
					temp[i+1] = m1[i];
				}
			}

			if (m4[0] < 0){
				temp = new byte[m4.length+1];
				for (int i = 0; i < m4.length; i++){
					temp[i+1] = m4[i];
				}
				m4 = temp;
			}

			if (m5[0] < 0){
				temp = new byte[m5.length+1];
				for (int i = 0; i < m5.length; i++){
					temp[i+1] = m5[i];
				}
				m5 = temp;
			}


			// NEED TO REVISIT THE M5 REALIGNMENT

			temp = new byte[a.length/2+m5.length];
			for (int i = 0; i < m5.length; i++){
				temp[i] = m5[i];
			}
			m5 = temp;
			
			M1 = new HeftyInteger(m1);
			M4 = new HeftyInteger(m4);
			M5 = new HeftyInteger(m5);

			// System.out.println("\tM1 is  "+new BigInteger(M1.getVal()).toString());
			// System.out.println("\tM4 is  "+new BigInteger(M4.getVal()).toString());
			// System.out.println("\tM5 is   "+new BigInteger(M5.getVal()).toString());			
			
			return M1.add(M5).add(M4).getVal();
		}

		
	}






	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public HeftyInteger[] XGCD(HeftyInteger other) {
		 HeftyInteger[] results = new HeftyInteger[3];
		 // can check up here if GCD is negative, and if so switch ints
		 if (this.isNegative()){ // this is negative
			HeftyInteger A = this.negate();

			if (other.isNegative()){ // other is negative
				// - -, flip res[1] and res[2]
				HeftyInteger B = other.negate();
				results = xgcd(A, B);
				results[1] = results[1].negate();
				results[2] = results[2].negate();
				return results;
			} else {
				// - +, flip res[1]
				results = xgcd(A, other);
				results[1] = results[1].negate();
				return results;
			}
		} else { // this is positive
			if (other.isNegative()){ // other is negative
				// + -, flip res[2]
				HeftyInteger B = other.negate();
				results = xgcd(this, B);
				results[2] = results[2].negate();
				return results;
			} else { // other is positive
				// + +, good as is
				return xgcd(this, other);
			}
		}
	 }

	 private HeftyInteger[] xgcd(HeftyInteger a, HeftyInteger b){
		HeftyInteger[] result = new HeftyInteger[3];

		// BASE CASE:
		if (!b.isNegative() && !(b.negate().isNegative())){ // if both are true, b is ZERO
			// for this one, since b is 0, y must be 0 as well
			// additionally, the gcd between a and a has a factor of 1, so this is the easiest case!
			result[0] = a; // in this case, the GCD IS a!
			result[1] = new HeftyInteger(ONE); // assigns 1 to the X spot in result[]
			result[2] = new HeftyInteger(new byte[1]); // assigns 0 to the Y spot in result[]
			// System.out.println("We hit zero!");
			return result;
		}
		//System.out.print(" | ");
		result = xgcd(b, a.mod(b));
		HeftyInteger d = result[0];
		HeftyInteger x = result[2]; // As per slides, x = y previous
		//System.out.print("*/");

		// TRY TO ENSURE THAT THE SIZES ARE ONLY AS BIG AS WE NEED!!!!!
		int size = result[2].length();
		boolean flag = false;
		byte[] mid;
		for (int i = 0; i < result[2].length(); i++){
			// check from index 0 up to whenever it is NON-ZERO
			// then, if less than 1, need to decrement i
			// otherwise, i is good to remove
			//copy back in :-)
			if (result[2].getVal()[i] != 0){
				if (result[2].getVal()[i] < 0){
					if (i == 0) break;
					size = i-1;
					flag = true;
					break;
				} else {
					size = i;
					flag = true;
					break;
				}
			}
		}
		if (flag){
			byte[] temp = new byte[result[2].length()-size];
			mid = result[2].getVal();
			for (int i = size; i < result[2].length(); i++){
				// starting at Mid[size], copy into NEW byte[] starting at 0
				temp[i-size] = mid[i];
			}
			mid = temp;
			result[2] = new HeftyInteger(mid);
		}
		size = result[1].length();
		flag = false;
		for (int i = 0; i < result[1].length(); i++){
			// check from index 0 up to whenever it is NON-ZERO
			// then, if less than 1, need to decrement i
			// otherwise, i is good to remove
			//copy back in :-)
			if (result[1].getVal()[i] != 0){
				if (result[1].getVal()[i] < 0){
					if (i == 0) break;
					size = i-1;
					flag = true;
					break;
				} else {
					size = i;
					flag = true;
					break;
				}
			}
		}
		if (flag){
			byte[] temp = new byte[result[1].length()-size];
			mid = result[1].getVal();
			for (int i = size; i < result[1].length(); i++){
				// starting at Mid[size], copy into NEW byte[] starting at 0
				temp[i-size] = mid[i];
			}
			mid = temp;
			result[1] = new HeftyInteger(mid);
		}
		size = result[0].length();
		flag = false;
		for (int i = 0; i < result[0].length(); i++){
			// check from index 0 up to whenever it is NON-ZERO
			// then, if less than 1, need to decrement i
			// otherwise, i is good to remove
			//copy back in :-)
			if (result[0].getVal()[i] != 0){
				if (result[0].getVal()[i] < 0){
					if (i == 0) break;
					size = i-1;
					flag = true;
					break;
				} else {
					size = i;
					flag = true;
					break;
				}
			}
		}
		if (flag){
			byte[] temp = new byte[result[0].length()-size];
			mid = result[0].getVal();
			for (int i = size; i < result[0].length(); i++){
				// starting at Mid[size], copy into NEW byte[] starting at 0
				temp[i-size] = mid[i];
			}
			mid = temp;
			result[0] = new HeftyInteger(mid);
		}
		//finish resizing to speed up a bit, so many 0s


		HeftyInteger y = result[1].subtract((a.divide(b)).multiply(result[2])); // USE PARENTHESIS TO ENSURE FORMULA IS CORRECT
		result[0] = d;
		result[1] = x;
		result[2] = y;
		return result;

		// System.out.println("trying more gcd calls");
		// return gcd(b, a.mod(b));
	 }

	 public HeftyInteger divide(HeftyInteger other){

		if (this.isNegative()){
			if (other.isNegative()){ // - - 
				// return r
				HeftyInteger tThis = this.negate();
				HeftyInteger tOther = other.negate();
				HeftyInteger r = divide(tThis, tOther);
				return r;

			} else { // - + 
				// return -r
				HeftyInteger tThis = this.negate();
				HeftyInteger r = divide(tThis, other);
				return r.negate();

			}
		} else { // THIS IS POSITIVE
			// return -r
			if (other.isNegative()){ // + -
				// return b+r
				HeftyInteger tOther = other.negate();
				HeftyInteger r = divide(this, tOther);
				return r.negate();
			} else { // + + 
				// return r

				return divide(this, other);

			}
		}
	 }

	 /**
	  * This will probably be useful for the XGCD function since I can't just use the mod operation...
	  */
	 private HeftyInteger divide(HeftyInteger a, HeftyInteger b){
		//System.out.print("start ");
		byte[] low = new byte[1];
		HeftyInteger Low = new HeftyInteger(low);
		byte[] high = a.getVal();
		HeftyInteger High = new HeftyInteger(high);
		if (a.subtract(b).isNegative()) return new HeftyInteger(new byte[1]);
		HeftyInteger Mid = new HeftyInteger(b.getVal());
		while (!((High.subtract(Low)).isNegative())){ // if hi - low is NOT negate
			// System.out.print("BEFORE: Mid is "+new BigInteger(Mid.getVal()).toString());
			//System.out.print("|");
			//System.out.print("add");
			Mid = High.add(Low);
			//System.out.print("ing\n");
			byte[] mid = Mid.getVal();
			// System.out.print("\nmid legnth is "+Mid.length());
			// System.out.print("\thi legnth is "+High.length());
			// System.out.print("\tlo legnth is "+Low.length());
			// System.out.print("\ta legnth is "+a.length());
			// System.out.print("\tb legnth is "+b.length());
			//System.out.print("shift");
			for (int i = Mid.length()-1; i >= 1; i--){
				// In here, I need to build a temp variable and shift each and every byte
				byte temp = mid[i]; // copies last element into byte
				temp = (byte)((temp >>> 1) & 0x7F); // shifts right by one
				byte tmp = (byte)(mid[i-1] & 0x01); // gets just the last bit of the previous byte
				tmp = (byte)(tmp << 7);
				temp = (byte)((char)temp | (char)tmp);
				mid[i] = temp;
			}
			mid[0] = (byte)(mid[0]>>1); // catches the last one!
			Mid = new HeftyInteger(mid); // resets mid, just like we divided by 2
			//System.out.print(".");
			//System.out.print("ing\n");

			// System.out.print("\tmid: "+new BigInteger(Mid.getVal()).toString());
			// System.out.print("\thi: "+new BigInteger(High.getVal()).toString());
			// System.out.print("\tlo: "+new BigInteger(Low.getVal()).toString());



			// TRY TO ENSURE THAT THE SIZES ARE ONLY AS BIG AS WE NEED!!!!!
			int size = Mid.length();
			boolean flag = false;
			for (int i = 0; i < Mid.length(); i++){
				// check from index 0 up to whenever it is NON-ZERO
				// then, if less than 1, need to decrement i
				// otherwise, i is good to remove
				//copy back in :-)
				if (Mid.getVal()[i] != 0){
					if (Mid.getVal()[i] < 0){
						//if (i == 0) break;
						size = i-1;
						flag = true;
						break;
					} else {
						size = i;
						flag = true;
						break;
					}
				}
			}
			if (flag){
				byte[] temp = new byte[Mid.length()-size];
				mid = Mid.getVal();
				for (int i = size; i < Mid.length(); i++){
					// starting at Mid[size], copy into NEW byte[] starting at 0
					temp[i-size] = mid[i];
				}
				mid = temp;
				Mid = new HeftyInteger(mid);
			}
			size = High.length();
			flag = false;
			for (int i = 0; i < High.length(); i++){
				// check from index 0 up to whenever it is NON-ZERO
				// then, if less than 1, need to decrement i
				// otherwise, i is good to remove
				//copy back in :-)
				if (High.getVal()[i] != 0){
					if (High.getVal()[i] < 0){
						//if (i == 0) break;
						size = i-1;
						flag = true;
						break;
					} else {
						size = i;
						flag = true;
						break;
					}
				}
			}
			if (flag){
				byte[] temp = new byte[High.length()-size];
				mid = High.getVal();
				for (int i = size; i < High.length(); i++){
					// starting at Mid[size], copy into NEW byte[] starting at 0
					temp[i-size] = mid[i];
				}
				mid = temp;
				High = new HeftyInteger(mid);
			}
			size = Low.length();
			flag = false;
			for (int i = 0; i < Low.length(); i++){
				// check from index 0 up to whenever it is NON-ZERO
				// then, if less than 1, need to decrement i
				// otherwise, i is good to remove
				//copy back in :-)
				if (Low.getVal()[i] != 0){
					if (Low.getVal()[i] < 0){
						//if (i == 0) break;
						size = i-1;
						flag = true;
						break;
					} else {
						size = i;
						flag = true;
						break;
					}
				}
			}
			if (flag){
				byte[] temp = new byte[Low.length()-size];
				mid = Low.getVal();
				for (int i = size; i < Low.length(); i++){
					// starting at Mid[size], copy into NEW byte[] starting at 0
					temp[i-size] = mid[i];
				}
				mid = temp;
				Low = new HeftyInteger(mid);
			}
			size = a.length();
			flag = false;
			for (int i = 0; i < a.length(); i++){
				// check from index 0 up to whenever it is NON-ZERO
				// then, if less than 1, need to decrement i
				// otherwise, i is good to remove
				//copy back in :-)
				if (a.getVal()[i] != 0){
					if (a.getVal()[i] < 0){
						//if (i == 0) break;
						size = i-1;
						flag = true;
						break;
					} else {
						size = i;
						flag = true;
						break;
					}
				}
			}
			if (flag){
				byte[] temp = new byte[a.length()-size];
				mid = a.getVal();
				for (int i = size; i < a.length(); i++){
					// starting at Mid[size], copy into NEW byte[] starting at 0
					temp[i-size] = mid[i];
				}
				mid = temp;
				a = new HeftyInteger(mid);
			}
			size = b.length();
			flag = false;
			for (int i = 0; i < b.length(); i++){
				// check from index 0 up to whenever it is NON-ZERO
				// then, if less than 1, need to decrement i
				// otherwise, i is good to remove
				//copy back in :-)
				if (b.getVal()[i] != 0){
					if (b.getVal()[i] < 0){
						//if (i == 0) break;
						size = i-1;
						flag = true;
						break;
					} else {
						size = i;
						flag = true;
						break;
					}
				}
			}
			if (flag){
				byte[] temp = new byte[b.length()-size];
				mid = b.getVal();
				for (int i = size; i < b.length(); i++){
					// starting at Mid[size], copy into NEW byte[] starting at 0
					temp[i-size] = mid[i];
				}
				mid = temp;
				b = new HeftyInteger(mid);
			}
			
			// System.out.print("\nmid legnth is "+Mid.length());
			// System.out.print("\thi legnth is "+High.length());
			// System.out.print("\tlo legnth is "+Low.length());
			// System.out.print("\ta legnth is "+a.length());
			// System.out.print("\tb legnth is "+b.length()+"\n");


			// System.out.print("=");
			HeftyInteger temp1 = new HeftyInteger(a.subtract(b.multiply(Mid)).getVal());
			// System.out.print("=\n\n");
			HeftyInteger negtemp1 = temp1.negate();

			// System.out.println("\tAFTER: Mid is "+new BigInteger(Mid.getVal()).toString());
			if (!((temp1).isNegative()) && !(negtemp1.isNegative())) return Mid;
			//System.out.println("temp1 is "+new BigInteger(temp1.getVal()).toString());
			// /System.out.println("MID is "+new BigInteger(Mid.getVal()).toString());

			//System.out.print("start ");
			if (temp1.isNegative()){ // Mid is too big
				// System.out.println("Equates to "+new BigInteger(a.subtract(b.multiply(Mid)).getVal()).toString());
				High = Mid.subtract(new HeftyInteger(ONE));
				//High = Mid;
				 //System.out.println("Bringing HIGH down to "+new BigInteger(High.getVal()).toString());
			} else if (temp1.subtract(b).isNegative()) { // we're in the range! truncate by returning mid
				return Mid;
			} else if (negtemp1.isNegative()){ // Mid is too low! Bring up Low
				Low = Mid.add(new HeftyInteger(ONE));
				 //System.out.println("Bringing LOW up to "+new BigInteger(Low.getVal()).toString());
			} else { // otherwise it's 0!
				//System.out.print("end\n\n");
				return Mid;
			}
			//System.out.print("end\n\n");
		}
		//System.out.print("end\n\n");
		return Mid;
	 }















	 public HeftyInteger mod(HeftyInteger other){

		if (this.isNegative()){
			// either b-r or -r
			if (other.isNegative()){ // - - 
				// return -r
				HeftyInteger tThis = this.negate();
				HeftyInteger tOther = other.negate();
				HeftyInteger r = mod(tThis, tOther);
				return r.negate();

			} else { // - + 
				// return b-r
				HeftyInteger tThis = this.negate();
				HeftyInteger r = mod(tThis, other);
				return other.subtract(r);

			}
		} else { // THIS IS POSITIVE
			// either -(b-r) or r
			if (other.isNegative()){ // + -
				// return b+r
				HeftyInteger tOther = other.negate();
				HeftyInteger r = mod(this, tOther);
				return other.add(r);
			} else { // + + 
				// return r

				return mod(this, other);

			}
		}
	 }

	 /**
	  * This will probably be useful for the XGCD function since I can't just use the mod operation...
	  */
	 private HeftyInteger mod(HeftyInteger a, HeftyInteger b){
		byte[] low = new byte[1];
		HeftyInteger Low = new HeftyInteger(low);
		byte[] high = a.getVal();
		HeftyInteger High = new HeftyInteger(high);
		if (a.subtract(b).isNegative()) return new HeftyInteger(a.getVal());
		HeftyInteger Mid = new HeftyInteger(b.getVal());
		while (!((High.subtract(Low)).isNegative())){ // if hi - low is NOT negate
			// System.out.print("BEFORE: Mid is "+new BigInteger(Mid.getVal()).toString());
			//System.out.print("|");
			Mid = High.add(Low);
			byte[] mid = Mid.getVal();
			// System.out.print("\nmid legnth is "+Mid.length());
			// System.out.print("\thi legnth is "+High.length());
			// System.out.print("\tlo legnth is "+Low.length());
			for (int i = Mid.length()-1; i >= 1; i--){
				// In here, I need to build a temp variable and shift each and every byte
				byte temp = mid[i]; // copies last element into byte
				temp = (byte)((temp >>> 1) & 0x7F); // shifts right by one
				byte tmp = (byte)(mid[i-1] & 0x01); // gets just the last bit of the previous byte
				tmp = (byte)(tmp << 7);
				temp = (byte)((char)temp | (char)tmp);
				mid[i] = temp;
			}
			mid[0] = (byte)(mid[0]>>1); // catches the last one!
			Mid = new HeftyInteger(mid); // resets mid, just like we divided by 2
			//System.out.print(".");

			// System.out.print("\tmid: "+new BigInteger(Mid.getVal()).toString());
			// System.out.print("\thi: "+new BigInteger(High.getVal()).toString());
			// System.out.print("\tlo: "+new BigInteger(Low.getVal()).toString());



			// TRY TO ENSURE THAT THE SIZES ARE ONLY AS BIG AS WE NEED!!!!!
			int size = Mid.length();
			boolean flag = false;
			for (int i = 0; i < Mid.length(); i++){
				// check from index 0 up to whenever it is NON-ZERO
				// then, if less than 1, need to decrement i
				// otherwise, i is good to remove
				//copy back in :-)
				if (Mid.getVal()[i] != 0){
					if (Mid.getVal()[i] < 0){
						//if (i == 0) break;
						size = i-1;
						flag = true;
						break;
					} else {
						size = i;
						flag = true;
						break;
					}
				}
			}
			if (flag){
				byte[] temp = new byte[Mid.length()-size];
				mid = Mid.getVal();
				for (int i = size; i < Mid.length(); i++){
					// starting at Mid[size], copy into NEW byte[] starting at 0
					temp[i-size] = mid[i];
				}
				mid = temp;
				Mid = new HeftyInteger(mid);
			}
			size = High.length();
			flag = false;
			for (int i = 0; i < High.length(); i++){
				// check from index 0 up to whenever it is NON-ZERO
				// then, if less than 1, need to decrement i
				// otherwise, i is good to remove
				//copy back in :-)
				if (High.getVal()[i] != 0){
					if (High.getVal()[i] < 0){
						//if (i == 0) break;
						size = i-1;
						flag = true;
						break;
					} else {
						size = i;
						flag = true;
						break;
					}
				}
			}
			if (flag){
				byte[] temp = new byte[High.length()-size];
				mid = High.getVal();
				for (int i = size; i < High.length(); i++){
					// starting at Mid[size], copy into NEW byte[] starting at 0
					temp[i-size] = mid[i];
				}
				mid = temp;
				High = new HeftyInteger(mid);
			}
			size = Low.length();
			flag = false;
			for (int i = 0; i < Low.length(); i++){
				// check from index 0 up to whenever it is NON-ZERO
				// then, if less than 1, need to decrement i
				// otherwise, i is good to remove
				//copy back in :-)
				if (Low.getVal()[i] != 0){
					if (Low.getVal()[i] < 0){
						//if (i == 0) break;
						size = i-1;
						flag = true;
						break;
					} else {
						size = i;
						flag = true;
						break;
					}
				}
			}
			if (flag){
				byte[] temp = new byte[Low.length()-size];
				mid = Low.getVal();
				for (int i = size; i < Low.length(); i++){
					// starting at Mid[size], copy into NEW byte[] starting at 0
					temp[i-size] = mid[i];
				}
				mid = temp;
				Low = new HeftyInteger(mid);
			}
			size = a.length();
			flag = false;
			for (int i = 0; i < a.length(); i++){
				// check from index 0 up to whenever it is NON-ZERO
				// then, if less than 1, need to decrement i
				// otherwise, i is good to remove
				//copy back in :-)
				if (a.getVal()[i] != 0){
					if (a.getVal()[i] < 0){
						//if (i == 0) break;
						size = i-1;
						flag = true;
						break;
					} else {
						size = i;
						flag = true;
						break;
					}
				}
			}
			if (flag){
				byte[] temp = new byte[a.length()-size];
				mid = a.getVal();
				for (int i = size; i < a.length(); i++){
					// starting at Mid[size], copy into NEW byte[] starting at 0
					temp[i-size] = mid[i];
				}
				mid = temp;
				a = new HeftyInteger(mid);
			}
			size = b.length();
			flag = false;
			for (int i = 0; i < b.length(); i++){
				// check from index 0 up to whenever it is NON-ZERO
				// then, if less than 1, need to decrement i
				// otherwise, i is good to remove
				//copy back in :-)
				if (b.getVal()[i] != 0){
					if (b.getVal()[i] < 0){
						//if (i == 0) break;
						size = i-1;
						flag = true;
						break;
					} else {
						size = i;
						flag = true;
						break;
					}
				}
			}
			if (flag){
				byte[] temp = new byte[b.length()-size];
				mid = b.getVal();
				for (int i = size; i < b.length(); i++){
					// starting at Mid[size], copy into NEW byte[] starting at 0
					temp[i-size] = mid[i];
				}
				mid = temp;
				b = new HeftyInteger(mid);
			}
			
			// System.out.print("\nmid legnth is "+Mid.length());
			// System.out.print("\thi legnth is "+High.length());
			// System.out.print("\tlo legnth is "+Low.length()+"\n");



			HeftyInteger temp1 = new HeftyInteger(a.subtract(b.multiply(Mid)).getVal());
			//System.out.print("=");
			HeftyInteger negtemp1 = temp1.negate();

			// System.out.println("\tAFTER: Mid is "+new BigInteger(Mid.getVal()).toString());
			if (!((temp1).isNegative()) && !(negtemp1.isNegative())) return new HeftyInteger(new byte[1]);
			if (temp1.isNegative()){ // Mid is too big
				// System.out.println("Equates to "+new BigInteger(a.subtract(b.multiply(Mid)).getVal()).toString());
				High = Mid.subtract(new HeftyInteger(ONE));
				// System.out.println("Bringing HIGH down to "+new BigInteger(High.getVal()).toString());
			} else if (temp1.subtract(b).isNegative()){ // found our remainder!
				return temp1;
			} else if (negtemp1.isNegative()){ // Mid is too low! Bring up Low
				Low = Mid.add(new HeftyInteger(ONE));
				// System.out.println("Bringing LOW up to "+new BigInteger(Low.getVal()).toString());
			} else { // otherwise it's 0!
				return new HeftyInteger(new byte[1]);
			}

		}

		return Mid;
	 }
}
