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

package resources;

import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.MissingResourceException;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

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
	    Validate.notNull(name, "name must not be null");
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