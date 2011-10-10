package com.terrain;

import java.io.IOException;

public interface Saveable {

    void mark();

    boolean hasChanged();

    void save() throws IOException;
}