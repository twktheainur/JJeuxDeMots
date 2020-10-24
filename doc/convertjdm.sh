
#!/bin/bash

DUMPFILE=$1

rtstart=$(grep "// ---- RELATION TYPES" $DUMPFILE -n -m1 | cut -d':' -f1)
rtend=$(grep "// ---- NODE TYPES" $DUMPFILE -n -m1 | cut -d':' -f1)
nstart=$(grep "// -- NODES" $DUMPFILE -n -m1 | cut -d':' -f1)
rstart=$(grep "// -- RELATIONS" $DUMPFILE -n -m1 | cut -d':' -f1)
rend=$(wc -l $DUMPFILE | cut -f1 -d' ')

#Relation Types
# rtid=0|name="r_associated"|nom_etendu="id<E9>e associ<E9>e"|info="Il est demand<E9> d'<E9>num<E9>rer les termes les plus <E9>troitement associ<E9>s au mot cible... Ce mot vous fait penser <E0> quoi ?"
head -$(expr $rtend - 2) $DUMPFILE | tail -n $(expr $rtend - $rtstart - 3) | sed 's/rtid=\([0-9]*\)|name=\("[^"]*"\)|nom_etendu=\("[^"]*"\)|info=\("[^"]*"\)\|t=\([0-9]*\)/\1;\2;\3;\4/g' > relation_types.csv

#Nodes
head -$(expr $rstart - 1) $DUMPFILE | tail -n $(expr $rstart - $nstart - 2) | sed 's/eid=\([0-9]*\)|n=\("[^"]*"\)|t=\([0-9]*\)|w=\([0-9]*\)\(|nf=\("[^"]*"\)\)\{0,1\}/\1;\2;\3;\4;\5/g' > nodes.csv

#Relations

echo "Extracting relations" 
echo "$rstart"
echo "$rend"
tail -n$(expr $rend - $rstart) $DUMPFILE | sed 's/rid=\([0-9]*\)|n1=\([0-9]*\)|n2=\([0-9]*\)|t=\([0-9]*\)|w=\([0-9]*\)/\1;\2;\3;\4;\5/g' > relations.csv

