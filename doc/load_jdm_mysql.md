# Convertion script from JeuxDeMot dump to CSV files (for database loading)

## 1. Converting from the dump to CSV

The converjdm.sh script converts the JDM dump to three CSV files: relation_types.csv; relations.csv; nodes.csv; in order to allow loading the data into an SQL database with a simple relational schema. 

The current version of the scrip should work on the gnu versions of `sed` and `grep`. If you are using _non gnu_ versions, some adaptations could be necessary, for example, on MacOS, | should be escaped from regular expressions, while on `gnu sed`, they shouldn't be escaped. 

```bash

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

```

### Usage

1. Download the latest full dump from: http://www.jeuxdemots.org/JDM-LEXICALNET-FR/

2. Make the script executable: `chmod +x ./convertjdm.sh `

3. Convert the dump to UTF-8: By default the encoding used by the JdM dump is `cp1252`(Windows) and should be converted to UTF-8, unless you are using bash on the Windows Subsystem For Linux (Windows 10.1 and later). For example:

   `iconv -f WINDOWS-1252 -t UTF-8 02042019-LEXICALNET-JEUXDEMOTS-FR-NOHTML.txt > jdm_full_utf8.txt`

4. Run the script: `./convertjdm.sh jdm_full_utf8.txt`

## 2. Loading into a database

Most SQL databases support loading CSV files in a straightforward way. We provide and example that loads the files on mysql. Please make sure to adapt the paths and to configure the database server properly (see below).

```mysql
create database jdm;
use jdm;

CREATE TABLE IF NOT EXISTS nodes (eid INT, n TEXT, t INT, w INT, nf TEXT) engine = MYISAM;
CREATE TABLE IF NOT EXISTS relations (rid INT, n1 INT, n2 INT, t INT, w INT) engine = MYISAM;
CREATE TABLE IF NOT EXISTS relation_types (rtid INT, name TEXT, nom_etendu TEXT, info TEXT) engine = MYISAM;


LOAD DATA INFILE "/var/lib/mysql-files/nodes.csv" INTO TABLE nodes CHARACTER SET 'latin1' COLUMNS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n';
LOAD DATA INFILE "/var/lib/mysql-files/relation_types.csv" INTO TABLE relation_types CHARACTER SET 'latin1' COLUMNS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n';
LOAD DATA INFILE "/var/lib/mysql-files/relations.csv" INTO TABLE relations CHARACTER SET 'latin1' COLUMNS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n’;

```

You should configure MySQL to allow the loading of data from CSV file: 

```
# On of the other depending on the version of MySQL, it is safe to put both
local-infile=ON 
local_infile=ON

# The path from which loading files is allowed, that's where the csv files should be located
datadir=/var/lib/mysql 
```

