package com.harleylizard.retro.mappings;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public sealed interface MappingsWriter permits ProguardWriter, ReobfuscatedTinyWriter {

    void write(Path cache, Writer writer, Mappings<Mappings<String>> mappings) throws IOException;
}
