/*
 * Copyright 2011 Taggart Spilman. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Taggart Spilman ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL Taggart Spilman OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of Taggart Spilman.
 */

package com.tag;

import java.text.NumberFormat;

public class Utils {
    
    public static void printElapsedSeconds(long start) {
	long stop = System.currentTimeMillis();
	double difference = (stop - start);
	double seconds = difference / 1000;
	NumberFormat format = NumberFormat.getNumberInstance();
	String beautified = format.format(seconds);
	System.out.println(beautified + " seconds");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void validate(Number value, Comparable minimum,
	    Comparable maximum) {
	if (value == null)
	    throw new IllegalArgumentException("value must not be null");
	else if (minimum == null)
	    throw new IllegalArgumentException("minimum must not be null");
	else if (maximum == null)
	    throw new IllegalArgumentException("maximum must not be null");
	if ((minimum.compareTo(value) > 0) || (maximum.compareTo(value) < 0))
	    throw new IllegalArgumentException("!(" + minimum + " <= " + value
		    + " <= " + maximum + ")");
    }

    // TODO: test this
    public static int getLow(byte b) {
	return (b & 0xF);
    }

    // TODO: test this
    public static int getHigh(byte b) {
	return (b >>> 4) & 0xF;
    }

}