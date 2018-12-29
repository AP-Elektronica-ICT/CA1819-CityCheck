using DataLayer;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BussinessLayer.Methods
{
    public class DoelMethod
    {


        private CityCheckContext context;

        public DoelMethod(CityCheckContext context)
        {
            this.context = context;
        }



        //methods---------------


        public List<DoelLocatie> getAllLocs(string Naam, int? page, int? pageLength, string direction)
        {
            //alle doellocaties selecteren
            IQueryable<DoelLocatie> doelen = context.DoelLocaties.Include(r=>r.locatie).Include(loc => loc.Vragen).ThenInclude(ant => ant.Antwoorden);

            //eerst kijken of we een bepaalde loc opvragen volgens naam
            if (!string.IsNullOrWhiteSpace(Naam))
            {  //allDoelLocs?naam=mas (vb)
                doelen = doelen.Where(d => d.Titel == Naam);
                return doelen.ToList();
            }


            if (page.HasValue)
            {   //allDoelLocs?page=1
                doelen = doelen.Skip(page.Value * pageLength.Value);

            }

            //altijd maar 5 mee geven
            doelen = doelen.Take(pageLength.Value);

            //query returnen in list formaat
            return doelen.ToList();

        }



        public bool addLoc(DoelLocatie newDoel)
        {

            //DoelLocatie doel = context.DoelLocaties.Where(r=> r==newDoel).Include(r=>r.locatie).Single<DoelLocatie>();

            if (newDoel != null)
            {
                context.DoelLocaties.Add(newDoel);
                context.SaveChanges();
                return true;
            }
            else
                return false;
        }


        public bool editLoc(DoelLocatie newDoel, int id)
        {

            if (newDoel != null)
            {
                //doellocatie zoeken
                DoelLocatie huidigeLoc = context.DoelLocaties.Include(r=> r.locatie).Include(vr => vr.Vragen).ThenInclude(y => y.Antwoorden).Where(r=>r.Id == id).Single<DoelLocatie>();
                //Locatie titel en locatie aanpassen
                huidigeLoc.Titel = newDoel.Titel;
                //context.Locaties.Remove(huidigeLoc.locatie);
                huidigeLoc.locatie.Lat = newDoel.locatie.Lat;
                huidigeLoc.locatie.Long = newDoel.locatie.Long;

                //edits opslaan
                context.SaveChanges();
                return true;
            }
            else
                return false;


        }



        public bool delLoc(int id)
        {

            DoelLocatie doelToDeleted = context.DoelLocaties.Find(id);
            if (doelToDeleted != null)
            {
                //Delete location
                context.DoelLocaties.Remove(doelToDeleted);
                context.SaveChanges();
                return true;
            }
            else
                return false;
        }


        public bool addQuest(Vraag newVraag, int id)
        {
            //id is de id van de doellocatie

            DoelLocatie doel = context.DoelLocaties.Include(r=> r.Vragen).ThenInclude(y => y.Antwoorden).Where(r=>r.Id == id).Single<DoelLocatie>();

            if (doel != null && newVraag != null)
            {
                if (doel.Vragen != null)
                {
                    //nieuwe vraag opslaan
                    doel.Vragen.Add(newVraag);
                } else
                {
                    doel.Vragen = new List<Vraag>();
                    doel.Vragen.Add(newVraag);
                }
                context.SaveChanges();
                return true;
            }
            else
                //geen vraag
                return false;
        }


        public List<Vraag> getAllLocsQuest(int id)
        {
            //alle vragen ophalen op 1 locatie inclusief antwoorden
            DoelLocatie doelenQuery = context.DoelLocaties.Include(r => r.Vragen).ThenInclude(y => y.Antwoorden).Where(r=>r.Id == id).Single<DoelLocatie>();
            List<Vraag> vragenQuery = doelenQuery.Vragen.ToList<Vraag>();


            if (vragenQuery != null)
                return vragenQuery;
            else
                return null;
        }


        public bool delQuest(int id, int vid)
        {
            //id is de id van de doellocatie


            DoelLocatie doel = null;
            Vraag vraag = null;
            List<Antwoord> antwoorden = null;


            try
            {
                doel = context.DoelLocaties.Include(vr => vr.Vragen).ThenInclude(antw => antw.Antwoorden).Where(whr => whr.Id == id).Single<DoelLocatie>();
                vraag = doel.Vragen.Where(r => r.Id == vid).Single<Vraag>();
                antwoorden = vraag.Antwoorden;
            }
            catch
            {
                Console.WriteLine("Geen Content");
            }

            if (vraag != null)
            {

                context.Antwoorden.RemoveRange(antwoorden);
                context.Vragen.Remove(vraag);
                context.SaveChanges();

                return (true);
            }
            else {
                return (false);
            }
        }


        public Vraag getALocQuest(int id)
        {
            //id is de doellocatie id

            //vragen ophalen
            DoelLocatie doel = context.DoelLocaties.Include(r => r.Vragen).ThenInclude(y => y.Antwoorden).Where(r => r.Id == id).Single<DoelLocatie>();
            List<Vraag> vragen = doel.Vragen;
            //aantal vragen
            int vragenAmount = vragen.Count;


            //random vraag positie bepalen
            Random rand = new Random();
            int randomVraagPos = rand.Next(0, vragenAmount);
            Vraag vraag = vragen[randomVraagPos];
            if (vraag != null)
                return vraag;
            else
                return null;
        }




        public bool claimQuest(int id, int locid)
        {
            //id is de gameID

            Game game = context.Games.Where(r => r.GameCode == id).Include(r=>r.GameDoelen).SingleOrDefault<Game>();
            GameDoelen doel = game.GameDoelen.Where(r => r.Id == locid).SingleOrDefault<GameDoelen>();
            if (doel != null)
            {
                doel.Claimed = true;
                context.SaveChanges();
                return true;
            }
            else
                return false;

        }


    }
}
