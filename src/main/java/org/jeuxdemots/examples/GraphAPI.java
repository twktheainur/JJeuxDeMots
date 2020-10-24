package org.jeuxdemots.examples;

import org.jeuxdemots.model.api.graph.JDMNode;
import org.jeuxdemots.model.api.graph.JDMRelation;
import org.jeuxdemots.model.api.graph.JDMRelationType;
import org.jeuxdemots.model.api.graph.JeuxDeMots;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public final class GraphAPI {

    public static void main(String... args) throws SQLException, IOException {


        final String jdbcUrl = "jdbc:mysql://root@localhost:3307/jdm";
        final String user = "root";
        final String password = "totototo";

        final JeuxDeMots jeuxDeMots = JeuxDeMots.createFromSQL(jdbcUrl, user, password);


        Optional<JDMNode> voiture = jeuxDeMots.getNode("voiture");
        Optional<JDMRelationType> synonym = jeuxDeMots.findType("r_syn");
        if (voiture.isPresent()) {
            if (synonym.isPresent()) {
                Collection<JDMRelation> synonyms = jeuxDeMots.getOutgoingRelations(synonym.get(), voiture.get());
                for (JDMRelation relation : synonyms) {
                    int target = relation.getTargetId().intValue();
                    Optional<JDMNode> targetNode = jeuxDeMots.getNode(target);
                    targetNode.ifPresent(n -> System.out.println("SYN:" + n.getName()));
                }

                Collection<JDMRelation> isSynonymOf = jeuxDeMots.getIncomingRelations(synonym.get(), voiture.get());

                for (JDMRelation relation : isSynonymOf) {
                    int source = relation.getSourceId().intValue();
                    Optional<JDMNode> sourceNode = jeuxDeMots.getNode(source);
                    sourceNode.ifPresent(n -> System.out.println("SYN OF:" + n.getName()));
                }
            }

            Optional<JDMRelationType> raff = jeuxDeMots.findType("r_raff_sem");

            if (raff.isPresent()) {
                Collection<JDMRelation> senseRelations = jeuxDeMots.getOutgoingRelations(raff.get(), voiture.get());

                System.out.println("Senses of " + voiture.get().getName());

                for (JDMRelation relation : senseRelations) {
                    int target = relation.getTargetId().intValue();
                    Optional<JDMNode> senseNode = jeuxDeMots.getNode(target);
                    senseNode.ifPresent(jdmNode -> System.out.println("\t>SENSE :" + jdmNode.getName()));
                }
            }


        }
    }
}
