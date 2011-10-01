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

import org.apache.commons.lang3.StringUtils;

public class HexUtils {

    private HexUtils() {

    }

    public static int unsigned(byte b) {
	return 0xFF & b;
    }

    public static String toHex(byte b) {
	int i = unsigned(b);
	return toHex(i);
    }

    public static String toHex(int i) {
	String hex = Integer.toHexString(i);
	int size = 2;
	hex = StringUtils.leftPad(hex, size, '0');
	hex = hex.toUpperCase();
	int length = hex.length();
	int beginIndex = length - size;
	return hex.substring(beginIndex);
    }

}
