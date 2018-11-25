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


    }
}
