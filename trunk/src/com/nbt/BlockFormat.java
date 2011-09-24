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

package com.nbt;

import java.util.HashMap;
import java.util.Map;

public class BlockFormat {

    private static BlockFormat instance;

    public static BlockFormat getInstance() {
	if (instance == null)
	    instance = new BlockFormat();
	return instance;
    }

    private Map<Integer, BlockID> map;

    private BlockFormat() {
	this.map = new HashMap<Integer, BlockID>();

	for (int z = BlockID.MIN_Z; z < BlockID.MAX_Z; z++) {
	    for (int x = BlockID.MIN_X; x < BlockID.MAX_X; x++) {
		for (int y = BlockID.MIN_Y; y < BlockID.MAX_Y; y++) {
		    int id = y
			    + (z * BlockID.MAX_Y + (x * BlockID.MAX_Y * BlockID.MAX_Z));
		    map.put(id, new BlockID(x, y, z));
		}
	    }
	}
    }

    public BlockID getBlockID(int index) {
	return map.get(index);
    }

}
