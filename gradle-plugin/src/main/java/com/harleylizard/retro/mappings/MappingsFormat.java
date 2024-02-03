package com.harleylizard.retro.mappings;

public enum MappingsFormat {
    PROGUARD(new ProguardWriter()),
    REOBFUSCATED_TINY(new ReobfuscatedTinyWriter())
    ;

    private final MappingsWriter writer;

    MappingsFormat(MappingsWriter writer) {
        this.writer = writer;
    }

    public MappingsWriter getWriter() {
        return writer;
    }
}
