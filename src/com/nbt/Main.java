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

import java.awt.EventQueue;
import java.io.File;

import javax.swing.UIManager;

public class Main {

	public static void main(final String[] args) {
		//setPreferredLookAndFeel();
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				GUI gui = new GUI();
				gui.setVisible(true);

				if (args.length > 0) {
					File file = new File(args[0]);
					if (file.canRead())
						gui.doImport(file);
				}
			}

		});
	}

	@SuppressWarnings("unused")
	private static void setPreferredLookAndFeel() {
		String[] lafs = {
				//"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel",
				UIManager.getSystemLookAndFeelClassName(),
				UIManager.getCrossPlatformLookAndFeelClassName()
		};
		for (String laf : lafs) {
			try {
				UIManager.setLookAndFeel(laf);
				break;
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

}