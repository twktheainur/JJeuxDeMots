package org.jeuxdemots.examples;

import org.apache.commons.lang3.tuple.Pair;
import org.jeuxdemots.model.api.graph.AccessException;
import org.jeuxdemots.model.api.graph.JeuxDeMots;
import org.jeuxdemots.model.api.lexical.JDMLexicalAspect;
import org.jeuxdemots.model.api.lexical.JDMLexicalEntry;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LexicalAspect {

    public static void main(String... args) throws SQLException, IOException {


        final String jdbcUrl = "jdbc:mysql://root@localhost:3307/jdm";
        final String user = "root";
        final String password = "totototo";

        final JeuxDeMots jeuxDeMots = JeuxDeMots.createFromSQL(jdbcUrl, user, password);
        final JDMLexicalAspect lexicalAspect = jeuxDeMots.getLexicalAspect();

        JDMLexicalEntry lexicalEntry = lexicalAspect.getLexicalEntry("voiture")
                .orElseThrow(() -> new AccessException(""));

        System.out.println(lexicalEntry);

        System.out.println("Domain: " + lexicalEntry.getDomain());
        System.out.println("POS: " + lexicalEntry.getPosTag());
        List<Pair<JDMLexicalEntry, Double>> sentiment = lexicalEntry.getSentiment();
        for (Pair<JDMLexicalEntry, Double> pair : sentiment) {
            System.out.println(pair.getKey() + " [" + String.valueOf(pair.getValue()) + "]");
        }


    }
}
