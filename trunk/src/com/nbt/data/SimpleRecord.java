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

package com.nbt.data;

public class SimpleRecord implements Record {

    private static final int EXPECTED_LENGTH = 3;
    private static final int INDEX_NAME = 0, INDEX_ID = 1, INDEX_ICON = 2;

    private final String name;
    private final int id, iconIndex;

    /**
     * 
     * @param row
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     */
    public SimpleRecord(String[] row) {
	if (row.length < EXPECTED_LENGTH)
	    throw new IllegalArgumentException("row is too short");
	this.name = row[INDEX_NAME].trim();
	this.id = Integer.parseInt(row[INDEX_ID].trim());
	this.iconIndex = Integer.parseInt(row[INDEX_ICON].trim());
    }

    @Override
    public String getName() {
	return this.name;
    }

    @Override
    public int getID() {
	return this.id;
    }

    @Override
    public int getIconIndex() {
	return this.iconIndex;
    }

    public String toString() {
	return getName() + ", " + getID() + ", " + getIconIndex();
    }

}