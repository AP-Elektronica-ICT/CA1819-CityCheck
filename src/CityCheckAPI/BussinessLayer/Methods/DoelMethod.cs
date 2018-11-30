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
            IQueryable<DoelLocatie> doelen = context.DoelLocaties.Include(r=>r.locatie);

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
                DoelLocatie huidigeLoc = context.DoelLocaties.Include(r=> r.locatie).Where(r=>r.Id == id).Single<DoelLocatie>();
                //Locatie titel en locatie aanpassen
                huidigeLoc.Titel = newDoel.Titel;
                huidigeLoc.locatie = newDoel.locatie;

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

            DoelLocatie doel = context.DoelLocaties.Include(r=> r.Vragen).Where(r=>r.Id == id).Single<DoelLocatie>();

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
            DoelLocatie doelenQuery = context.DoelLocaties.Include(r => r.Vragen.Select(y => y.Antwoorden)).Where(r=>r.Id == id).Single<DoelLocatie>();
            List<Vraag> vragenQuery = doelenQuery.Vragen.ToList<Vraag>();


            if (vra
genQuery != null)
                return vragenQuery;
            else
                return null;
        }



        public Vraag getALocQuest(int id)
        {
            //id is de doellocatie id

            //vragen ophalen
            DoelLocatie doel = context.DoelLocaties.Include(r => r.Vragen.Select(y => y.Antwoorden)).Where(r => r.Id == id).Single<DoelLocatie>();
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


    }
}
