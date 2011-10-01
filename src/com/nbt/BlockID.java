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

package com.nbt;

public class BlockID {

    public static final int MIN_X = 0, MAX_X = 16, MIN_Y = 0, MAX_Y = 128,
	    MIN_Z = 0, MAX_Z = 16;

    private int x, y, z;

    public BlockID() {
	this(MIN_X, MIN_Y, MIN_Z);
    }

    public BlockID(int x, int y, int z) {
	setX(x);
	setY(y);
	setZ(z);
    }

    public int getX() {
	return x;
    }

    public void setX(int x) {
	if (x < MIN_X || x > MAX_X)
	    throw new IllegalArgumentException(MAX_X + " >= " + x + " >= "
		    + MIN_X);
	this.x = x;
    }

    public int getY() {
	return y;
    }

    public void setY(int y) {
	if (y < MIN_Y || y > MAX_Y)
	    throw new IllegalArgumentException(MAX_Y + " >= " + y + " >= "
		    + MIN_Y);
	this.y = y;
    }

    public int getZ() {
	return z;
    }

    public void setZ(int z) {
	if (z < MIN_Z || z > MAX_Z)
	    throw new IllegalArgumentException(MAX_Z + " >= " + z + " >= "
		    + MIN_Z);
	this.z = z;
    }

    public String toString() {
	return "X = " + getX() + ", Y = " + getY() + ", Z = " + getZ();
    }

}