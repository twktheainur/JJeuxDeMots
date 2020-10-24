package org.jeuxdemots.model.api.graph;

@SuppressWarnings("PublicMethodNotExposedInInterface")
public enum NodeType {
    UNDEFINED(-1, ""), JUNK(-1, "n_junk"), GENERIC(0, "n_generic"), TERM(1, "n_term"), FORM(2, "n_forme"), DEFINITION(3, "n_definition"),
    POS(4, "n_pos"), CONCEPT(5, "n_concept"), FLPOT(6, "n_flpot"), HUB(7, "n_hub "), CHUNK(8, "n_chunk"),
    QUESTION(9, "n_question"), RELATION(10, "n_relation"), RULE(11, "r_rule"), ANALOGY(12, "n_analogy"),
    COMMANDS(13, "n_commands"), SYNT_FUNCTION(14, "f_synt_function"), DATA(18, "n_data"), DATA_POT(36, "n_data_pot"),
    LINK(444, "n_link"), AKI(666, "n_AKI"), WIKIPEDIA(777, "n_wikipedia");

    private final int code;
    private final String name;

    NodeType(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public static NodeType fromCode(final int code) throws InvalidNodeTypeCodeException {
        final NodeType[] types = NodeType.values();
        int index = 0;
        int currentCode = types[0].getCode();
        while ((index < types.length) && (currentCode != code)) {
            index++;
            currentCode = types[index].getCode();
        }
        if (index < types.length) {
            return types[index];
        } else {
            throw new InvalidNodeTypeCodeException(String.valueOf(code));
        }
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }
}
