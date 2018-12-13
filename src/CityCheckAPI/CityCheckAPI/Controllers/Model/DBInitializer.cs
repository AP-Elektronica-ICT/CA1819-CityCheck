using DataLayer;
using System.Linq;

namespace Model
{

    public class DBInitializer
    {

        public static void Initialize(CityCheckContext context)
        {
            //create the db is not exist yet
            context.Database.EnsureCreated();

            //Default wat doellocaties al in de db steken
            //zijn er al doellocaties?
            if (!context.DoelLocaties.Any())
            {
                //er zijn nog geen doellocaties
                //We steken er standaard een aantal belangrijke in.

                var doel = new DoelLocatie();
                var loc = new Locatie();

                //Antwoorden instellen
                var ant1 = new Antwoord();
                ant1.CorrectBool = false;
                ant1.Antwoordzin = "Groenplaats";
                var ant2 = new Antwoord();
                ant2.CorrectBool = true;
                ant2.Antwoordzin = "Eilandje";
                var ant3 = new Antwoord();
                ant3.CorrectBool = false;
                ant3.Antwoordzin = "Rooseveld";

                //Vraag toevoegen
                var ques = new Vraag();
                ques.VraagZin = "Waar zijn we?";
                ques.Antwoorden = new System.Collections.Generic.List<Antwoord>();
                ques.Antwoorden.Add(ant1);
                ques.Antwoorden.Add(ant2);
                ques.Antwoorden.Add(ant3);

                //mas
                doel.Titel = "Mas";
                loc.Lat = 51.2289238;
                loc.Long = 4.4026316;
                doel.locatie = loc;
                doel.Vragen = new System.Collections.Generic.List<Vraag>();
                doel.Vragen.Add(ques);
                context.DoelLocaties.Add(doel);
                context.SaveChanges();

                //centraal station
                doel = new DoelLocatie();
                loc = new Locatie();
                doel.Titel = "Centraal Station";
                loc.Lat = 51.2183305;
                loc.Long = 4.4204524;
                doel.locatie = loc;
                context.DoelLocaties.Add(doel);
                context.SaveChanges();

                //kathedraal
                doel = new DoelLocatie();
                loc = new Locatie();
                doel.Titel = "OlvKathedraal";
                loc.Lat = 51.2202678;
                loc.Long = 4.399327;
                doel.locatie = loc;
                context.DoelLocaties.Add(doel);
                context.SaveChanges();

                //het steen
                doel = new DoelLocatie();
                loc = new Locatie();
                doel.Titel = "Steen";
                loc.Lat = 51.2227238;
                loc.Long = 4.395175;
                doel.locatie = loc;
                context.DoelLocaties.Add(doel);
                context.SaveChanges();

                //Hendrik Conscienceplein
                doel = new DoelLocatie();
                loc = new Locatie();
                doel.Titel = "Hendrik Conscienceplein";
                loc.Lat = 51.2211204;
                loc.Long = 4.4021283;
                doel.locatie = loc;
                context.DoelLocaties.Add(doel);
                context.SaveChanges();


                //Groenplaats
                doel = new DoelLocatie();
                loc = new Locatie();
                doel.Titel = "Groenplaats";
                loc.Lat = 51.2189511;
                loc.Long = 4.3989123;
                doel.locatie = loc;
                context.DoelLocaties.Add(doel);
                context.SaveChanges();



                //Boerentoren
                doel = new DoelLocatie();
                loc = new Locatie();
                doel.Titel = "Boerentoren";
                loc.Lat = 51.2185463;
                loc.Long = 4.4042931;
                doel.locatie = loc;
                context.DoelLocaties.Add(doel);
                context.SaveChanges();

            //if
            }



        }
    }
}