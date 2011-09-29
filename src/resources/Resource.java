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

package resources;

import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.MissingResourceException;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import au.com.bytecode.opencsv.CSVReader;

public class Resource {

    public Resource() {

    }
    
    public List<String[]> getCSV(String name) {
	return new ResourceLoader<List<String[]>>(name) {
	    @Override
	    protected List<String[]> get(URL url) throws Exception {
		CSVReader reader = null;
		try {
		    reader = new CSVReader(new InputStreamReader(
			    url.openStream()));
		    return reader.readAll();
		} finally {
		    IOUtils.closeQuietly(reader);
		}
	    }
	}.get();
    }

    public BufferedImage getImage(String name) {
	return new ResourceLoader<BufferedImage>(name) {
	    @Override
	    protected BufferedImage get(URL url) throws Exception {
		return ImageIO.read(url);
	    }
	}.get();
    }
    
    private static abstract class ResourceLoader<V> {

	final String name;

	public ResourceLoader(String name) {
	    if (name == null)
		throw new IllegalArgumentException("name must not be null");
	    this.name = name;
	}

	public final V get() throws MissingResourceException {
	    URL url = Resource.class.getResource(name);
	    try {
		return get(url);
	    } catch (Exception e) {
		String className = Resource.class.getName();
		throw new MissingResourceException(e.getMessage(), className,
			name);
	    }
	}

	protected abstract V get(URL url) throws Exception;

    }

}