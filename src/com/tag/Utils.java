/**
 * Copyright 2011 Taggart Spilman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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

    // TODO: test this
    public static int getLow(byte b) {
	return (b & 0xF);
    }

    // TODO: test this
    public static int getHigh(byte b) {
	return (b >>> 4) & 0xF;
    }

}