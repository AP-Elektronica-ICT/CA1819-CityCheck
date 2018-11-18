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


        public List<DoelLocatie> getAllLocs()
        {

            List<DoelLocatie> doelen = context.DoelLocaties.Include(r=>r.locatie).ToList<DoelLocatie>();
            if (doelen != null)
                return doelen;
            else
                return null;
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
