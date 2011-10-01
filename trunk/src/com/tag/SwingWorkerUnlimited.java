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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.SwingWorker;

/**
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6880336
 */
public class SwingWorkerUnlimited {

    private static Executor pool;
    static {
	pool = Executors.newCachedThreadPool();
    }

    private SwingWorkerUnlimited() {
	super();
    }

    public static <T, V> void execure(SwingWorker<T, V> swingWorker) {
	pool.execute(swingWorker);
    }

}