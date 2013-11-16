


var Heute=new Date();

var DieserMonatstag = Heute.getDate();
var DieserWochentag = Heute.getDay();
var DieserMonatNummmer = Heute.getMonth()+1;
var DiesesJahr4stellig = Heute.getFullYear();

// zweistellige Darstellung

var DieserMonatstag2 = ((DieserMonatstag<10) ? "0" : "")+ DieserMonatstag;
var DieserMonatNummmer2 = ((DieserMonatNummmer<10) ? "0" : "")+ DieserMonatNummmer;

function TagTxt (zahl) {
var Tag=new Array();
Tag[0]="Sonntag,";
Tag[1]="Montag,";
Tag[2]="Dienstag,";
Tag[3]="Mittwoch,";
Tag[4]="Donnerstag,";
Tag[5]="Freitag,";
Tag[6]="Samstag,";
return Tag[zahl];
}
var TagName=TagTxt(DieserWochentag);



function TagTxtKurz (zahl) {
var TageK = new Array ("So", "Mo", "Di", "Mi", "Do", "Fr", "Sa");
return TageK[zahl];
}
var TagNameKurz=TagTxtKurz(DieserWochentag);



function MonatTxt (zahl) {
var Monat=new Array();
Monat[1]="Jan";
Monat[2]="Feb";
Monat[3]="Mär";
Monat[4]="Apr";
Monat[5]="Mai";
Monat[6]="Jun";
Monat[7]="Jul";
Monat[8]="Aug";
Monat[9]="Sep";
Monat[10]="Okt";
Monat[11]="Nov";
Monat[12]="Dez";
return Monat[zahl];
}
var MonatName=MonatTxt(DieserMonatNummmer);


function MonatTxtVoll (zahl) {
var Monat=new Array();
Monat[1]="Januar";
Monat[2]="Februar";
Monat[3]="März";
Monat[4]="April";
Monat[5]="Mai";
Monat[6]="Juni";
Monat[7]="Juli";
Monat[8]="August";
Monat[9]="September";
Monat[10]="Oktober";
Monat[11]="November";
Monat[12]="Dezember";
return Monat[zahl];
}
var MonatNameVoll=MonatTxtVoll(DieserMonatNummmer);





function Datum(Zahl) {

var heute ="";

if(Zahl == 1) {
heute = TagNameKurz + ". "  + DieserMonatstag + ". " + MonatNameVoll + " " + DiesesJahr4stellig ;
}
if(Zahl == 2) {
heute = TagName + " "  + DieserMonatstag + ". " + MonatNameVoll + " " + DiesesJahr4stellig ;
}
if(Zahl == 3) {
heute = TagNameKurz + ". "  + DieserMonatstag + ". " + DieserMonatNummmer + ". " + DiesesJahr4stellig ;
}
if(Zahl == 4) {
heute = TagNameKurz + ". "  + DieserMonatstag + ". " + MonatNameVoll ;
}
if(Zahl == 5) {
heute = DieserMonatstag + ". " + MonatName + ". " + DiesesJahr4stellig ;
}

if(Zahl == 6) {
heute = DieserMonatstag + ". " + DieserMonatNummmer + ". " + DiesesJahr4stellig ;
}

if(Zahl == 7) {
heute = TagNameKurz + ". "  + DieserMonatstag2 + "." + DieserMonatNummmer2 + ". " + DiesesJahr4stellig ;
}

if(Zahl == 8) {
heute = DieserMonatstag2 + "." + DieserMonatNummmer2 + "." + DiesesJahr4stellig ;
}

document.write(heute);
}
