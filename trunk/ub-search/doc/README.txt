run war file in test server: jetty
mvn -DskipTests=true  jetty:run

create war; 
==========
mvn package
or 
mvn compile war:war

create exploded and packaged war; 
==========
mvn -DskipTests=true clean compile package war:exploded war:war 
 

 check here: 
 http://localhost:8080/ubsearch/ubsearch?similar=http://www.xn--untergrund-blttle-2qb.ch/archiv/ausgabe2/bangkok_blues.html
 
 or here:
 http://localhost:8080/ubsearch/
