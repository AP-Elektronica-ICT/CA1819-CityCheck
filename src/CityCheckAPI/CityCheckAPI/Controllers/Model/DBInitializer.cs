using System.Linq;

namespace Model
{

    public class DBInitializer
    {

        public static void Initialize(CityCheckContext context)
        {
            //create the db is not exist yet
            context.Database.EnsureCreated();

            //zijn er al athletes?
            ////if (!context.setnaam.Any())
            //{


            //}



        }
    }
}