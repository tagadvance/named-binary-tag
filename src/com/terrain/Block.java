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

package com.terrain;

public interface Block {

    public static final int MIN_X = 0, MAX_X = 16, MIN_Z = 0, MAX_Z = 16,
	    MIN_Y = 0, MAX_Y = 128;

    int getLocalX();

    int getLocalZ();

    int getAltitude();

    int getAbsoluteX();

    int getAbsoluteZ();

    int getBlockID();

    int getData();

    int getSkyLight();

    int getBlockLight();

    String getName();

}
