package org.jnbt;

/*
 * JNBT License
 * 
 * Copyright (c) 2010 Graham Edgecombe All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * * Neither the name of the JNBT team nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The <code>TAG_Compound</code> tag.
 * 
 * @author Graham Edgecombe
 * @author Taggart Spilman
 * 
 */
public class CompoundTag extends Tag<Map<String, Tag<?>>> {
	
	public static final String LABEL = "TAG_Compound";
	
	public CompoundTag(String name) {
		super(name, null);
	}
	
	public CompoundTag(String name, Map<String, Tag<?>> value) {
		super(name, value);
	}

	@Override
	protected Map<String, Tag<?>> createDefaultValue() {
		return new LinkedHashMap<String, Tag<?>>();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(LABEL);
		String name = getName();
		if (!name.isEmpty())
			sb.append("(\"").append(name).append("\")");
		Map<String, Tag<?>> map = getValue();
		int size = map.size();
		sb.append(": ").append(size).append(" entries\r\n{\r\n");
		for (Map.Entry<String, Tag<?>> entry : map.entrySet()) {
			Tag<?> tag = entry.getValue();
			String s = tag.toString();
			s = s.replaceAll("\r\n", "\r\n   ");
			sb.append("   ").append(s).append("\r\n");
		}
		sb.append("}");
		return sb.toString();
	}

}